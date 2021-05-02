package com.yang.crm.settings.service;

import com.yang.crm.exception.LoginException;
import com.yang.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
