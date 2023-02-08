package com.example.hospital.api.service;

import cn.hutool.json.JSONArray;
import com.google.gson.JsonArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

/**
 * 医疗部门子工作计划服务
 *
 * @author zm
 * @date 2023/02/06
 */
public interface MedicalDeptSubWorkPlanService {

    JSONArray searchWorkPlanInRange(Map<String,Object> param , ArrayList dateList);
}
