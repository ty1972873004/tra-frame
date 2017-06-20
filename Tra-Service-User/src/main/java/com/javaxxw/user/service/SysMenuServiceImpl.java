package com.javaxxw.user.service;

import com.javaxxw.base.service.BaseServiceImpl;
import com.javaxxw.user.model.SysMenu;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-16 16:20
 **/
@Service("sysMenuService")
@CacheConfig(cacheNames = "sysMenu")
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenu>  implements SysMenuService {
}
