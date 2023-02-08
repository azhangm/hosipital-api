package com.example.hospital.api.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DoctorWorkPlanDaoTest {

    @Resource
    private DoctorWorkPlanDao  dao;

    @Test
    public void testSearchWorkPlanInRange () {
        HashMap<String, Object> map = new HashMap<>();
        map.put("startDate",new Date());
        map.put("endDate",new Date());
        map.put("doctorName","张猛");
        System.out.println(dao.searchWorkPlanInRange(map));
    }
}