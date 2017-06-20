package com.javaxxw.user.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.javaxxw.base.model.BaseModel;

@TableName("sys_menu_user")
public class SysMenuUser extends BaseModel{

    private Long menuId;

    private Long userId;

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
