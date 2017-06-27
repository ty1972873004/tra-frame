<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="basePath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>菜单管理</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
    <%@include file="/resources/common/common.jspf"%>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <ul id="topli"></ul>
        </div>
        <div class="ibox-content">
            <div class="row row-lg">

                <div class="col-sm-12">
                    <!-- Example Pagination -->
                    <div class="example-wrap">
                        <h4 class="example-title">菜单管理列表</h4>
                       <div id="toolbar">
                           <%-- <button id="check" class="btn btn-default">全选</button>
                            <button id="uncheck" class="btn btn-default">取消全选</button>--%>
                               <shiro:hasPermission name="manager:role:add"><a class="btn btn-primary btn-rounded" href="javascript:;" onclick="createAction()"> 新增角色</a></shiro:hasPermission>
                        </div>
                        <div class="example">
                            <table id="menuList" >
                            </table>  <!-- 留意-->
                        </div>
                    </div>
                    <!-- End Example Pagination -->
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${basePath}/resources/js/manager/menu/list.js"></script>
</body>
</html>
