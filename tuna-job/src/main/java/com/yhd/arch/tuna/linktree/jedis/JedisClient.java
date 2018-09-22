package com.yhd.arch.tuna.linktree.jedis;



import java.util.*;

import com.yhd.arch.tuna.linktree.util.ConfigureLoders;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

/**
 * redis客户端分布式接口.
 *
 * Created by root on 9/22/16.
 */

public class JedisClient {

	private static Logger log = Logger.getLogger(JedisClient.class);
	
	/** */
	private Set<ShardedJedis> connectionList = new HashSet<ShardedJedis>();

	/** */
	private ShardedJedisPool shardedPool;

	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
	private static int MAX_WAIT = 100000;
	
	private static int DEFAULT_TIME_OUT = 3000;

	public JedisClient(){
		init();
	}

	private void init(){
		ConfigureLoders instant=ConfigureLoders.getInstance();
		JedisPoolConfig config = instant.getJedisPoolConfig();
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		String redisUrl	=instant.getRedisUrl();
		if(redisUrl==null){
			log.error("获取Redis服务器的IP为null，请检查!!!");
		}
		int timeout=instant.getRedisTimeOut();
		if(timeout==0){
			timeout=DEFAULT_TIME_OUT;
		}
		String[] records = redisUrl.split(";");

		for(String record: records){
			String[] ipPort = record.split(":");
			shards.add(new JedisShardInfo(ipPort[0], Integer.parseInt(ipPort[1]), timeout));
		}

		this.shardedPool = new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH);
	}


	/**
	 * 
	 * @return
	 */
	final public ShardedJedis getConnection() {
		ShardedJedis jedis = this.shardedPool.getResource();
		this.connectionList.add(jedis);

		return jedis;
	}

	/**
	 * 释放Redis资源.
	 * 
	 * @param jedis
	 */
	final public void returnResource(ShardedJedis jedis) {
		this.shardedPool.returnResource(jedis);
	}

	/**
     *
     */
	final public void destoryPool() {
		Iterator<ShardedJedis> jedisList = this.connectionList.iterator();
		while (jedisList.hasNext()) {
			ShardedJedis jedis = jedisList.next();
			this.shardedPool.returnResource(jedis);
		}
		this.shardedPool.destroy();
	}
}
