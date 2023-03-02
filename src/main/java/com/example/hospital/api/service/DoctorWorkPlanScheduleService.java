package com.example.hospital.api.service;

import com.example.hospital.api.db.pojo.DoctorWorkPlanScheduleEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface DoctorWorkPlanScheduleService {
     void insert(List<DoctorWorkPlanScheduleEntity> list);

    ArrayList searchDeptSubSchedule(Map param);
}
