package com.example.hospital.api.service;

import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface DoctorService {
    PageUtils serviceByPage(Map param);

    R searchContent(Integer id);

    void updatePhoto(MultipartFile file, Integer id);
}
