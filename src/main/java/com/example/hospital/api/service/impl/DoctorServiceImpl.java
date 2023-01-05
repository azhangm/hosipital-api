package com.example.hospital.api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.db.dao.DoctorDao;
import com.example.hospital.api.db.dao.MedicalDeptSubAndDoctorDao;
import com.example.hospital.api.db.pojo.MedicalDeptSubAndDoctorEntity;
import com.example.hospital.api.exception.HospitalException;
import com.example.hospital.api.service.DoctorService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Resource
    private DoctorDao doctorDao;

//    插入交叉表维持关系
    @Resource
    private MedicalDeptSubAndDoctorDao medicalDeptSubAndDoctorDao;
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


    @Override
    @Transactional
    public void updatePhoto(MultipartFile file ,Integer doctorId){

        /*定义图片名称*/
        String fileName = "doctor" + doctorId + ".jpg";
        /*连接 minio 容器*/
        MinioClient minioClient = new MinioClient.Builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

        try {
        /*不能超过 5MB*/
            minioClient.putObject(PutObjectArgs.builder().bucket("hospital").
                    object("doctor/" + fileName).
                    stream(file.getInputStream(),-1,5 * 1024 *1024).
                    contentType("image/jepg").
                    build()
            );
            HashMap map = new HashMap<>();
            map.put("id",doctorId);
//            保存在存储桶 中的相对地址
            map.put("photo","/doctor/" + fileName);
            doctorDao.updatePhoto(map);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.error("保存医生照片失败",e);
            throw new HospitalException("保存医生照片失败");
        }
        }


        @Override
        public void insert(Map param) {
            doctorDao.insert(param);
            Object uuid = param.get("uuid");
            Integer doctorId = doctorDao.searchIdByUuid((String) uuid);
            Integer subId = MapUtil.getInt(param, "subId");
            MedicalDeptSubAndDoctorEntity medicalDeptSubAndDoctorEntity = new MedicalDeptSubAndDoctorEntity();
            medicalDeptSubAndDoctorEntity.setDoctorId(doctorId);
            medicalDeptSubAndDoctorEntity.setDeptSubId(subId);
            medicalDeptSubAndDoctorDao.insert(medicalDeptSubAndDoctorEntity);
        }


    @Override
    public Map searchById(int id) {
        Map map = doctorDao.searchById(id);
        String tag = MapUtil.getStr(map, "tag");
        JSONArray array = JSONUtil.parseArray(tag);
        map.replace("tag", array);
        return map;
    }


    @Override
    public void update(Map param) {
        doctorDao.update(param);
        medicalDeptSubAndDoctorDao.updateDoctorSubDept(param);
    }

    @Override
    public void deleteByIds(Integer[] ids) {
        doctorDao.deleteByIds(ids);
    }
}
