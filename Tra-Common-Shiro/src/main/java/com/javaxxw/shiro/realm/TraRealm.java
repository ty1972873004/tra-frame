package com.javaxxw.shiro.realm;

import com.javaxxw.common.utils.MD5Util;
import com.javaxxw.common.utils.PropertiesFileUtil;
import com.javaxxw.user.model.SysMenu;
import com.javaxxw.user.model.SysRole;
import com.javaxxw.user.model.SysUser;
import com.javaxxw.user.service.SysAuthorizeService;
import com.javaxxw.user.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @desc 用户认证和授权
 * @author tuyong
 * @since 2017/6/19
 * @version 1.0
 */
public class TraRealm extends AuthorizingRealm {

    private static Logger logger = LogManager.getLogger(TraRealm.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysAuthorizeService sysAuthorizeService;

    /**
     * 授权：验证权限时调用
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        SysUser sysUser = sysUserService.findByLoginName(username);

        // 当前用户所有角色
        List<SysRole> sysRoles = sysUserService.findByRole(sysUser.getId());
        Set<String> roles = new HashSet<>();
        for (SysRole sysRole : sysRoles) {
            if (StringUtils.isNotBlank(sysRole.getRoleName())) {
                roles.add(sysRole.getRoleName());
            }
        }

        // 当前用户所有权限
        List<SysMenu> tPermissions = sysAuthorizeService.selectPermissionByUserId(sysUser.getId());
        Set<String> permissions = new HashSet<>();
        for (SysMenu sysMenu : tPermissions) {
            if (StringUtils.isNotBlank(sysMenu.getMenuKey())) {
                permissions.add(sysMenu.getMenuKey());
            }
        }

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    /**
     * 认证：登录时调用
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());
        // client无密认证
        String traType = PropertiesFileUtil.getInstance("tra-manager-client").get("tra.manager.type");
        if ("client".equals(traType)) {
            return new SimpleAuthenticationInfo(username, password, getName());
        }

        // 查询用户信息
        SysUser sysUser = sysUserService.findByLoginName(username);

        if (null == sysUser) {
            throw new UnknownAccountException();
        }
        if (!sysUser.getPassword().equals(MD5Util.MD5(password + sysUser.getSalt()))) {
            throw new IncorrectCredentialsException();
        }
        if (sysUser.getEnable().equals("0")) {
            throw new LockedAccountException();
        }

        return new SimpleAuthenticationInfo(username, password, getName());
    }

}
