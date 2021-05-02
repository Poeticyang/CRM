<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" +
            request.getServerName() + ":" + request.getServerPort() +
            request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>"/>
    <title>Title</title>
</head>
<body>


$.ajax({
    url : "",
    data : {

    },
    type : "",
    dataType : "json",
    success : function (data) {



    }
})

-----------------------------------------------------------------------------
//创建时间：系统当前时间
String createTime = DateTimeUtil.getSysTime();
//创建人：系统当前登录用户
String createBy = ((User)request.getSession().getAttribute("user")).getName();

------------------------------------------------------------------------------------
日历控件
$(".time").datetimepicker({
    minView: "month",
    language:  'zh-CN',
    format: 'yyyy-mm-dd',
    autoclose: true,
    todayBtn: true,
    pickerPosition: "bottom-left"
});

========================================
自动补全控件
$("#create-customerName").typeahead({
    source: function (query, process) {
        $.get(
            "workbench/transaction/getCustomerName.do",
            { "name" : query },
            function (data) {
            //alert(data);
            process(data);
            },
            "json"
            );
        },
    delay: 1500
});

</body>
</html>
