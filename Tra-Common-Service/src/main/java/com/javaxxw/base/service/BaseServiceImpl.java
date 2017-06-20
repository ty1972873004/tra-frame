package com.javaxxw.base.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.javaxxw.base.BaseMapper;
import com.javaxxw.base.model.BaseModel;
import com.javaxxw.common.constants.Constants;
import com.javaxxw.common.utils.DataUtil;
import com.javaxxw.common.utils.ExceptionUtil;
import com.javaxxw.common.utils.InstanceUtil;
import com.javaxxw.redis.service.CacheUtils;
import com.javaxxw.redis.service.JedisClient;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-15 15:35
 **/
@Service
public abstract class BaseServiceImpl<T extends BaseModel> implements BaseService<T>  {

    protected Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected BaseMapper<T> mapper;

    @Autowired
    protected JedisClient jedisClient;



    @Override
    public List<T> selectAll() {
        return null;
    }

    @Override
    public T queryById(Long id) {
        String key = getCacheKey(id);
        T record = (T) this.jedisClient.get(key);
        if (record == null) {
            String lockKey = getLockKey(id);
            if (CacheUtils.getLock(lockKey)) {
                try {
                    record = mapper.selectById(id);
                    jedisClient.set(key, record);
                } finally {
                    CacheUtils.unlock(lockKey);
                }
            } else {
                logger.debug(getClass().getSimpleName() + ":" + id + " retry queryById.");
                sleep(20);
                return queryById(id);
            }
        }
        return record;
    }

    @Transactional
    @Override
    public T insertOrUpdate(T t) {

        try {
            t.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            if (t.getId() == null) {
                t.setCreateTime(new Timestamp(System.currentTimeMillis()));
                mapper.insert(t);
            } else {
                T org = this.queryById(t.getId());
                String lockKey = getLockKey(t.getId());
                if (CacheUtils.getLock(lockKey)) {
                    try {
                        T update = InstanceUtil.getDiff(org, t);
                        update.setId(t.getId());
                        mapper.updateById(update);
                        t = mapper.selectById(t.getId());
                        CacheUtils.getCache().set(getCacheKey(t.getId()), t);
                    } finally {
                        CacheUtils.unlock(lockKey);
                    }
                } else {
                    sleep(20);
                    return insertOrUpdate(t);
                }
            }
        } catch (DuplicateKeyException e) {
            String msg = ExceptionUtil.getStackTraceAsString(e);
            logger.error(Constants.Exception_Head + msg, e);
            throw new RuntimeException("已经存在相同的配置.");
        } catch (Exception e) {
            String msg = ExceptionUtil.getStackTraceAsString(e);
            logger.error(Constants.Exception_Head + msg, e);
            throw new RuntimeException(msg);
        }
        return t;
    }


