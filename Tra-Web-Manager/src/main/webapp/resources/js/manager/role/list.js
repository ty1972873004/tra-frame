$(function() {
    querys();
})
function querys() {
    $("#edit").attr({"disabled":"disabled"});
    $("#delete").attr({"disabled":"disabled"});
    $("#empUserList").bootstrapTable({
        url : rootPath + '/role/findByPage',
        height : '500',
        undefinedText : '-',
        pagination : true, // 分页
        striped : true, // 是否显示行间隔色
        queryParams : queryParams,
        cache : false, // 是否使用缓存
        pageList : [ 5, 10, 20 ],
        toolbar : "#toolbar",// 指定工具栏
        showColumns : true, // 显示隐藏列
        showRefresh : true, // 显示刷新按钮
        uniqueId : "id", // 每一行的唯一标识
        sidePagination : "server", // 服务端处理分页
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
            formatter : genderFormatter,
            sortable : true
        } ],
        responseHandler : function(res) {
            return {
                total : res.total,
                rows : res.records
            };
        },
        onCheck:function(){
            buttonControl('#empUserList','#edit','#delete');
        },
        onCheckAll:function(){
            buttonControl('#empUserList','#edit','#delete');
        },
        onUncheckAll:function(){
            buttonControl('#empUserList','#edit','#delete');
        },
        onUncheck:function(){
            buttonControl('#empUserList','#edit','#delete');
        }
    })
}
/** 替换数据为文字 */
function genderFormatter(value) {
    if (value == null || value == undefined) {
        return "-";
    } else if (value==1) {
        return "正常";
    } else if(value==0){
        return "冻结";
    }
}
/** 刷新页面 */
function refresh() {
    $('#empUserList').bootstrapTable('refresh');
}
/**查询条件与分页数据 */
function queryParams(pageReqeust) {
    pageReqeust.enabled = $("#enabled").val();
    pageReqeust.querys = $("#querys").val();
    pageReqeust.pageNo = this.offset;
    pageReqeust.pageSize = this.pageNumber;
    return pageReqeust;
}

