package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.SearchContentForm;
import com.example.hospital.api.controller.form.SearchDoctorByPageForm;
import com.example.hospital.api.service.DoctorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
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
        Map param = BeanUtil.beanToMap(form);
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

}
