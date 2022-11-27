package com.example.hospital.api.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DoctorDaoTest {

    @Resource
    private DoctorDao doctorDao;
    @Test
    void searchContent() {
        Map map = doctorDao.searchContent(1);
        System.out.println(map);
    }
}