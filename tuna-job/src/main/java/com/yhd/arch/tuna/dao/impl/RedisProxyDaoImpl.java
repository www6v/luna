package com.yhd.arch.tuna.dao.impl;

import com.ycache.redis.clients.jedis.BinaryClient;
import com.ycache.redis.clients.jedis.Jedis;
import com.ycache.redis.clients.jedis.JedisPubSub;
import com.ycache.redis.clients.jedis.ShardedJedisPipelineUtils;
import com.yhd.arch.tuna.dao.IRedisProxyDao;
import com.yihaodian.common.yredis.RedisProxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 9/9/16.
 */
public class RedisProxyDaoImpl implements IRedisProxyDao{
	private RedisProxy redisProxy;

	public void setRedisProxy(RedisProxy redisProxy) {
		this.redisProxy = redisProxy;
	}

	@Override
	public ShardedJedisPipelineUtils buildPipelineUtils() {
		return redisProxy.buildPipelineUtils();
	}

	@Override
	public long rpush(String key, String string) {
		return redisProxy.rpush(key, string);
	}

	@Override
	public long rpush(byte[] key, byte[] string) {
		return redisProxy.rpush(key, string);
	}

	@Override
	public long sadd(String key, String... members) {
		return redisProxy.sadd(key, members);
	}

	@Override
	public long sadd(byte[] key, byte[]... members){
		return redisProxy.sadd(key, members);
	}

	public long sadds(String key, String[] members) {
		return redisProxy.sadd(key, members);
	}
	@Override
	public String lpop(String key) {
		return redisProxy.lpop(key);
	}

	@Override
	public String rpop(String key) {
		return redisProxy.rpop(key);
	}

	@Override
	public long llen(String key) {
		return redisProxy.llen(key);
	}

	@Override
	public long lrem(String key, long count, String value) {
		return redisProxy.lrem(key, count, value);
	}

	@Override
	public String ltrim(String key, long start, long end) {
		return redisProxy.ltrim(key, start, end);
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		return redisProxy.lrange(key, start, end);
	}

	@Override
	public Long hset(String key, String field, String value) {
		return redisProxy.hset(key, field, value);
	}

	@Override
	public Long hset(byte[] key, byte[] field, byte[] value){
		return redisProxy.hset(key,field,value);
	}

	@Override
	public String set(String key, String value) {
		return redisProxy.set(key, value);
	}

	@Override
	public String set(byte[] key, byte[] value) {
		return redisProxy.set(key, value);
	}

	@Override
	public byte[] get(byte[] key) {
		return redisProxy.get(key);
	}

	@Override
	public long del(String key) {
		return redisProxy.del(key);
	}

	@Override
	public String hmset(String key, Map<String, String> map) {
		return redisProxy.hmset(key, map);
	}

	@Override
	public String hmset(byte[] key, Map<byte[], byte[]> map){
		return redisProxy.hmset(key, map);
	}

	@Override
	public String setex(String key, int seconds, String value) {
		return redisProxy.setex(key, seconds, value);
	}

	@Override
	public Long expire(String key, int seconds) {
		return redisProxy.expire(key, seconds);
	}

	@Override
	public boolean exists(String key) {
		return redisProxy.exists(key);
	}

	@Override
	public String type(String key) {
		return redisProxy.type(key);
	}

	@Override
	public String hget(String key, String field) {
		return redisProxy.hget(key, field);
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		return redisProxy.hgetAll(key);
	}

	@Override
	public Map<String, String> hgetAll(String key, boolean order) {
		return redisProxy.hgetAll(key, order);
	}

	@Override
	public Map<String, String> hgetAllToLinkedHashMap(String key) {
		return redisProxy.hgetAllToLinkedHashMap(key);
	}

	@Override
	public Set<String> smembers(String key) {
		return redisProxy.smembers(key);
	}

	@Override
	public Long srem(String key, String... field) {
		return redisProxy.srem(key, field);
	}

	@Override
	public boolean sismember(String key, String field) {
		return redisProxy.sismember(key, field);
	}

	@Override
	public Long append(String key, String value) {
		return redisProxy.append(key, value);
	}

	@Override
	public Long decr(String key) {
		return redisProxy.decr(key);
	}

	@Override
	public Long decrBy(String key, Integer integer) {
		return redisProxy.decrBy(key, integer);
	}

	@Override
	public String getrange(String key, int startOffset, int endOffset) {
		return redisProxy.getrange(key, startOffset, endOffset);
	}

	@Override
	public String getSet(String key, String value) {
		return redisProxy.getSet(key, value);
	}

	@Override
	public Long hdel(String key, String... fields) {
		return redisProxy.hdel(key, fields);
	}

	@Override
	public Boolean hexists(String key, String fields) {
		return redisProxy.hexists(key, fields);
	}

	@Override
	public Long hincrBy(String key, String field, int value) {
		return redisProxy.hincrBy(key, field, value);
	}

	@Override
	public Set<String> hkeys(String key) {
		return redisProxy.hkeys(key);
	}

	@Override
	public Long hlen(String key) {
		return redisProxy.hlen(key);
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		return redisProxy.hmget(key, fields);
	}

	public List<String> hmgets(String key, String[] fields) {
		return redisProxy.hmget(key, fields);
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		return redisProxy.hsetnx(key, field, value);
	}

	@Override
	public List<String> hvals(String key) {
		return redisProxy.hvals(key);
	}

	@Override
	public Long incr(String key) {
		return redisProxy.incr(key);
	}

	@Override
	public boolean electioneer(String key, String candidates, int timeOut) {
		return redisProxy.electioneer(key, candidates, timeOut);
	}

	@Override
	public Long incrBy(String key, int integer) {
		return redisProxy.incrBy(key, integer);
	}

	@Override
	public void subscribe(String shardkey, JedisPubSub jedisPubSub,
			String... channels) {
		redisProxy.subscribe(shardkey, jedisPubSub, channels);
	}

	@Override
	public void publish(String shardkey, String channel, String message) {
		redisProxy.publish(shardkey, channel, message);
	}

	@Override
	public Jedis getJedisByShardKey(String shardKey) {
		return redisProxy.getJedisByShardKey(shardKey);
	}

	@Override
	public int getShardIndex(String uri, String key) {
		return redisProxy.getShardIndex(uri, key);
	}

	@Override
	public String get(String key) {
		return redisProxy.get(key);
	}

	@Override
	public long lpush(String key, String string) {
		return redisProxy.lpush(key, string);
	}

	@Override
	public long rpushx(String key, String string) {
		return redisProxy.rpushx(key, string);
	}



	@Override
	public long lpushx(String key, String string) {
		return redisProxy.lpushx(key, string);
	}

	@Override
	public String lindex(String key, long index) {
		return redisProxy.lindex(key, index);
	}

	@Override
	public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot,
			String value) {
		return redisProxy.linsert(key, where, pivot, value);
	}

	@Override
	public String lset(String key, long index, String value) {
		return redisProxy.lset(key, index, value);
	}

	@Override
	public long ttl(String key) {
		return redisProxy.ttl(key);
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		return redisProxy.expireAt(key, unixTime);
	}

	@Override
	public Long scard(String key) {
		return redisProxy.scard(key);
	}

	@Override
	public String spop(String key) {
		return redisProxy.spop(key);
	}

	@Override
	public String srandmember(String key) {
		return redisProxy.srandmember(key);
	}
}
