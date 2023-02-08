package com.example.hospital.api.db.dao;


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
}




