package com.yang.crm.web.listener;

import com.yang.crm.settings.domain.DicValue;
import com.yang.crm.settings.service.DicService;
import com.yang.crm.settings.service.impl.DicServiceImpl;
import com.yang.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {


    //监听上下文域对象的方法
    public void contextInitialized(ServletContextEvent event) {
        //System.out.println("上下文域对象创建了");
        System.out.println("服务器缓存处理数据字典开始=========================");
        //拿到上下文域对象
        ServletContext application = event.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        /*
           打包为一个map
                    map.put("appellationList",dvList1);
                    map.put("clueStateList",dvList2);
                    map.put("stageList",dvList3);
                    ...
         */

        Map<String, List<DicValue>> map = ds.getAll();

        //将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束=========================");


        //===============================================
        //数据字典处理完毕后，处理Stage2Possibility.properties文件（map）

        //解析properties文件

        Map<String,String> pMap = new HashMap<String,String>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> e = rb.getKeys();

        while (e.hasMoreElements()){

            //阶段
            String key = e.nextElement();
            //可能性
            String value = rb.getString(key);

            pMap.put(key,value);
        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);



    }
}
