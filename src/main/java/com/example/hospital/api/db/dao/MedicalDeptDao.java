package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.MedicalDeptEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 医疗部门 mapper 接口
 *
 * @author zm
 * @date 2022/11/20
 */
public interface MedicalDeptDao {

    ArrayList<HashMap> searchAll();

}




