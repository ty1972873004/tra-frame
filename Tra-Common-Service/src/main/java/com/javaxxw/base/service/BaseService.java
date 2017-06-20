package com.javaxxw.base.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.javaxxw.base.model.BaseModel;

import java.util.List;
import java.util.Map;

public interface BaseService<T extends BaseModel> {

    Page<T> query(Map<String, Object> params);

    List<T> queryList(Map<String, Object> params);

    List<T> selectAll();

    T queryById(Long id);

    T insertOrUpdate(T t);

    int deleteByPrimaryKey(Long id);

    List<T> findTByT(T t);

    T findTByTOne(T t);



}
