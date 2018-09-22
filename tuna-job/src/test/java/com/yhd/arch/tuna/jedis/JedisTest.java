package com.yhd.arch.tuna.jedis;

import com.yhd.arch.tuna.linktree.jedis.RedisProxyService;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by root on 9/22/16.
 */
public class JedisTest {


	public void refreshRiskStatusCache() {
		Long start = System.currentTimeMillis();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("refreshRiskStatusCache startTime:" + (df.format(new Date())));
		RedisProxyService proxyService = RedisProxyService.getInstance();
		try {
			String redisIpPorts="";
//			String redisIpPorts = (String)csSysPropUtil.getValue("redis_ip", CsSysPropUtil.STRING);
//			String env = YccGlobalPropertyConfigurer.getEnv();// »ñÈ¡Ö´ÐÐ»·¾³
//			if(CommonConst.ENV_STG.equals(env)){
//				redisIpPorts = (String)csSysPropUtil.getValue("redis_stg_ip", CsSysPropUtil.STRING);
//			}
			String redisConfig = "";
//			(String)csSysPropUtil.getValue("maxActive_maxIdle_maxWait", CsSysPropUtil.STRING);
//			int pageSize = (Integer) csSysPropUtil.getValue("redis_pipeline_size", CsSysPropUtil.INTEGER);



			long maxId = 0;
			try {
				ShardedJedis one = proxyService.getShardedJedis();
				ShardedJedisPipeline pipeline = one.pipelined();
				List<Map<String, Object>> riskUserList= new ArrayList();

				for (Map<String, Object> riskUserMap : riskUserList) {
					Map<String, String> riskfields = new HashMap<>();
				//	String key =  MessageFormat.format(CommonConst.TNS_RISK_KEY, riskUserMap.get("endUserId").toString(),version);
					String key="";
					riskUserMap.remove("endUserId");
					riskUserMap.remove("id");


					Set<Map.Entry<String, Object>> entrySet = riskUserMap.entrySet();
					for(Map.Entry<String, Object> ds:entrySet){
						riskfields.put(ds.getKey(), ds.getValue()==null?"0":ds.getValue().toString());
					}
					//						redisService.hmsetWithExpire(key, riskfields,CommonConst.REDIS_ONE_DAY);

					try {
						pipeline.hmset(key, riskfields);

					//	pipeline.expire(key, EXPIRE_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//		        	pipeline.syncAndReturnAll();
				try {
					pipeline.sync();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					proxyService.getJedisClient().returnResource(one);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

	} catch (Exception e) {
	}finally {
		System.out.println("destory jedis Pool");
			proxyService.getJedisClient().destoryPool();
	}
}


}
