package com.javaxxw.redis.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class JedisClientSingleImpl implements JedisClient{
	
	@Autowired
	private JedisPool jedisPool;
	
	@Override
	public Object get(String key) {
		Object value = null;
		Jedis jedis = jedisPool.getResource();
		try{
			value = jedis.get(key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public byte[] get(byte[] key) {
		byte[] value = null;
		Jedis jedis = jedisPool.getResource();
		try{
			value = jedis.get(key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			value = jedis.set(key, value);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public String set(String key, String value, int expire) {
		Jedis jedis = jedisPool.getResource();
		try {
			value = jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public String set(final String key, final Serializable value) {
		Jedis jedis = jedisPool.getResource();
		String v=null;
		try {
			v =jedis.set(key, JSON.toJSONString(value));
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return v;
	}

	@Override
	public String set(byte[] key, byte[] value, int expire) {
		Jedis jedis = jedisPool.getResource();
		String v=null;
		try {
			v =jedis.set(key, value);
			if (expire != 0) {
				jedis.expire(key, expire);
			}
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return v;
	}

	@Override
	public String hget(String hkey, String key) {
		String value =null;
		Jedis jedis = jedisPool.getResource();
		try{
			value = jedis.hget(hkey, key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return value;
	}

	@Override
	public long hset(String hkey, String key, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result= jedis.hset(hkey, key, value);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.incr(key);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long expire(String key, int second) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.expire(key, second);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.ttl(key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long del(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.del(key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long del(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.del(key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public long hdel(String hkey, String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.hdel(hkey, key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Set<byte[]> keys(String pattern) {
		Set<byte[]> keys = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		}catch (Exception e){
			return null;
		}
		try {
			keys = jedis.keys(pattern.getBytes("utf-8"));
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return keys;
	}

	@Override
	public void flushDB() {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.flushDB();
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
	}

	@Override
	public Long dbSize() {
		Long dbSize = 0L;
		Jedis jedis = jedisPool.getResource();
		try {
			dbSize = jedis.dbSize();
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return dbSize;
	}

	@Override
	public String set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		String v=null;
		try {
			v =jedis.set(key, value);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return v;
	}

	@Override
	public  Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		Boolean result = null;
		try{
			result = jedis.exists((String) key);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public Long setnx(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.setnx(key, value);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void unlock(String key) {
		del(key);
	}

	@Override
	public Long srem(String key, String... members) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result = jedis.srem(key, members);
		} catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void sadd(String key, String value, int seconds) {
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.sadd(key, value);
			jedis.expire(key, seconds);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = jedisPool.getResource();
		Set<String> keys = null;
		try{
			keys=jedis.smembers(key);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return keys;
	}

	@Override
	public Long scard(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result=jedis.scard(key);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void lrem(String key, long count, String value) {
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.lrem(key, count, value);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
	}

	@Override
	public Long llen(String key) {
		Jedis jedis = jedisPool.getResource();
		Long result = Long.valueOf(0);
		try{
			result=jedis.llen(key);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = jedisPool.getResource();
		List<String> result=null;

		try{
			result=jedis.lrange(key, start, end);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
		return result;
	}

	@Override
	public void lpush(String key, String... strings) {
		Jedis jedis = jedisPool.getResource();
		try{
			jedis.lpush(key,strings);
		}catch (Exception e) {
			//jedisPool.returnBrokenResource(jedis);
		} finally {
			//jedisPool.returnResource(jedis);
			if(null != jedis){
				jedis.close();
			}
		}
	}
}
