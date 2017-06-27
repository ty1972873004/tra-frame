package com.javaxxw.manager.controller.admin;

import com.javaxxw.user.model.SysMenu;
import com.javaxxw.user.service.SysMenuService;
import com.javaxxw.user.service.SysRoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-27 15:20
 **/
@Controller
@RequestMapping("/menu")
@Api(value = "菜单管理", description = "菜单管理")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String toList(){
        return "/manager/menu/list.jsp";
    }



}
