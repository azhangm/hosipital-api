package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.*;
import com.example.hospital.api.service.DoctorService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 医生控制器
 *
 * @author zm
 * @date 2022/11/18
 */
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT","DOCTOR:SELECT"},mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchDoctorByPageForm form) {
        Map<String,Object> param = BeanUtil.beanToMap(form);
//        当前页数
        int page = form.getPage();
        int length = form.getLength();
//        从 第几条 开始
        int start = (page - 1) * length;
        param.put("start",start);
        PageUtils pageUtils = doctorService.serviceByPage(param);
        return R.ok().put("result",pageUtils);
    }

    @PostMapping("/searchContent")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT","DOCTOR:SELECT"},mode = SaMode.OR)
    public R searchContent(@RequestBody @Valid SearchContentForm form) {
        return doctorService.searchContent(form.getId());
    }

    @PostMapping("/updatePhoto")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT","DOCTOR:UPDATE"},mode = SaMode.OR)
    public R updatePhoto(@Param("file")MultipartFile file , @Param("doctorId") Integer doctorId) {
        doctorService.updatePhoto(file,doctorId);
        return R.ok();
    }

    @PostMapping("/insert")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT","DOCTOR:INSERT"},mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertDoctorForm from) {
        Map<String,Object> param = BeanUtil.beanToMap(from);
        for (Object o : param.entrySet()) {
            System.out.println(param.get(o));
        }
        String s = JSONUtil.parseArray(from.getTag()).toString();
        param.replace("tag",s);
        param.put("uuid", IdUtil.simpleUUID().toUpperCase());
        doctorService.insert(param);
        return R.ok();
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchDoctorByIdForm form) {
        Map<String ,Object> map =  doctorService.searchById(form.getId());
        return R.ok(map);
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateDoctorForm form) {
        Map<String,Object> param = BeanUtil.beanToMap(form);
        String json = JSONUtil.parseArray(form.getTag()).toString();
        param.replace("tag", json);
        doctorService.update(param);
        return R.ok();
    }


    @PostMapping("/delete")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT","DOCTOR:DELETE"},mode = SaMode.OR)
    public R delete(@RequestBody @Valid  DeleteDoctorForm form) {
        doctorService.deleteByIds(form.getIds());
        return R.ok();
    }
}
