package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseServiceImpl;
import com.javaxxw.user.mapper.SysRoleMenuMapper;
import com.javaxxw.user.model.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-16 16:27
 **/
@Service("sysAuthorizeService")
public class SysAuthorizeServiceImpl extends BaseServiceImpl<SysMenu> implements SysAuthorizeService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;


    @Override
    public List<SysMenu> selectPermissionByUserId(Long userId) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId",userId);
        List<Long> ids=sysRoleMenuMapper.selectIdPage(params);
        return this.getList(ids);
    }
}
