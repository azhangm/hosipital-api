package com.example.hospital.api.service;

import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SuppressWarnings("all")
public interface DoctorWorkPlanScheduleService {
     void insert(List<DoctorWorkPlanScheduleEntity> list);

    HashMap searchByWorkPlanId(int workPlanId);

    ArrayList searchDeptSubSchedule(Map param);

     void updateSchedule(Map param);

    void deleteByWorkPlanId(int workPlanId);
}
