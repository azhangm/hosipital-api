package com.example.hospital.api.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.BiMap;
import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.db.dao.DoctorWorkPlanDao;
import com.example.hospital.api.db.dao.DoctorWorkPlanScheduleDao;
import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;
import com.example.hospital.api.service.DoctorWorkPlanScheduleService;
import org.apache.hadoop.util.hash.Hash;
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
}