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


    <title> - Bootstrap Table</title>
    <meta name="keywords" content="">
    <meta name="description" content="">

    <link rel="shortcut icon" href="favicon.ico"> <link href="${basePath}/resources/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="${basePath}/resources/css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <link href="${basePath}/resources/css/plugins/bootstrap-table/bootstrap-table.min.css" rel="stylesheet">
    <link href="${basePath}/resources/css/animate.css" rel="stylesheet">
    <link href="${basePath}/resources/css/style.css?v=4.1.0" rel="stylesheet">


</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">


    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>基本</h5>
        </div>
        <div class="ibox-content">
            <div class="row row-lg">

                <div class="col-sm-12">
                    <!-- Example Pagination -->
                    <div class="example-wrap">
                        <h4 class="example-title">分页：客户端</h4>
                        <div class="example">
                            <table id="empUserList" >
                            </table>  <!-- 留意-->
                        </div>
                    </div>
                    <!-- End Example Pagination -->
                </div>
            </div>
        </div>
    </div>




</div>

<!-- 全局js -->
<script src="${basePath}/resources/js/jquery.min.js?v=2.1.4"></script>
<script src="${basePath}/resources/js/bootstrap.min.js?v=3.3.6"></script>

<!-- 自定义js -->
<script src="${basePath}/resources/js/content.js?v=1.0.0"></script>


<!-- Bootstrap table -->
<script src="${basePath}/resources/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="${basePath}/resources/js/plugins/bootstrap-table/bootstrap-table-mobile.min.js"></script>
<script src="${basePath}/resources/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<!-- Peity -->
<script type="application/javascript">
    var rootPath = "${basePath}";
    alert(rootPath);
</script>
<script src="${basePath}/resources/js/demo/bootstrap-table-demo.js"></script>
<script src="${basePath}/resources/js/manager/role/list.js"></script>

</body>
</html>
