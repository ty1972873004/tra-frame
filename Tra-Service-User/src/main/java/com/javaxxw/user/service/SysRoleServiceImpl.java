package com.javaxxw.user.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.javaxxw.base.service.BaseServiceImpl;
import com.javaxxw.user.mapper.SysRoleMapper;
import com.javaxxw.user.model.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-15 17:00
 **/
@Service("sysRoleService")
@CacheConfig(cacheNames = "sysUser")
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;


    @Override
    public Page<SysRole> queryBean(Map<String, Object> params) {
        Page<SysRole> pageInfo = super.query(params);
        return pageInfo;
    }
}
