package com.javaxxw.user.util;

import com.javaxxw.user.entity.MenuTreeObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 把一个list集合,里面的bean含有 parentId 转为树形式
 *
 */
public class MenuTreeUtil {
	
	
	/**
	 * 根据父节点的ID获取所有子节点
	 * @param list 分类表
	 * @param parentId 传入的父节点ID
	 * @return String
	 */
	public List<MenuTreeObject> getChildTreeObjects(List<MenuTreeObject> list, long parentId) {
		List<MenuTreeObject> returnList = new ArrayList<MenuTreeObject>();
		for (Iterator<MenuTreeObject> iterator = list.iterator(); iterator.hasNext();) {
			MenuTreeObject t = (MenuTreeObject) iterator.next();
			// 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
			if (t.getParentId().equals(String.valueOf(parentId))) {
				recursionFn(list, t);
				returnList.add(t);
			}
		}
		return returnList;
	}
	
	/**
	 * 递归列表
	 * @param list
	 * @param t
	 */
	private void  recursionFn(List<MenuTreeObject> list, MenuTreeObject t) {
		List<MenuTreeObject> childList = getChildList(list, t);// 得到子节点列表
		t.setChildren(childList);
		for (MenuTreeObject tChild : childList) {
			if (hasChild(list, tChild)) {// 判断是否有子节点
				//returnList.add(TreeObject);
				Iterator<MenuTreeObject> it = childList.iterator();
				while (it.hasNext()) {
					MenuTreeObject n = (MenuTreeObject) it.next();
					recursionFn(list, n);
				}
			}
		}
	}
	
	// 得到子节点列表
	private List<MenuTreeObject> getChildList(List<MenuTreeObject> list, MenuTreeObject t) {
		
		List<MenuTreeObject> tlist = new ArrayList<MenuTreeObject>();
		Iterator<MenuTreeObject> it = list.iterator();
		while (it.hasNext()) {
			MenuTreeObject n = (MenuTreeObject) it.next();
			if (n.getParentId().equals(t.getId())) {
				tlist.add(n);
			}
		}
		return tlist;
	} 
	List<MenuTreeObject> returnList = new ArrayList<MenuTreeObject>();
	/**
     * 根据父节点的ID获取所有子节点
     * @param list 分类表
     * @param typeId 传入的父节点ID
     * @param prefix 子节点前缀
     */
    public List<MenuTreeObject> getChildTreeObjects(List<MenuTreeObject> list, long typeId,String prefix){
        if(list == null) return null;
        for (Iterator<MenuTreeObject> iterator = list.iterator(); iterator.hasNext();) {
			MenuTreeObject node = (MenuTreeObject) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (node.getParentId().equals(String.valueOf(typeId))) {
                recursionFn(list, node,prefix);
            }
            // 二、遍历所有的父节点下的所有子节点
            /*if (node.getParentId()==0) {
                recursionFn(list, node);
            }*/
        }
        return returnList;
    }
     
    private void recursionFn(List<MenuTreeObject> list, MenuTreeObject node,String p) {
        List<MenuTreeObject> childList = getChildList(list, node);// 得到子节点列表
        if (hasChild(list, node)) {// 判断是否有子节点
            returnList.add(node);
            Iterator<MenuTreeObject> it = childList.iterator();
            while (it.hasNext()) {
				MenuTreeObject n = (MenuTreeObject) it.next();
                n.setMenuName(p+n.getMenuName());
                recursionFn(list, n,p+p);
            }
        } else {
            returnList.add(node);
        }
    }

	// 判断是否有子节点
	private boolean hasChild(List<MenuTreeObject> list, MenuTreeObject t) {
		return getChildList(list, t).size() > 0 ? true : false;
	}
	
}
