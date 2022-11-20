package com.example.hospital.api.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.LoginForm;
import com.example.hospital.api.service.MisUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis_user")
public class MisUserController {

    @Resource
    private MisUserService misUserService;

    @PostMapping("/login")
    public R Login(@RequestBody  LoginForm loginForm) {
        System.out.println(loginForm);
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(loginForm);
        Integer login = misUserService.login(stringObjectMap);
        if (login == null) return R.ok().put("result",false);
        StpUtil.login(login);
        String tokenValue = StpUtil.getTokenValue();
        List<String> permissionList = StpUtil.getPermissionList();
        return R.ok().put("result",true).put("token",tokenValue).put("permissions",permissionList);
    }

    @GetMapping("logout")
    @SaCheckLogin
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }
}
