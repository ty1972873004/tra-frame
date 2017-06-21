package com.javaxxw.common.utils;


import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;


/**
 * @desc 扩展org.apache.commons.beanutils.BeanUtils。
 * @author tuyong
 * @since 2017/6/21
 * @version 1.0
 */
public class BeanUtils {
    /** 
     * 将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性 
     *  
     * @param dest 
     *            目标对象，标准的JavaBean 
     * @param orig 
     *            源对象，可为Map、标准的JavaBean
     */  
    @SuppressWarnings("rawtypes")  
    public static void applyIf(Object dest, Object orig) throws Exception {  
        try {  
            if (orig instanceof Map) {  
                Iterator names = ((Map) orig).keySet().iterator();  
                while (names.hasNext()) {  
                    String name = (String) names.next();  
                    if (PropertyUtils.isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);  
                        if (value != null) {  
                            PropertyUtils.setSimpleProperty(dest, name, value);  
                        }  
                    }  
                }  
            } else {  
                Field[] fields = orig.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {  
                    String name = fields[i].getName();
                    Field destField= ReflectionUtils.findField(dest.getClass(), name);
                    if(destField==null){
                        continue;
                    }
                    if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {  
                        Object value = PropertyUtils.getSimpleProperty(orig, name);  
                        if (value != null) {  
                            PropertyUtils.setSimpleProperty(dest, name, value);  
                        }  
                    }  
                }  
            }  
        } catch (Exception e) {  
            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);  
        }  
    }  
  

    public static boolean checkObjProperty(Object orig, Map dest) throws Exception {  
        try {  
            Field[] fields = orig.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {  
                String name = fields[i].getName();  
                if (!dest.containsKey(name)) {  
                    if (PropertyUtils.isReadable(orig, name)) {  
                        Object value = PropertyUtils.getSimpleProperty(orig, name);  
                        if (value == null) {  
                            return true;  
                        }  
                    }  
                }  
            }  
            return false;  
        } catch (Exception e) {  
            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);  
        }  
    }

    public static <T extends Object> T flushObject(T t, Map<String, Object> params) {
        if (params == null || t == null)
            return t;

        Class<?> clazz = t.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName(); // 获取属性的名字
                    Object value = params.get(name);
                    if (value != null && !"".equals(value)) {
                        // 注意下面这句，不设置true的话，不能修改private类型变量的值
                        fields[i].setAccessible(true);
                        fields[i].set(t, value);
                    }
                }
            } catch (Exception e) {
            }

        }
        return t;
    }
}  