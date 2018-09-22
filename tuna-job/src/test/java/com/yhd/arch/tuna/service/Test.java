package com.yhd.arch.tuna.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ycache.redis.clients.jedis.Response;
import com.ycache.redis.clients.jedis.ShardedJedis;
import com.ycache.redis.clients.jedis.ShardedJedisPipeline;
import com.ycache.redis.clients.jedis.ShardedJedisPipelineUtils;
import com.yhd.arch.tuna.dao.impl.RedisProxyDaoImpl;

import com.yhd.arch.tuna.linktree.dto.LinkTreeParam;
import com.yhd.arch.tuna.linktree.jedis.RedisClient;
import com.yhd.arch.tuna.linktree.serialize.KryoSerializer;
import com.yhd.arch.tuna.linktree.serialize.KryoSerializers;
import com.yhd.arch.tuna.linktree.util.InternalConstant;
import com.yhd.arch.tuna.util.ParamConstants;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by root on 10/19/16.
 */
public class Test {
	public static void main(String[] args){
		init();
		//testRedisClient();
		indexLink();
//		readRedis();
//		expire();
		//		System.out.println(args[0]);
//		if(args.length>0) {
//			if (args[0].equals("readData")){
//				readData();
//			}else if(args[0].equals("delData")){
//				delData();
//			}else {
//				//	del();
//				read(args[0]);
//			}
//		}
	}

	static RedisProxyDaoImpl redisProxyDao= null;
	static RedisClient redisClient=null;

	public static String HASH_LINK_TEST_KEY = "global_test_key";

	public static String TEST_INDEX_KEY = "TEST_index_key";

	public static String _LinkInfo_INDEX_KEY                               ="linkinfo_index_key";

	public static String _errorInfo_INDEX_KEY                               ="errorInfo_index_key";

	public static String _SPARNID_INDEX_KEY                                ="spanid_index_key";
	public static String _LINK_KEY                                         ="linkdata_key";

	public static String lINK_INFO_TOJSON                                  ="linkInfo";

	public static void readRedis(){
//		System.out.println(redisProxyDao.hget(_LINK_KEY,"84a0c8b329f2e3d08173d4b1601859fed34d1b67"));
//		Map<String,String> map=redisProxyDao.hgetAll(HASH_LINK_TEST_KEY);
//		System.out.println(map.size());
//		System.out.println(redisProxyDao.smembers(TEST_INDEX_KEY).size());
//		System.out.println(redisProxyDao.hlen(_LINK_KEY));
//		System.out.println(redisProxyDao.smembers(_SPARNID_INDEX_KEY).size());
//		System.out.println(redisProxyDao.get("c472cb41e39ae2b56c28fe93ba1f6543"));
		System.out.println(""+redisProxyDao.get("d370c53cea6a61a05249ac941f02584eefad734c@"+_LinkInfo_INDEX_KEY));
	}

