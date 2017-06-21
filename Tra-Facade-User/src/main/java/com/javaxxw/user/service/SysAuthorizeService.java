package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseService;
import com.javaxxw.user.model.SysMenu;

import java.util.List;
import java.util.Map;

public interface SysAuthorizeService extends BaseService<SysMenu> {

    List<SysMenu>  selectPermissionByUserId(Long userId);

    List<Map<String,Object>> selectMenuByUserId(Long userId);
}
