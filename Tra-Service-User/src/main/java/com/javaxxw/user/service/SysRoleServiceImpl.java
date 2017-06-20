package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseServiceImpl;
import com.javaxxw.user.model.SysRole;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-15 17:00
 **/
@Service("sysRoleService")
@CacheConfig(cacheNames = "sysUser")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole> implements SysRoleService {
}
