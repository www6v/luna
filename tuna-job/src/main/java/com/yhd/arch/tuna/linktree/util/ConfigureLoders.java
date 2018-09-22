package com.yhd.arch.tuna.linktree.util;

import com.yhd.arch.tuna.linktree.jedis.JedisClient;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by root on 9/23/16.
 */
public class ConfigureLoders {

	private static Logger log = Logger.getLogger(JedisClient.class);

	private ClassPathXmlApplicationContext applicationContext =null;

	private Properties jedisConfig =new Properties();

	private ConfigureLoders(){
		load();
		applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

	}

	private final static ConfigureLoders INSTANCE=new ConfigureLoders();

	public void load(){
		try {
			String gp =System.getProperty("global.config.path");
			if(HedwigUtil.isBlankString(gp)) {
				System.setProperty("global.config.path",  InternalConstant.global_config_path);

				log.warn("Can't find global config path,use default value:"+ InternalConstant.global_config_path);
			}

			Properties jedisProp=YccGlobalPropertyConfigurer.loadConfigProperties(InternalConstant.CONFIG_GROUP,
																					InternalConstant.CONFIG_TUNA_JEDIS,
																					false);
			if (jedisProp != null && jedisProp.size() > 0) {
				jedisConfig.putAll(jedisProp);
			}else{
				Properties prop=localLoad();
				if(prop.size()>0){
					jedisConfig.putAll(prop);
				}
			}
		}catch(Exception ex){
				log.error("get ycc property config is fail",ex);
		}
	}

	/**
	 * 加载本地属性文件
	 *
	 * @return properties*/
	public Properties localLoad(){
		Properties prop=new Properties();
		InputStream resourceStream=ConfigureLoders.class.getClassLoader().getResourceAsStream( InternalConstant.CONFIG_TUNA_JEDIS);
		try{
			prop.load(resourceStream);

			resourceStream.close();
			log.warn("load localfile: "+ InternalConstant.CONFIG_TUNA_JEDIS+" is successful");
		}catch(IOException e) {
			log.error("load localfile fail",e);
		}
		return prop;
	}

	public static ConfigureLoders getInstance(){
		return INSTANCE;
	}

	public JedisPoolConfig getJedisPoolConfig() {

		return (JedisPoolConfig)applicationContext.getBean("poolConfig");
	}

	public String getRedisUrl(){
		return jedisConfig.getProperty(InternalConstant.REDIS_URL);
	}

	public Integer getRedisTimeOut() {
		String timeout=jedisConfig.getProperty(InternalConstant.REDIS_TIMEOUT);
		if(timeout==null){
			return 0;
		}
		return Integer.parseInt(timeout);
	}


}
