package com.yang.crm.workbench.service.impl;

import com.yang.crm.utils.DateTimeUtil;
import com.yang.crm.utils.SqlSessionUtil;
import com.yang.crm.utils.UUIDUtil;
import com.yang.crm.workbench.dao.CustomerDao;
import com.yang.crm.workbench.dao.TranDao;
import com.yang.crm.workbench.dao.TranHistoryDao;
import com.yang.crm.workbench.domain.Customer;
import com.yang.crm.workbench.domain.Tran;
import com.yang.crm.workbench.domain.TranHistory;
import com.yang.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    public boolean save(Tran t, String customerName) {

        /*
            t对象，就少了一条信息，就是客户的主键customerId

            先处理客户的相关需求
                1.根据客户名称（customerName）在客户表中进行精确查询
                    如果有这个客户，将该客户的id封装到t中
                    如果没有这个客户，就新建一个客户信息，将新建的客户的id封装到t中
                2.经过了上述操作后，t对象中的信息就完整了，需要执行添加交易的操作
                3.添加交易完毕后，需要创建一条交易历史

         */

        boolean flag = true;

        Customer cus = customerDao.getCustomerByName(customerName);


        //如果cus为null，需要创建客户
        if (cus == null){

            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setContactSummary(t.getContactSummary());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());

            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1){
                flag = false;
            }

        }

        //客户Customer到此已经有了,客户的id也就有了
        //将customerId封装到t对象中
        t.setCustomerId(cus.getId());

        //添加交易
        int count2 = tranDao.save(t);
        if (count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());

        int count3 = tranHistoryDao.save(th);
        if (count3 != 1){
            flag = false;
        }

        return flag;
    }

    public Tran detail(String id) {

        Tran t = tranDao.detail(id);

        return t;
    }

    public List<TranHistory> getHistoryListByTranId(String tranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(tranId);

        return thList;
    }

    public boolean changStage(Tran t) {

        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if (count1 != 1 ){
            flag = false;
        }

        //交易阶段改变后，新增一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(t.getStage());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());

        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }

        return flag;
    }

    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();


        //取得dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();


        //将total和dataList保存到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);

        //返回map
        return map;
    }
}
