package com.yang.crm.settings.service.impl;

import com.yang.crm.settings.dao.DicTypeDao;
import com.yang.crm.settings.dao.DicValueDao;
import com.yang.crm.settings.domain.DicType;
import com.yang.crm.settings.domain.DicValue;
import com.yang.crm.settings.service.DicService;
import com.yang.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    public Map<String, List<DicValue>> getAll() {

        Map<String, List<DicValue>> map = new HashMap<String, List<DicValue>>();
        //将字典类型列表取出
        List<DicType> dtList = dicTypeDao.getTypeList();

        //遍历字典类型列表
        for (DicType dt:dtList){

            //取得每一种类型的字典类型编码 Code
            String code = dt.getCode();

            //根据每一个code来查找字典的值valueList
            List<DicValue> dvList = dicValueDao.getListByCode(code);

            map.put(code+"List",dvList);
        }


        return map;
    }
}
