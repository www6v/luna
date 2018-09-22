package com.yhd.arch.tuna.dao.impl

import java.util

import com.yhd.arch.tuna.linktree.jedis.RedisClient
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import org.apache.spark.SparkFiles
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
  * Created by root on 9/12/16.
  */
object RedisService {
  private var appContext: ClassPathXmlApplicationContext         = null
  private var redisProxyDao: RedisProxyDaoImpl                   =null
  private var redisClient:RedisClient                            =null

  try{
      val gp: String = System.getProperty("global.config.path")
      if (HedwigUtil.isBlankString(gp)) {
        val sparkfiles=SparkFiles.get("env.ini").replaceAll("env.ini","")
        if(sparkfiles!=null) {
          println("Redis.init","global.config.path", sparkfiles)
          System.setProperty("global.config.path", sparkfiles)
        }
      }
      appContext = new ClassPathXmlApplicationContext("applicationContext.xml")
      redisProxyDao = appContext.getBean("redisProxyDaoImpl").asInstanceOf[RedisProxyDaoImpl]
      redisClient=new RedisClient

  }catch{
    case ex:Exception=>println("redisService is error",ex)
  }

  def getRedisDao():RedisProxyDaoImpl={
    redisProxyDao
  }

  def getRedisClient():RedisClient={
    redisClient
  }
}