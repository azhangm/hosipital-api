package com.example.hospital.api.db.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MisUserDaoTest {

    @Resource
    private MisUserDao misUserDao;
    @Test
    void login() {
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("username","admin");
        objectObjectHashMap.put("password","061575f43e456772015c0032c0531edf");
        Integer login = misUserDao.login(objectObjectHashMap);
        System.out.println(login);
    }
}