package com.example.hospital.api.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.BiMap;
import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.controller.form.vo.DoctorScheduleSlotVO;
import com.example.hospital.api.db.dao.DoctorWorkPlanDao;
import com.example.hospital.api.db.dao.DoctorWorkPlanScheduleDao;
import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;
import com.example.hospital.api.exception.HospitalException;
import com.example.hospital.api.service.DoctorWorkPlanScheduleService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class DoctorWorkPlanScheduleServiceImpl implements DoctorWorkPlanScheduleService {

    @Resource
    private DoctorWorkPlanScheduleDao doctorWorkPlanScheduleDao;

    @Resource
    private DoctorWorkPlanDao doctorWorkPlanDao;

    @Resource
    private RedisTemplate redisTemplate;

//    设置缓存过期时间
    Map<String,Integer> map = new HashMap<>();
    {
        map.put("08:00", 1);
        map.put("08:30", 2);
        map.put("09:00", 3);
        map.put("09:30", 4);
        map.put("10:00", 5);
        map.put("10:30", 6);
        map.put("11:00", 7);
        map.put("11:30", 8);
        map.put("13:00", 9);
        map.put("13:30", 10);
        map.put("14:00", 11);
        map.put("14:30", 12);
        map.put("15:00", 13);
        map.put("15:30", 14);
        map.put("16:00", 15);
    }
    private BiMap<String,Integer> effectDate = new BiMap<>(map);


    @Override
    public void insert(List<DoctorWorkPlanScheduleEntity> list) {
        insertScheduleHandle(list);
        //TODO 设置Redis缓存，用于防止挂号超售

        addScheduleCache(list);
    }

    @Override
    public HashMap searchByWorkPlanId(int workPlanId) {
        ArrayList<HashMap> list = doctorWorkPlanScheduleDao.searchByWorkPlanId(workPlanId);

        HashMap result = new HashMap();
        ArrayList temp = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);
            int scheduleId = MapUtil.getInt(map, "scheduleId");
            int doctorId = MapUtil.getInt(map, "doctorId");
            int maximum = MapUtil.getInt(map, "maximum");
            int num = MapUtil.getInt(map, "num");
            int slot = MapUtil.getInt(map, "slot");

            if (i == 0) {
                result.put("doctorId", doctorId);
                result.put("maximum", maximum);
            }
            temp.add(new HashMap<>() {{
                put("scheduleId", scheduleId);
                put("slot", slot);
                put("num", num);
            }});
        }
        result.put("slots", temp);
        return result;
    }

    @Transactional
    void insertScheduleHandle(List<DoctorWorkPlanScheduleEntity> list) {
        for (DoctorWorkPlanScheduleEntity entity : list) {
            doctorWorkPlanScheduleDao.insert(entity);
        }
    }

    /**
     * 添加表缓存
     *
     * @param list 列表
     */
    private void addScheduleCache(List<DoctorWorkPlanScheduleEntity> list) {
        if (list == null || list.size() == 0) return;
        int wokPlanId = list.get(0).getWorkPlanId();
        ArrayList<HashMap> list1 = doctorWorkPlanScheduleDao.searchNewSchedule(wokPlanId);
        for (HashMap map : list1) {
            int id = MapUtil.getInt(map,"id");
            int slot = MapUtil.getInt(map,"slot");
//            定义缓存记录的 key 值
            String key = "doctor_schedule_" + id;

//           医生出诊时间缓存到 Redis
            redisTemplate.opsForHash().putAll(key,map);

//            医生出诊日期。
            String date = MapUtil.getStr(map, "date");
//          该时间段起始日期
            String time = effectDate.getKey(slot);
//          比如说 2023 - 2 - 12 12 ： 00 过期 到点自动不能挂号咯。
            redisTemplate.expireAt(key, DateUtil.parse(date+ " " + time));
        }

    }

    @Override
    public ArrayList searchDeptSubSchedule(Map param) {
        ArrayList<HashMap> list = doctorWorkPlanScheduleDao.searchDeptSubSchedule(param);
        ArrayList<HashMap> result = new ArrayList();

        int tempDoctorId = 0;
        HashMap doctor = new HashMap();

        for (HashMap map : list) {
            int doctorId = MapUtil.getInt(map, "doctorId");
            int slot = MapUtil.getInt(map, "slot");
            //如果当前记录跟上一条记录的医生不是同一个人
            if (tempDoctorId != doctorId) {
                tempDoctorId = doctorId;
                doctor = map;
                doctor.replace("slot", new ArrayList<Integer>() {{
                    add(slot);
                }});
                result.add(doctor);
            }
            //如果当前记录与上一条记录是同一个医生
            else if (tempDoctorId == doctorId) {
                ArrayList<Integer> slotList = (ArrayList) doctor.get("slot");
                slotList.add(slot);
            }
        }
        //筛选哪些时段出诊，哪些时段不出诊
        for (HashMap map : result) {
            ArrayList<Integer> slot = (ArrayList) map.get("slot");
            ArrayList tempSlot = new ArrayList();
            for (int i = 1; i <= 15; i++) {
                tempSlot.add(slot.contains(i));
            }
            map.replace("slot", tempSlot);
        }
        return result;
    }

    @Override
    public void updateSchedule(Map param) {
        //删除或者添加新的出诊时段
        ArrayList<DoctorWorkPlanScheduleEntity> addList = this.insertOrDeleteScheduleHandle(param);
        //把新添加的出诊时段缓存到Redis
        this.addScheduleCache(addList);
    }

    @Transactional
    ArrayList<DoctorWorkPlanScheduleEntity> insertOrDeleteScheduleHandle(Map param) {
        //更新计划表接诊人数上限
        doctorWorkPlanDao.updateMaximum(param);

        Integer workPlanId = MapUtil.getInt(param, "workPlanId");
        Integer maximum = MapUtil.getInt(param, "maximum");

        //待处理的时间段列表（添加或者删除）
        ArrayList<DoctorScheduleSlotVO> slots = (ArrayList<DoctorScheduleSlotVO>) param.get("slots");
        //用于存放添加时间段的列表
        ArrayList<DoctorWorkPlanScheduleEntity> addList = new ArrayList();
        //用于存放删除时间段的列表
        ArrayList<Integer> removeList = new ArrayList();

        //分析哪些是添加时间段，哪些是删除时间段
        slots.forEach(one -> {
            //判断添加记录
            if (one.getOperate().equals("insert")) {
                DoctorWorkPlanScheduleEntity entity = new DoctorWorkPlanScheduleEntity();
                entity.setWorkPlanId(workPlanId);
                entity.setMaximum(maximum);
                entity.setSlot(one.getSlot());
                addList.add(entity); //时间段保存到添加列表
            } else {
                removeList.add(one.getScheduleId()); //时间段保存到删除列表
            }
        });

        //判断删除列表是否有元素
        if (removeList.size() > 0) {
            //查询这些待删除时间段的总挂号人数
            long sum = doctorWorkPlanScheduleDao.searchSumNumByIds(removeList);
            //如果这些待删除的时间段总挂号人数大于0，说明这些时间段中有的存在患者挂号，所以不能删除直接抛异常
            if (sum > 0) {
                throw new HospitalException("修改失败，本次修改与已经挂号记录冲突");
            }

            //如果没有患者挂号，就删除这些时间段记录
            doctorWorkPlanScheduleDao.deleteByIds(removeList);

            //删除Redis上面的时间段缓存
            removeList.forEach(one -> {
                String key = "doctor_schedule_" + one;
                redisTemplate.delete(key);
            });
        }

        //处理添加时间段列表
        addList.forEach(one -> {
            //添加时间段记录
            doctorWorkPlanScheduleDao.insert(one);
        });

        return addList;
    }

    @Override
    @Transactional
    public void deleteByWorkPlanId(int workPlanId) {
        //查询出诊日程记录的ID值
        ArrayList<HashMap> list = doctorWorkPlanScheduleDao.searchByWorkPlanId(workPlanId);
        doctorWorkPlanScheduleDao.deleteByWorkPlanId(workPlanId);
        list.forEach(one -> {
            int scheduleId = MapUtil.getInt(one, "scheduleId");
            String key = "doctor_schedule_" + scheduleId;
            redisTemplate.delete(key);
        });
    }
}