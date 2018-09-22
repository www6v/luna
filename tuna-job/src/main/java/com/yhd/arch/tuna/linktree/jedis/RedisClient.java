package com.yhd.arch.tuna.linktree.jedis;
import org.apache.log4j.Logger;
import com.ycache.redis.clients.jedis.ShardedJedis;
import com.ycache.redis.clients.jedis.ShardedJedisPool;
import com.yihaodian.common.yredis.client.RedisAdmin;
/**
 * redis客户端接口
 *
 * @author root
 *
 */
public class RedisClient {

	private static Logger log = Logger.getLogger(RedisClient.class);

	/**
	 * 线程变量
	 **/
	private static ThreadLocal<ShardedJedisPool> shardedPoolThreadLocal = new ThreadLocal<ShardedJedisPool>();

	public RedisClient() {

	}
	/**
	 * 获取连接
	 */
	final public ShardedJedis getConnection() {

		ShardedJedisPool shardedPool = null;
		if (shardedPoolThreadLocal.get() == null) {
			shardedPool = RedisAdmin.getBaseProxy("yihaodian_tuna");
			shardedPoolThreadLocal.set(shardedPool);
		} else {
			shardedPool = shardedPoolThreadLocal.get();
		}

		ShardedJedis jedis = shardedPool.getResource();
		return jedis;
	}

	/**
	 * 释放redis资源
	 */
	final public void returnResource(ShardedJedis jedis) {
		ShardedJedisPool shardedPool = null;
		try {
			shardedPool = shardedPoolThreadLocal.get();
			shardedPool.returnResource(jedis);
		} catch (Exception e) {
			log.error("=======释放Redis资源 JedisClient.returnResource=======", e);
			e.printStackTrace();
			if (shardedPool != null && jedis != null) {
				shardedPool.returnBrokenResource(jedis);
			}
		} finally {
			shardedPoolThreadLocal.remove();
		}
	}
}
//
//	ShardedJedis one = redisHelper.getJedisClient().getConnection();
//	ShardedJedisPipeline pipeline = one.pipelined();
//for (PolicySetting vo : list) {
//
//
//		String key = "keyaaaaaa";
//
//		try {
//		pipeline.hmset(key, policyfields);
//		//????????? ????????25??
//		pipeline.expire(key, CommonConst.REDIS_ONE_DAY);
//		} catch (Exception e) {
//		e.printStackTrace();
//		}
//		}
//
//		try {
//		pipeline.sync();
//
//
//		} catch (Exception e) {
//		e.printStackTrace();
//		} finally {
//		redisHelper.getJedisClient().returnResource(one);
//		}
