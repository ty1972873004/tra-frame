package com.javaxxw.user.mapper;

import com.javaxxw.base.BaseMapper;
import com.javaxxw.user.model.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-16 16:21
 **/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<Map<String,Object>> selectMenuByUserId(@Param("userId")Long userId);

}
