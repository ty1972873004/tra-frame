package com.javaxxw.redis.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class JedisClientClusterImpl implements JedisClient {

	@Autowired
	private JedisCluster jedisCluster;

	@Override
	public String set(byte[] key, byte[] value) {
		return null;
	}

	@Override
	public  Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public Long setnx(String key, String value) {
		return jedisCluster.setnx(key,value);
	}

	@Override
	public void unlock(String key) {
		 del(key);
	}

	@Override
	public Object get(String key) {
		return jedisCluster.get(key);
	}

	@Override
	public byte[] get(byte[] key) {
		return new byte[0];
	}

	@Override
	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	@Override
	public String set(String key, String value, int expire) {
		value = jedisCluster.set(key, value);
		if (expire != 0) {
			jedisCluster.expire(key, expire);
		}
		return value;
	}

	@Override
	public String set(final String key, final Serializable value) {
		return jedisCluster.set(key, JSON.toJSONString(value));
	}

	@Override
	public String set(byte[] key, byte[] value, int expire) {
		return null;
	}

	@Override
	public String hget(String hkey, String key) {
		return jedisCluster.hget(hkey, key);
	}

	@Override
	public long hset(String hkey, String key, String value) {
		return jedisCluster.hset(hkey, key, value);
	}

	@Override
	public long incr(String key) {
		return jedisCluster.incr(key);
	}

	@Override
	public long expire(String key, int second) {
		return jedisCluster.expire(key, second);
	}

	@Override
	public long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	@Override
	public long del(String key) {
		
		return jedisCluster.del(key);
	}

	@Override
	public long del(byte[] key) {
		return 0;
	}

	@Override
	public long hdel(String hkey, String key) {
		
		return jedisCluster.hdel(hkey, key);
	}

	@Override
	public Set<byte[]> keys(String pattern) {
		return null;
	}

	@Override
	public void flushDB() {

	}

	@Override
	public Long dbSize() {
		return null;
	}

	@Override
	public Long srem(String key, String... members) {
		return jedisCluster.srem(key,members);
	}

	@Override
	public void sadd(String key, String value, int seconds) {
		jedisCluster.sadd(key, value);
		jedisCluster.expire(key, seconds);
	}

	@Override
	public Set<String> smembers(String key) {
		return jedisCluster.smembers(key);
	}

	@Override
	public Long scard(String key) {
		return jedisCluster.scard(key);
	}

	@Override
	public void lrem(String key, long count, String value) {
		jedisCluster.lrem(key, count, value);
	}

	@Override
	public Long llen(String key) {
		return jedisCluster.llen(key);
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		return jedisCluster.lrange(key, start, end);
	}

	@Override
	public void lpush(String key, String... strings) {
		jedisCluster.lpush(key,strings);
	}
}
