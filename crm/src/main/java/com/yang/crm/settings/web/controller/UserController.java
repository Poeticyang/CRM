package com.yang.crm.settings.web.controller;

import com.yang.crm.settings.domain.User;
import com.yang.crm.settings.service.UserService;
import com.yang.crm.settings.service.impl.UserServiceImpl;
import com.yang.crm.utils.MD5Util;
import com.yang.crm.utils.PrintJson;
import com.yang.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到用户控制器");

        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if ("/settings/user/xxx.do".equals(path)){
            //xxx(request,response);
        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到后台controller");

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        //将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);

        //接受浏览器端的ip地址
        /*****************************/
        String ip = request.getRemoteAddr();
        /*String ip = "127.0.0.1";*/
        System.out.println("------------------"+ip);

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{

            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);

            PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            e.printStackTrace();

            String msg = e.getMessage();

            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",msg);

            PrintJson.printJsonObj(response,map);

        }


    }
}
