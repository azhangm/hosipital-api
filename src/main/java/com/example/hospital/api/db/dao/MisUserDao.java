package com.example.hospital.api.db.dao;

import java.util.ArrayList;
import java.util.Map;

public interface MisUserDao {
    public ArrayList<String> searchUserPermissions(int userId);

    public Integer login(Map param);
}




