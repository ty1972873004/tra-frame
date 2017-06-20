package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseService;
import com.javaxxw.user.model.SysRole;
import com.javaxxw.user.model.SysUser;

import java.util.List;

public interface SysUserService extends BaseService<SysUser>{

    void updateLoginInfo(SysUser user, String ip);

    SysUser findByLoginName(String loginName);

    List<SysRole> findByRole(Long userId);


}
