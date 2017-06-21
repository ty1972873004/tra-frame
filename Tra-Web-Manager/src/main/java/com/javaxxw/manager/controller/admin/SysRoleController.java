package com.javaxxw.manager.controller.admin;

import com.baomidou.mybatisplus.plugins.Page;
import com.javaxxw.base.controller.BaseController;
import com.javaxxw.shiro.util.WebUtil;
import com.javaxxw.user.model.SysRole;
import com.javaxxw.user.service.SysRoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-21 15:22
 **/
@Controller
@RequestMapping("/role")
@Api(value = "角色管理", description = "角色管理")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toList(){
        return "/manager/role/list.jsp";
    }
    @ResponseBody
    @RequestMapping("findByPage")
    public Page findByPage(String pageNow,
                           String pageSize, String column, HttpServletRequest request) throws Exception {
        Map<String, Object> params = WebUtil.getParameterMap(request);
        params.put("pageNum", pageNow);
        params.put("pageSize", pageSize);
        params.put("enable", "1");
        params.put("orderBy", com.baomidou.mybatisplus.toolkit.StringUtils.camelToUnderline(column));
        Page<SysRole> list=sysRoleService.queryBean(params);
        return list;
    }


}
