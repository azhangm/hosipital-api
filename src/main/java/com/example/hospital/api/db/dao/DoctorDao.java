package com.example.hospital.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorDao {

    ArrayList<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    Map searchContent(Integer id);
}