    @Transactional
    public int deleteByPrimaryKey(Long id) {
        Integer i=0;
        try {
            i=mapper.deleteById(id);
            CacheUtils.getCache().del(getCacheKey(id));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return i;
    }

    @Override
    public List<T> findTByT(T t) {
        return null;
    }

    @Override
    public T findTByTOne(T t) {
        return mapper.selectOne(t);
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(RandomUtils.nextLong(10, millis));
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }

    /** 分页查询 */
    public static Page<Long> getPage(Map<String, Object> params) {
        Integer current = 1;
        Integer size = 10;
        String orderBy = "id";
        if (DataUtil.isNotEmpty(params.get("pageNum"))) {
            current = Integer.valueOf(params.get("pageNum").toString());
        }
        if (DataUtil.isNotEmpty(params.get("pageSize"))) {
            size = Integer.valueOf(params.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(params.get("orderBy"))) {
            orderBy = (String) params.get("orderBy");
            params.remove("orderBy");
        }
        if (size == -1) {
            return new Page<Long>();
        }
        Page<Long> page = new Page<Long>(current, size, orderBy);
        page.setAsc(false);
        return page;
    }


    /** 根据Id查询(默认类型T) */
    public Page<Map<String, Object>> getPageMap(Page<Long> ids) {
        if (ids != null) {
            Page<Map<String, Object>> page = new Page<Map<String, Object>>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<Map<String, Object>> records = InstanceUtil.newArrayList();
            for (int i = 0; i < ids.getRecords().size(); i++) {
                records.add(null);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            for (int i = 0; i < ids.getRecords().size(); i++) {
                final int index = i;
                executorService.execute(new Runnable() {
                    public void run() {
                        records.set(index, InstanceUtil.transBean2Map(queryById(ids.getRecords().get(index))));
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.error("awaitTermination", "", e);
            }
            page.setRecords(records);
            return page;
        }
        return new Page<Map<String, Object>>();
    }


    /** 根据Id查询(默认类型T) */
    public Page<T> getPage(Page<Long> ids) {
        if (ids != null) {
            Page<T> page = new Page<T>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<T> records = InstanceUtil.newArrayList();
            for (int i = 0; i < ids.getRecords().size(); i++) {
                records.add(null);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            for (int i = 0; i < ids.getRecords().size(); i++) {
                final int index = i;
                executorService.execute(new Runnable() {
                    public void run() {
                        records.set(index, queryById(ids.getRecords().get(index)));
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.error("awaitTermination", "", e);
            }
            page.setRecords(records);
            return page;
        }
        return new Page<T>();
    }

    /** 根据Id查询(cls返回类型Class) */
    public <K> Page<K> getPage(Page<Long> ids, Class<K> cls) {
        if (ids != null) {
            Page<K> page = new Page<K>(ids.getCurrent(), ids.getSize());
            page.setTotal(ids.getTotal());
            List<K> records = InstanceUtil.newArrayList();
            for (int i = 0; i < ids.getRecords().size(); i++) {
                records.add(null);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            for (int i = 0; i < ids.getRecords().size(); i++) {
                final int index = i;
                executorService.execute(new Runnable() {
                    public void run() {
                        T t = queryById(ids.getRecords().get(index));
                        K k = InstanceUtil.to(t, cls);
                        records.set(index, k);
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.error("awaitTermination", "", e);
            }
            page.setRecords(records);
            return page;
        }
        return new Page<K>();
    }

    /** 根据Id查询(默认类型T) */
    public List<T> getList(List<Long> ids) {
        List<T> list = InstanceUtil.newArrayList();
        if (ids != null) {
            for (int i = 0; i < ids.size(); i++) {
                list.add(null);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            for (int i = 0; i < ids.size(); i++) {
                final int index = i;
                executorService.execute(new Runnable() {
                    public void run() {
                        list.set(index, queryById(ids.get(index)));
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.error("awaitTermination", "", e);
            }
        }
        return list;
    }

    /** 根据Id查询(cls返回类型Class) */
    public <K> List<K> getList(List<Long> ids, Class<K> cls) {
        List<K> list = InstanceUtil.newArrayList();
        if (ids != null) {
            for (int i = 0; i < ids.size(); i++) {
                list.add(null);
            }
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            for (int i = 0; i < ids.size(); i++) {
                final int index = i;
                executorService.execute(new Runnable() {
                    public void run() {
                        T t = queryById(ids.get(index));
                        K k = InstanceUtil.to(t, cls);
                        list.set(index, k);
                    }
                });
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                logger.error("awaitTermination", "", e);
            }
        }
        return list;
    }

    @Override
    public Page<T> query(Map<String, Object> params) {
        Page<Long> page = getPage(params);
        page.setRecords(mapper.selectIdPage(page, params));
        return getPage(page);
    }

    public List<T> queryList(Map<String, Object> params) {
        List<Long> ids = mapper.selectIdPage(params);
        List<T> list = getList(ids);
        return list;
    }

    protected <P> Page<P> query(Map<String, Object> params, Class<P> cls) {
        Page<Long> page = getPage(params);
        page.setRecords(mapper.selectIdPage(page, params));
        return getPage(page, cls);
    }

    /** 获取缓存键值 */
    protected String getCacheKey(Object id) {
        String cacheName = getCacheKey();
        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":").append(id).toString();
    }

    /** 获取缓存键值 */
    protected String getLockKey(Object id) {
        String cacheName = getCacheKey();
        return new StringBuilder(Constants.CACHE_NAMESPACE).append(cacheName).append(":LOCK:").append(id).toString();
    }

    private String getCacheKey() {
        Class<?> cls = getClass();
        String cacheName = Constants.cacheKeyMap.get(cls);
        if (StringUtils.isBlank(cacheName)) {
            CacheConfig cacheConfig = cls.getAnnotation(CacheConfig.class);
            if (cacheConfig == null || cacheConfig.cacheNames() == null || cacheConfig.cacheNames().length < 1) {
                cacheName = getClass().getName();
            } else {
                cacheName = cacheConfig.cacheNames()[0];
            }
            Constants.cacheKeyMap.put(cls, cacheName);
        }
        return cacheName;
    }
}
