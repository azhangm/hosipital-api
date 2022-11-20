package com.example.hospital.api.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.db.dao.DoctorDao;
import com.example.hospital.api.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    @Resource
    private DoctorDao doctorDao;

    @Override
    public PageUtils serviceByPage( Map param) {
        ArrayList<HashMap> list;
        long count = doctorDao.searchCount(param);
        if (count > 0) {
            list = doctorDao.searchByPage(param);
        }else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param,"page");
        int length = MapUtil.getInt(param,"length");
        return new PageUtils(list,count,page,length);
    }

}
