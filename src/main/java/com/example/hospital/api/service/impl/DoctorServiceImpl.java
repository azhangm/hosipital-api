package com.example.hospital.api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
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
//            没有分页记录
            list = doctorDao.searchByPage(param);
        }else {
//            为了让前端 不报语法错误 封装为一个空的集合
            list = new ArrayList<>();
        }
//        form 类 里面定义的变量
        int page = MapUtil.getInt(param,"page");
        int length = MapUtil.getInt(param,"length");
//        list 分页记录
//        count 记录总数
//        当前在 哪一页
//        length 每页的记录数
        return new PageUtils(list,count,page,length);
    }

    @Override
    public R searchContent(Integer id) {
        Map map = doctorDao.searchContent(id);
        String tag = MapUtil.getStr(map, "tag");
        JSONArray jsonTag = JSONUtil.parseArray(tag);
        map.replace("tag",jsonTag);
        return R.ok(map);
    }

}
