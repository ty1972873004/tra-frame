$(document).ready(function() {
    //初始化Table
    var oTable = new TableInit();
    oTable.Init();
});
var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#roleList').bootstrapTable({
            //url: 'findByPage',         //请求后台的URL（*）
            //method: 'post',                      //请求方式（*）
            //toolbar: '#toolbar',                //工具按钮用哪个容器
            //queryParams: oTableInit.queryParams,//传递参数（*）
            //queryParamsType : "undefined",
            //sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
            //pageNumber:1,                       //初始化加载第一页，默认第一页
            //pageSize: 10,                       //每页的记录行数（*）
            //striped: true,                      //是否显示行间隔色
            //showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
            //cardView: false,                    //是否显示详细视图
            //detailView: false,                   //是否显示父子表
            //pageList: [10, 15, 20, 50],        //可供选择的每页的行数（*）
            //processing: true,                   //载入数据的时候是否显示“载入中”
            //cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            //pagination: true,                   //是否显示分页（*）
            //search: false,                       //是否显示表格搜索
            //showColumns: true,                  //是否显示所有的列
            //showRefresh: true,                  //是否显示刷新按钮
            //uniqueId: "id",                     //每一行的唯一标识，一般为主键列
            //contentType: "application/x-www-form-urlencoded", //解决POST提交问题
            //silent: true,  //刷新事件必须设置
            //clickToSelect: true,//点击行即可选中单选/复选框
            //checkboxHeader: true,
            //maintainSelected: true,

            url: 'findByPage',
            method: 'post',                      //请求方式（*）
            queryParams: oTableInit.queryParams,//传递参数（*）
            contentType: "application/x-www-form-urlencoded", //解决POST提交问题
            queryParamsType : "undefined",
            striped: true,
            search: true,
            showRefresh: true,
            showColumns: true,
            minimumCountColumns: 2,
            clickToSelect: true,
            pagination: true,
            paginationLoop: false,
            sidePagination: 'server',
            silentSort: false,
            smartDisplay: false,
            escape: true,
            searchOnEnterKey: true,
            idField: 'id',
            maintainSelected: true,
            toolbar: '#toolbar',

            columns : [ {
                field : 'id',
                checkbox : true,
                align : 'center',
                valign : 'middle'
            }, {
                title : '角色名称',
                field : 'roleName', // 字段
                align : 'center', // 对齐方式（左 中 右）
                valign : 'middle', //
                sortable : true
            }, {
                title : '角色类型',
                field : 'roleType',
                align : 'center',
                valign : 'middle',
                sortable : true
            }, {
                title : '角色key',
                field : 'roleKey',
                align : 'center',
                valign : 'middle',
                sortable : true
            }, {
                title : '状态',
                field : 'status',
                align : 'center',
                valign : 'middle',
                formatter:function(value,row,index){
                    if (value == null || value == undefined) {
                        return "-";
                    } else if (value=='1') {
                        return "正常";
                    } else if(value=='0'){
                        return "冻结";
                    }
                },
                sortable : true
            }, {
                title : '创建时间',
                field : 'createTime',
                align : 'center',
                valign : 'middle',
                formatter:function(value,row,index){
                   return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                },
                sortable : true
            }  ],
            responseHandler : function(res) {
                return {
                    total : res.total,
                    rows : res.records
                };
            },
        });
    };
    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            pageNow : params.pageNumber,
            pageSize : params.pageSize,
         };
        return temp;
    };
    return oTableInit;
};


var $table = $('#roleList'),
    $check = $('#check'),
    $uncheck = $('#uncheck');

$(function () {
    $check.click(function () {
        $table.bootstrapTable('checkAll');
    });
    $uncheck.click(function () {
        $table.bootstrapTable('uncheckAll');
    });
});

