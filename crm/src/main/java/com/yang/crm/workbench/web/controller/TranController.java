package com.yang.crm.workbench.web.controller;

import com.yang.crm.settings.domain.User;
import com.yang.crm.settings.service.UserService;
import com.yang.crm.settings.service.impl.UserServiceImpl;
import com.yang.crm.utils.DateTimeUtil;
import com.yang.crm.utils.PrintJson;
import com.yang.crm.utils.ServiceFactory;
import com.yang.crm.utils.UUIDUtil;
import com.yang.crm.workbench.domain.Activity;
import com.yang.crm.workbench.domain.Clue;
import com.yang.crm.workbench.domain.Tran;
import com.yang.crm.workbench.domain.TranHistory;
import com.yang.crm.workbench.service.ActivityService;
import com.yang.crm.workbench.service.ClueService;
import com.yang.crm.workbench.service.CustomerService;
import com.yang.crm.workbench.service.TranService;
import com.yang.crm.workbench.service.impl.ActivityServiceImpl;
import com.yang.crm.workbench.service.impl.ClueServiceImpl;
import com.yang.crm.workbench.service.impl.CustomerServiceImpl;
import com.yang.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("=====================进入到交易模块控制器");


        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if ("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)){
            getHistoryListByTranId(request,response);
        }else if ("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if ("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到取得交易阶段数量统计图表操作的控制器");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());


        /*
            业务层为我们返回：
                total,
                dataList

                通过map打包以上两项来返回

         */
        Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response,map);

    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到执行改变交易阶段操作的控制器");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();


        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditTime(editTime);
        t.setEditBy(editBy);
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());


        boolean flag = ts.changStage(t);


        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);


    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到根据交易的id来取得相应的交易历史列表操作的控制器");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getHistoryListByTranId(tranId);

        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");

        for (TranHistory th:thList){

            //根据每条交易历史，取出其对应的阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);

        }

        PrintJson.printJsonObj(response,thList);

    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("进入到执行跳转到交易的详细信息页操作的控制器");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran t = ts.detail(id);


        /*
            处理可能性：
                阶段   t
                阶段和可能性之间的对应关系   pMap


         */
        String stage = t.getStage();
        //ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);

        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        System.out.println("进入到执行添加交易操作的控制器");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); //此处我们只有customerName
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(t,customerName);

        if (flag){

            //如果交易添加成功，跳转到列表页
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

        }



    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到取得客户名称列表操作的控制器（按照客户名称模糊查询）");

        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> sList = cs.getCustomerName(name);

        PrintJson.printJsonObj(response,sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        System.out.println("进入到跳转到交易添加页操作的控制器");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();

        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);



    }


}
