package com.javaxxw.redis.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tuyong
 * @version 1.0
 * @DESCRIPTION
 * @create 2017-06-15 14:14
 **/
@Configuration
public class CacheUtils {
    private static Logger logger = LogManager.getLogger(CacheUtils.class);

    private static JedisClient jedisClient;
    private static JedisClientSingleImpl jedisClientSingleImpl;

    @Bean
    public JedisClient setCache() {
        jedisClient = getCache();
        return jedisClient;
    }

    public static JedisClient getCache() {
        if (jedisClient == null) {
            synchronized (CacheUtils.class) {
                if (jedisClient == null) {
                    jedisClient = new JedisClientSingleImpl();
                }
            }
        }
        return jedisClient;
    }

    @Bean
    public JedisClientSingleImpl setRedisHelper() {
        jedisClientSingleImpl = getJedisClientSingle();
        return jedisClientSingleImpl;
    }
    public static JedisClientSingleImpl getJedisClientSingle() {
        if (jedisClientSingleImpl == null) {
            synchronized (CacheUtils.class) {
                if (jedisClientSingleImpl == null) {
                    jedisClientSingleImpl = new JedisClientSingleImpl();
                }
            }
        }
        return jedisClientSingleImpl;
    }


    public  static boolean getLock(String key) {
        try {
            if (!getJedisClientSingle().exists(key)) {
                synchronized (CacheUtils.class) {
                    if (!jedisClient.exists(key)) {
                        if (jedisClient.setnx(key, String.valueOf(System.currentTimeMillis())) == 1) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getLock", e);
        }
        int expires = 1000 * 60 * 3;
        String currentValue = (String) jedisClient.get(key);
        if (currentValue != null && Long.parseLong(currentValue) < System.currentTimeMillis() - expires) {
            if (jedisClient.setnx("UNLOCK_" + key, "0")==1) {
                unlock(key);
                jedisClient.set("UNLOCK_" + key, "0", 1);
            }
            return getLock(key);
        }
        return false;
    }

    public  static void unlock(String key) {
        jedisClient.unlock(key);
    }

}
