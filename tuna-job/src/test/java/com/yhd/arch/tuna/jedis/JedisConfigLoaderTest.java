package com.yhd.arch.tuna.jedis;


import com.yhd.arch.tuna.linktree.util.ConfigureLoders;

/**
 * Created by root on 9/23/16.
 */
public class JedisConfigLoaderTest {
	public static  void main(String[] args){
		test();
	}

	public static void test(){
		ConfigureLoders configureLoders= ConfigureLoders.getInstance();

		System.out.println(configureLoders.getRedisUrl()+"  "+configureLoders.getRedisTimeOut());
	}
}
