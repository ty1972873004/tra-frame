package com.javaxxw.user.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION 菜单树
 * @create 2017-06-21 10:04
 **/
public class MenuTreeObject {

    private String id;

    private String parentId;

    private String menuName;

    private String parentName;

    private String menuKey;

    private String request;

    private String icon;

    private String isHide;

    private String sort;

    private List<MenuTreeObject> children = new ArrayList<MenuTreeObject>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MenuTreeObject> getChildren() {
        return children;
    }

    public void setChildren(List<MenuTreeObject> children) {
        this.children = children;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
