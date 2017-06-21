package com.javaxxw.user.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.javaxxw.base.service.BaseService;
import com.javaxxw.user.model.SysRole;

import java.util.Map;

public interface SysRoleService extends BaseService<SysRole> {

     Page<SysRole> queryBean(Map<String, Object> params) ;
}
