package com.example.hospital.api.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.db.dao.MedicalDeptDao;
import com.example.hospital.api.service.MedicalDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
public class MedicalDeptServiceImpl implements MedicalDeptService {
    @Resource
    private MedicalDeptDao medicalDeptDao;

    @Override
    public ArrayList<HashMap> searchAll() {
        ArrayList<HashMap> list = medicalDeptDao.searchAll();
        return list;
    }

    @Override
    public HashMap searchDeptAndSub() {
        ArrayList<HashMap> list = medicalDeptDao.searchDeptAndSub();
        LinkedHashMap map = new LinkedHashMap();
        for (HashMap one : list) {

            Integer subId = MapUtil.getInt(one, "subId");
            String deptName = MapUtil.getStr(one, "deptName");
            String subName = MapUtil.getStr(one, "subName");

            if (map.containsKey(deptName)) {
                ArrayList<HashMap> subList = (ArrayList<HashMap>) map.get(deptName);
                subList.add(new HashMap() {{
                    put("subId", subId);
                    put("subName", subName);
                }});
            } else {
                map.put(deptName, new ArrayList() {{
                    add(new HashMap() {{
                        put("subId", subId);
                        put("subName", subName);
                    }});
                }});
            }
        }
        return map;
    }
}
