package com.example.hospital.api.db.dao;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorDao {

    ArrayList<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    Map searchContent(Integer id);

    void updatePhoto(Map param);

    @Transactional
    void insert(Map param);

    Integer searchIdByUuid(String uuid);

    Map searchById(Integer id);

    void update(Map param);

    void deleteByIds(Integer[] ids);
}




