package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseService;
import com.javaxxw.user.model.SysMenu;

import java.util.List;

public interface SysAuthorizeService extends BaseService<SysMenu> {

    List<SysMenu>  selectPermissionByUserId(Long userId);
}
