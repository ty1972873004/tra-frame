package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseService;
import com.javaxxw.base.service.BaseServiceImpl;
import com.javaxxw.user.mapper.SysRoleMapper;
import com.javaxxw.user.mapper.SysUserMapper;
import com.javaxxw.user.mapper.SysUserRoleMapper;
import com.javaxxw.user.model.SysRole;
import com.javaxxw.user.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-15 12:01
 **/
@Service("sysUserService")
@CacheConfig(cacheNames = "sysUser")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService{

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;


    @Override
    public void updateLoginInfo(SysUser user, String ip) {

    }
    @Override
    public SysUser findByLoginName(String loginName) {
        SysUser sysUser=new SysUser();
        sysUser.setLoginName(loginName);
        return this.findTByTOne(sysUser);
    }

    @Override
    public List<SysRole> findByRole(Long userId) {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("userId",userId);
        List<Long> ids = sysUserRoleMapper.selectIdPage(map);
        return sysRoleMapper.selectBatchIds(ids);
    }
}
