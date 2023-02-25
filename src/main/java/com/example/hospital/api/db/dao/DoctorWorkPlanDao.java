package com.example.hospital.api.db.dao;


import com.example.hospital.api.db.pojo.DoctorWorkPlanEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 医生工作计划
 *
 * @author zm
 * @date 2023/02/06
 */
public interface DoctorWorkPlanDao {

     ArrayList<HashMap> searchWorkPlanInRange(Map param);

//     ${doctorId}
//= ${deptSubId}
//     to_date 查询工作计划
     Integer searchId(Map<String,Object> param);

     @Transactional
     void insert(DoctorWorkPlanEntity entity);
}