	public static void expire(){
		redisProxyDao.hset("Hello","ex","sad");
		redisProxyDao.hset("Hello3","ex","sad");
		redisProxyDao.expire("Hello",2);
		redisProxyDao.expire("Hello3",2);
		try{
			Thread.sleep(1500);
			redisProxyDao.hset("Hello","exs","sads");
			redisProxyDao.expire("Hello",2);
			Thread.sleep(1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("Hello3: "+redisProxyDao.hgetAll("Hello3")+" Hello:"+redisProxyDao.hgetAll("Hello"));
	}

	public static void testRedisClient(){
		ShardedJedis shardedJedis=redisClient.getConnection();
		ShardedJedisPipeline pipeline=shardedJedis.pipelined();
		String hello="hello";

		pipeline.set(hello.getBytes(),hello.getBytes());
		Response<byte[]> response=pipeline.get(hello.getBytes());

		try{
			pipeline.sync();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			redisClient.returnResource(shardedJedis);
		}
		byte[] arg=response.get();
		System.out.println(new String(arg));
	}

	public static void readByte(){
		String linkId="f82348114c3936f3e83651d032609c1030d55743";
		String newlinkId = linkId + "@" + _LINK_KEY;
		byte[] linkidByte = newlinkId.getBytes();
		byte[] treeData = redisProxyDao.get(linkidByte);
		KryoSerializers kryoSerializers = new KryoSerializers();
		LinkTreeParam treeNode =(LinkTreeParam)kryoSerializers.deserialize(treeData);
		System.out.println(treeNode);
	}

	public static void read(String id){
		System.out.println(redisProxyDao.hget(_LINK_KEY,id));
	}

	public static void readData(){
		Map<String,String> map=redisProxyDao.hgetAll(_LinkInfo_INDEX_KEY);
		List<String> list=new ArrayList<String>();
		list.add("cd186e18c2a8900bf343332a56ecd7a4854ef4b5");
		list.add("c991da998ba89f8fd28b75cb01c22870d3160875");
		list.add("155ab600b37b55858c76cc227262c9ab88874986");
		list.add("e78d5302bbeaf4df06dfe8f0338aa7bb0a939844");
		list.add("bc2b43ca689b4ce0289e6c5913c81e21fb458f4a");
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(new File("/tmp/deletelinkid.txt"),true);
			long count=0;
			for(Map.Entry<String,String> entry:map.entrySet()){
				String linkid=entry.getKey();
				if(!list.contains(linkid)) {
					String linkinfostring = entry.getValue();
					JSONObject jsonObject = JSON.parseObject(linkinfostring);
					linkinfostring = jsonObject.getString(lINK_INFO_TOJSON);
					LinkTreeInfo linktreeInfo = JSON.parseObject(linkinfostring, LinkTreeInfo.class);
					if(linktreeInfo.getLinkCounts()<10){
						count++;
						outputStream.write(linkinfostring.getBytes());
						outputStream.flush();
					}

				}
			}

			outputStream.close();
			System.out.println("linktreeInfo.getLinkCounts()<10:"+count);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	public static void delData(){
		Map<String,String> map=redisProxyDao.hgetAll(_LinkInfo_INDEX_KEY);
		List<String> list=new ArrayList<String>();
		list.add("cd186e18c2a8900bf343332a56ecd7a4854ef4b5");
		list.add("c991da998ba89f8fd28b75cb01c22870d3160875");
		list.add("155ab600b37b55858c76cc227262c9ab88874986");
		list.add("e78d5302bbeaf4df06dfe8f0338aa7bb0a939844");
		list.add("bc2b43ca689b4ce0289e6c5913c81e21fb458f4a");
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(new File("/tmp/deletelinkid.txt"),true);

		for(Map.Entry<String,String> entry:map.entrySet()){
			String linkid=entry.getKey();
			if(!list.contains(linkid)) {
				String linkinfostring = entry.getValue();
				JSONObject jsonObject = JSON.parseObject(linkinfostring);
				linkinfostring = jsonObject.getString(lINK_INFO_TOJSON);
				LinkTreeInfo linktreeInfo = JSON.parseObject(linkinfostring, LinkTreeInfo.class);
				if(linktreeInfo.getLinkCounts()<10){
					long len=redisProxyDao.llen(linkid);
					List<String> spanList=redisProxyDao.lrange(linkid,0,len);
					Set<String> set=new HashSet<String>();
					set.addAll(spanList);
					Iterator<String> iterator=set.iterator();
					ShardedJedisPipelineUtils pipeline= redisProxyDao.buildPipelineUtils();
					while(iterator.hasNext()){
						String spanid=iterator.next();
						String metricKey=linkid+":"+spanid;
						pipeline.del(metricKey);
						pipeline.hdel(_LinkInfo_INDEX_KEY,linkid);
						pipeline.hdel(_LINK_KEY,linkid);
						pipeline.srem(spanid,linkid);
					}
					pipeline.sync();
					outputStream.write(linkinfostring.getBytes());
					outputStream.flush();
				}

			}
		}
			outputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void delAll(){
		redisProxyDao.del(_LINK_KEY);
		redisProxyDao.del(_errorInfo_INDEX_KEY);
		redisProxyDao.del(_LinkInfo_INDEX_KEY);
		Set<String> set = redisProxyDao.smembers(_SPARNID_INDEX_KEY);
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			redisProxyDao.del(key);
		}
		redisProxyDao.del(_SPARNID_INDEX_KEY);

		System.out.println("del is over");

	}
	public static  void init(){
		String gp = System.getProperty("global.config.path");
		if (HedwigUtil.isBlankString(gp)) {
			System.out.println("global.config.path "+ InternalConstant.global_config_path);
			System.setProperty("global.config.path", InternalConstant.global_config_path);
		}
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		redisProxyDao = (RedisProxyDaoImpl)appContext.getBean("redisProxyDaoImpl");
		redisClient=new RedisClient();
	}
	public static void test(){
		testpush();
		//System.out.println(redisProxyDao.smembers("TEST_index_key").size());
	}
	public static void testpush(){
		long l=redisProxyDao.lpush("TEST_index_","test1");
		redisProxyDao.lpush("TEST_index_","test2");
		System.out.println("lpush:"+l);
		List<String> list=redisProxyDao.lrange("TEST_index_",0,-1);
		System.out.println("list contemt :"+list);
	}
	public static void testsadd(){
		String[] args=new String[]{"value1","value2","value1"};
		Set<String> set=new HashSet<String>();
		set.add("va1");
		set.add("va2");
		System.out.println(redisProxyDao.sadd("rootTest6","va"));
		String[] arg2=new String[set.size()];
		redisProxyDao.sadd("rootTest6",set.toArray(arg2));
		Long len=redisProxyDao.scard("rootTest6");
		Long lens=redisProxyDao.scard("_LINK_KEY@0");
		System.out.println(lens+" ");

	}

	public static void indexLink(){
		for(int index = ParamConstants.CONTAINER_SIZE();index>0;index--){
			String key=ParamConstants._LINK_KEY()+"@"+index;
			Set<String> set=redisProxyDao.smembers(key);
			System.out.println(set);
		}
	}
}
