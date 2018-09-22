package com.yhd.arch.tuna.linktree.dao.service.reids

import java.util
import java.util.Date

import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.constants.LinkKeyType
import com.yhd.arch.tuna.dao.impl.RedisProxyDaoImpl
import com.yhd.arch.tuna.linktree.dao.RedisDao
import com.yhd.arch.tuna.linktree.util.InternalConstant
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import org.springframework.context.support.ClassPathXmlApplicationContext

import scala.collection.JavaConversions._
/**
  * Created by root on 2/10/17.
  */
class MetricErrRedis extends RedisDao{
  var errorList:util.ArrayList[util.HashMap[String,util.Set[String]]]=null
  override def handler(pipeline:ShardedJedisPipeline): Unit ={
    handlerErrorData(pipeline)
  }

  def handlerErrorData(pipeline:ShardedJedisPipeline): Unit ={
    val list=getErrorList()
    val time=new Date().getTime()

    for(map<-list){
      for(entry<-map.entrySet()){
        val key=entry.getKey+"@"+LinkKeyType.ERROR_MATCH_KEY.getCode
        val errorSet=entry.getValue
        for(errorValue<-errorSet){
          pipeline.sadd(key,errorValue)
          pipeline.expire(key,ParamConstants.METRIC_EXPIRE)

          pipeline.hset(LinkKeyType._ERRORINFO_INDEX_KEY.getCode,errorValue,""+time)
         // pipeline.sadd(LinkKeyType._ERRORINFO_INDEX_KEY.getCode,errorValue)
        }
      }
    }
  }

  def setErrorList(errorList:util.ArrayList[util.HashMap[String,util.Set[String]]]): Unit ={
    this.errorList=errorList
  }

  def getErrorList(): util.ArrayList[util.HashMap[String,util.Set[String]]] ={
    return errorList
  }
}
object TestRedis{
  def main(args: Array[String]) {
    val redisProxyDaoImpl=init()
    val map:util.Map[String,util.Map[String,String]]=new util.HashMap[String,util.Map[String,String]]()
    val hostMap:util.Map[String,String]=new util.HashMap[String,String]()
    hostMap.put("testRedis1","testRedisto")
    map.put("spanId0",hostMap)
    redisProxyDaoImpl.hset("spanId0","lalal","sadas")
    val hostMetricRedis=new HostMetricRedis
    hostMetricRedis.setData(map)
    hostMetricRedis.saveToRdis()
    println(redisProxyDaoImpl.hgetAll("spanId0"))
//    val set=new util.HashSet[String]()
//    set.add("testspan1")
//    set.add("testspan2")
//    val map=new util.HashMap[String,util.Set[String]]()
//    map.put("metricKey",set)
//    val list=new util.ArrayList[util.HashMap[String,util.Set[String]]]()
//    list.add(map)
//    val metricErrRedis=new MetricErrRedis
//    metricErrRedis.setErrorList(list)
//    metricErrRedis.saveToRdis()
//    println(redisProxyDaoImpl.smembers("metricKey"+LinkKeyType.ERROR_MATCH_KEY.getCode))
  }
  def init(): RedisProxyDaoImpl ={
    val gp: String = System.getProperty("global.config.path")
    if (HedwigUtil.isBlankString(gp)) {
      System.out.println("global.config.path " + InternalConstant.global_config_path)
      System.setProperty("global.config.path", InternalConstant.global_config_path)
    }
    val appContext: ClassPathXmlApplicationContext = new ClassPathXmlApplicationContext("applicationContext.xml")
    val redisProxyDao = appContext.getBean("redisProxyDaoImpl").asInstanceOf[RedisProxyDaoImpl]
    redisProxyDao
  }
}
