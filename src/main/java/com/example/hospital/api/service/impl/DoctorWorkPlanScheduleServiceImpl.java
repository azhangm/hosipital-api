package com.example.hospital.api.service.impl;

import com.example.hospital.api.db.dao.DoctorWorkPlanDao;
import com.example.hospital.api.db.dao.DoctorWorkPlanScheduleDao;
import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;
import com.example.hospital.api.service.DoctorWorkPlanScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorWorkPlanScheduleServiceImpl implements DoctorWorkPlanScheduleService {

    @Resource
    private DoctorWorkPlanScheduleDao doctorWorkPlanScheduleDao;

    @Resource
    private DoctorWorkPlanDao doctorWorkPlanDao;

    @Override
    public void insert(List<DoctorWorkPlanScheduleEntity> list) {
        insertScheduleHandle(list);
        //TODO 设置Redis缓存，用于防止挂号超售

    }

    @Transactional
    void insertScheduleHandle(List<DoctorWorkPlanScheduleEntity> list) {
        for (DoctorWorkPlanScheduleEntity entity : list) {
            doctorWorkPlanScheduleDao.insert(entity);
        }
    }

}