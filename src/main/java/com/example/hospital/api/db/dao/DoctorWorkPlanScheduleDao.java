package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 医生工作计划安排
 *
 * @author zm
 * @date 2023/02/06
 */
public interface DoctorWorkPlanScheduleDao {

    void insert(DoctorWorkPlanScheduleEntity entity);

    public ArrayList<HashMap> searchNewSchedule(int workPlanId);

     ArrayList<HashMap> searchDeptSubSchedule(Map param);
}




