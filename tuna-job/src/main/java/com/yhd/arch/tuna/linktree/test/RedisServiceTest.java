package com.yhd.arch.tuna.linktree.test;

import com.yhd.arch.tuna.dao.impl.RedisProxyDaoImpl;
import com.yhd.arch.tuna.linktree.util.InternalConstant;
import com.yihaodian.architecture.hedwig.common.hash.HashFunction;
import com.yihaodian.architecture.hedwig.common.hash.HashFunctionFactory;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.monitor.util.JenkinsHash;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

/**
 * Created by root on 9/9/16.
 */
public class RedisServiceTest {
	public static void main(String[] args){
		init();
		if(args.length>0){
			String linkid=args[0];
			readLinkId(linkid);
		}
		//testMHash();
	}
	static RedisProxyDaoImpl redisProxyDao=null;

	private static String link_key="linkdata_key";

	public static void readLinkId(String linkId){
		String linkid=linkId+"@"+link_key;
		System.out.println(redisProxyDao.get(linkid));
	}
	public static void test(){
	//	testpush();
		Map<String,String > map=redisProxyDao.hgetAll(link_key);
		for(Map.Entry<String,String> entry:map.entrySet()){
			System.out.println(entry.getKey()+" "+entry.getValue());
		}

		System.out.println("over");
	}
	public static  void init(){
		String gp = System.getProperty("global.config.path");
		if (HedwigUtil.isBlankString(gp)) {
			System.out.println("global.config.path "+ InternalConstant.global_config_path);
			System.setProperty("global.config.path", InternalConstant.global_config_path);
		}
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		redisProxyDao = (RedisProxyDaoImpl)appContext.getBean("redisProxyDaoImpl");
	}


	public static void testMHash(){
		Map<String,String> map=new HashMap<String, String>();
		for(int i=0;i<3;i++){
			map.put("str1_"+i, "value_"+i);
		}
		redisProxyDao.hmset("hello",map);
		Map<String,String> valuemap=redisProxyDao.hgetAll("hello");
		System.out.println(valuemap);
		Map<String,String> map3=new HashMap<String, String>();
		for(int i=0;i<3;i++){
			map3.put("str1_"+i, "value_"+i+3);
		}
		redisProxyDao.hmset("hello",map3);
		valuemap=redisProxyDao.hgetAll("hello");
		System.out.println(valuemap);
	}
	public static void testpush(){
		long l=redisProxyDao.lpush("rootTest","test1");
		redisProxyDao.lpush("rootTest","test2");
		System.out.println("lpush:"+l);
		List<String> list=redisProxyDao.lrange("rootTest",0,-1);
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
		System.out.println(redisProxyDao.smembers("rootTest6"));

	}

	public static void hash(){
		HashFunction df= HashFunctionFactory.getInstance().getMur2Function();

		String as="yyyyyyyyyyydasdas";
		String ss="yyyyyyyyyyydasdass/sadwdwdcvd][]wdcqwc";
		System.out.println (df.hash32(as));
		System.out.println (as.hashCode());
		System.out.println(new JenkinsHash().hash(as.getBytes()));
		System.out.println(new JenkinsHash().hash(as.getBytes()));
	}

}
