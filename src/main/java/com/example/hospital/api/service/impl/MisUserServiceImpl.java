package com.example.hospital.api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.hospital.api.db.dao.MisUserDao;
import com.example.hospital.api.service.MisUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
@Service
public class MisUserServiceImpl implements MisUserService {


    @Resource
    private MisUserDao misUserDao;

    @Override
    public Integer login(Map user) {
        String username = MapUtil.getStr(user, "username");
        String password = MapUtil.getStr(user, "password");
        MD5 md5 = MD5.create();
        String s = md5.digestHex(username);
        String subWithLength = StrUtil.subWithLength(s, 0, 6);
        String subEnd = StrUtil.subSuf(s, s.length() - 3);
        String newPassword = md5.digestHex(subWithLength + password + subEnd);
        user.replace("password",newPassword);
        return misUserDao.login(user);
    }
}
