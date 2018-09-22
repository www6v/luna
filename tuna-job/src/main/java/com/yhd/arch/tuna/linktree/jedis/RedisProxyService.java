package com.yhd.arch.tuna.linktree.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;


/**
 *
 * 
 * Created by root on 9/22/16.
 *
 */
public class RedisProxyService {
    
	/** */
	public static Logger log = Logger.getLogger(RedisProxyService.class);
	
    /** 过期时间，24小时 */
    public static final int DEFAULT_EXPIRE = 86400;

    private JedisClient     jedisClient;

	private ShardedJedis    shardedJedis;
	private RedisProxyService(){
		initJC();
	}

	private void initJC(){
		jedisClient=new JedisClient();
	}


	private static final RedisProxyService INSTANCE = new RedisProxyService();


	public static RedisProxyService getInstance(){
		return INSTANCE;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 *     给定域的值, 当给定域不存在或是给定 key 不存在时, 返回 null
	 */
	public String hget(String key, String field) {
	    String value = null;
        ShardedJedis jedis = jedisClient.getConnection();
        try {
            value = jedis.hget(key, field);
        } catch (Exception e) {
            log.info("Redis Error! get key : " + key);
            e.printStackTrace();
        } finally {
            jedisClient.returnResource(jedis);
        }
        return value;
	}
	
	/**
	 * 给key的特定域的值增加量incre
	 * @param key 
	 * @param field
	 * @return
	 */
	public long hincrby (String key, String field, long incre) {
		ShardedJedis jedis = jedisClient.getConnection();
		jedis.hincrBy(key, field, incre);
		jedisClient.returnResource(jedis);
		return incre;
	}
	
	/**
	 * 设置Key 的过期时间，默认过期时间是24小时.
	 * <p>
	 * <b>若Key 存在，则更新Key的过期时间</b> 
	 * 
	 * @param key
	 * @param field
	 */
	public void hset(String key, String field, String value) {
	    ShardedJedis jedis = jedisClient.getConnection();
        try {
            jedis.hset(key, field, value);
            
            // 设置key的过期时间
            jedis.expire(key, DEFAULT_EXPIRE);
        } catch (Exception e) {
            log.info("Redis Error! hset key field value : " 
                     + key + ", " 
                     + field + ", "
                     + value);
            e.printStackTrace();
        } finally {
            jedisClient.returnResource(jedis);
        }
	}
	
	/**
	 * 返回哈希表 key 中域的数量.
	 * 
	 * @param key
	 * @return
	 */
	public Long hlen(String key) {
	    long len = -1L;
	    ShardedJedis jedis = jedisClient.getConnection();
	    try {
            len = jedis.hlen(key);
        } catch (Exception e) {
            log.info("Redis Error! hlen : " + key);
            e.printStackTrace();
        } finally {
            jedisClient.returnResource(jedis);
        }
        return len;
	}
	
	/**
	 * 获取key的剩余过期时间.
	 * 
	 * @param key
	 * @return 
	 *     当 key 不存在时，返回 -2
     *     当 key 存在但没有设置剩余生存时间时，返回 -1
     *     否则，以秒为单位，返回 key 的剩余生存时间.
	 */
	public Long ttl(String key) {
	    long t = -2;
	    ShardedJedis jedis = jedisClient.getConnection();
	    try {
	        t = jedis.ttl(key);
	    } finally {
	        jedisClient.returnResource(jedis);
	    }
	    return t;
	}

	/**
	 * 设置 key 指定的哈希集中指定字段的值。该命令将重写所有在哈希集中存在的字段。并设置过期时间
	 * 如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key 关联
	 * @param key
	 * @param map
	 * @see link http://redis.cn/commands/hmset.html
	 * @return OK
	 */
	public void hmset(String key, Map<String, String> map) {
	    ShardedJedis jedis = jedisClient.getConnection();
        try {
            jedis.hmset(key, map);
            
            // 设置key的过期时间
            jedis.expire(key, DEFAULT_EXPIRE);
        } catch (Exception e) {
            log.info("Redis Error! hmset key field value : " 
                     + key + ", " 
                     + map);
            e.printStackTrace();
        } finally {
            jedisClient.returnResource(jedis);
        }
	}
	
	/**
	 * 返回 key 指定的哈希集中指定字段的值。 对于哈希集中不存在的每个字段，返回 nil 值。
	 * 因为不存在的keys被认为是一个空的哈希集，对一个不存在的 key 执行 HMGET 将返回一个只含有 nil 值的列表 返回值 多个返回值：含有给定字段及其值的列表，并保持与请求相同的顺序。
	 * @param key
	 * @param fields
	 * @see link http://redis.cn/commands/hmset.html
	 * @return List<String>
	 */
	public List<String> hmget(String key, String... fields) {
		ShardedJedis jedis = jedisClient.getConnection();
		try {
			return jedis.hmget(key, fields);
			
		} catch (Exception e) {
			log.info("Redis Error! hget key field value : " 
					+ key + ", " 
					+ fields);
			e.printStackTrace();
		} finally {
			jedisClient.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 管道
	 * @return 
	 */
	public ShardedJedisPipeline pipelined() {
	    ShardedJedis jedis = jedisClient.getConnection();
        try {
        	return jedis.pipelined();
            
        } catch (Exception e) {
            log.info("Redis Error! pipelined");
            e.printStackTrace();
        } 
        return null;
	}

	/**
	 * 管道
	 * @return
	 */
	public ShardedJedisPipeline pipelined(ShardedJedis jedis) {
		try {
			return jedis.pipelined();

		} catch (Exception e) {
			log.info("Redis Error! pipelined");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public ShardedJedis getShardedJedis(){
		ShardedJedis jedis = jedisClient.getConnection();
		return jedis;
	}
	
	/**
	 * 獲取所有key
	 * @return 
	 */
	public Set<String> keys(String key) {
		ShardedJedis jedis = jedisClient.getConnection();
		try {
			return jedis.hkeys(key);
			
		} catch (Exception e) {
			log.info("Redis Error! keys : " + key);
			e.printStackTrace();
		} finally {
			jedisClient.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 根据key获取map
	 * @return 
	 */
	public Map<String,String> hgetAll(String key) {
		ShardedJedis jedis = jedisClient.getConnection();
		try {
			return jedis.hgetAll(key);
			
		} catch (Exception e) {
			log.info("Redis Error! hgetAll : " + key);
			e.printStackTrace();
		} finally {
			jedisClient.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 释放连接
	 * @return 
	 */
	public void disconnect() {
		ShardedJedis jedis = jedisClient.getConnection();
		try {
			jedis.disconnect();
			
		} catch (Exception e) {
			log.info("Redis Error! disconnect");
			e.printStackTrace();
		} finally {
			jedisClient.returnResource(jedis);
		}
	}
	
	/**
	 * 删除指定key数据
	 * @param key 
	 * @param 
	 * @return
	 */
	public Long del(String key) {
		ShardedJedis jedis = jedisClient.getConnection();
		try {
			return jedis.del(key);
			
		} catch (Exception e) {
			log.info("Redis Error! del key:" + key);
			e.printStackTrace();
		} finally {
			jedisClient.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 
	 * @return jedisClient
	 */
	public JedisClient getJedisClient() {
		return jedisClient;
	}

	/**
	 * 释放Redis资源.
	 *
	 * @param jedis
	 */
	final public void returnResource(ShardedJedis jedis) {
		this.jedisClient.returnResource(jedis);
	}


	/**
	 * 
	 * @param jedisClient
	 */
	public void setJedisClient(JedisClient jedisClient) {
		this.jedisClient = jedisClient;
	}
}

