package com.yhd.arch.tuna.util

import java.util.Date
import java.util

import com.alibaba.fastjson.JSON
import com.google.code.morphia.query.Query
import com.ycache.redis.clients.jedis.Response
import com.ycache.redis.clients.jedis.exceptions.JedisException
import com.yhd.arch.tuna.constants.{LinkKeyType, MetricConstants}
import com.yhd.arch.tuna.dao.impl.{RedisProxyDaoImpl, RedisService}
import com.yhd.arch.tuna.linktree.dao.service.InitMongoDao
import com.yhd.arch.tuna.linktree.dto.LinkTreeParam
import com.yhd.arch.tuna.metric.dto.SpanMetricAnalyse
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
/**
  * Created by root on 10/26/16.
  */
object CommonUtils {
  val logger= LoggerFactory.getLogger(CommonUtils.getClass)
  def readRedis2Linkid(spanIds:util.Set[String]): util.Set[String] ={
    val start=System.currentTimeMillis()

    var newSet:util.Set[String]=new util.HashSet[String]()
    val shardedJedis=RedisService.getRedisClient().getConnection

    val pipeline = shardedJedis.pipelined()

    val resultMap=new util.HashMap[String,Response[util.Map[String,String]]]()
    try{
      for(spanId<- spanIds){
          val response=pipeline.hgetAll(spanId)
        resultMap.put(spanId,response)
      }

      pipeline.sync()
    }catch{
      case ex:Exception=>
        logger.error("readRedis2Linkid( "+spanIds+" ) is error",ex)
    }finally {
      RedisService.getRedisClient().returnResource(shardedJedis)
    }
    
    val tmpmap:util.Map[String,Integer]=new util.HashMap[String,Integer]()
    val nowDate=new Date()
    try {
      for (result <- resultMap.entrySet()) {
        val response=result.getValue
        val map:util.Map[String,String]=response.get()
        for(entry<-map.entrySet()) {
          val linkid = entry.getKey
          val time = entry.getValue.toLong
          if (nowDate.getTime - time < ParamConstants.ONE_MIN * 10) {
            val sample = tmpmap.get(linkid)
            if (sample != null) {
              tmpmap.put(linkid, new Integer(2))
            } else {
              tmpmap.put(linkid, new Integer(1))
            }
          }
//          else{
//            RedisService.getRedisDao().hdel(result.getKey,linkid)
//          }
        }
      }
      for(entry<-tmpmap.entrySet()){
        if(entry.getValue==2){
          newSet.add(entry.getKey)
        }
      }
    }catch{
          case ex:Exception=>
            println("CommonUtils.readRedis2Linkid  is error",ex)
            logger.error("CommonUtils.readRedis2Linkid  is error",ex)
        }
 //   println("error linkid size:"+newSet.size(),"readRedis2Linkid cost time:"+(System.currentTimeMillis()-start))
    newSet
  }

  def getMetricByUniqReqId(UniqReqId:String): util.Map[String,MetricAnalyse] ={
    val redisProxyDao: RedisProxyDaoImpl=RedisService.getRedisDao()
    val metricMap:util.Map[String,MetricAnalyse]=new util.HashMap[String,MetricAnalyse]
    try {
    if(redisProxyDao.exists(UniqReqId)) {
      var set:util.Set[String] =null
        set = redisProxyDao.smembers(UniqReqId)

      if (set != null && set.size() > 0) {
        val responseMap = new util.HashMap[String, Response[String]]()
        val shardedJedis = RedisService.getRedisClient().getConnection
        val pipeline = shardedJedis.pipelined()
        try {
          for (metricKey <- set) {
            val response = pipeline.get(metricKey)
            responseMap.put(metricKey, response)
          }
          pipeline.sync()
        } catch {
          case ex: Exception =>
            println("getMetricByUniqReqId is error", ex)
            logger.error("getMetricByUniqReqId is error", ex)
        } finally {
          RedisService.getRedisClient().returnResource(shardedJedis)
        }

        var linkid:String=null
        for (responseEntry <- responseMap.entrySet()) {
          val string = responseEntry.getValue.get()
          if (string != null) {
            val metrickey = responseEntry.getKey
            val metricAnalyse =buildMetricAnalyse(string)

            metricMap.put(metricAnalyse.getSpanId, metricAnalyse)

            linkid=metrickey.split(">").apply(0)
          }
        }
        //删除已存储的子链路数据
        deleteData(linkid)
      }
    }
    }catch{
      case ex:JedisException=> {
        println("smembers [" + UniqReqId + "] is error ", ex)
      }
      case e:Exception=>{
        logger.error("getMetricByUniqReqId is error", e)
      }
    }
    metricMap
  }

  def buildMetricAnalyse(string:String): MetricAnalyse ={
    val spanAnalyse: SpanMetricAnalyse = JSON.parseObject(string, classOf[SpanMetricAnalyse])
    val metricAnalyse = new MetricAnalyse
    metricAnalyse.initData(spanAnalyse)
    metricAnalyse
  }


//通过uniqid获取不完整链路数据
  def getMetric(UniqReqId:String): util.Map[String,util.HashMap[String,MetricAnalyse]] ={
    val redisProxyDao: RedisProxyDaoImpl=RedisService.getRedisDao()
    var metricMap:util.Map[String,util.HashMap[String,MetricAnalyse]]=new util.HashMap[String,util.HashMap[String,MetricAnalyse]]
    try {
      if(redisProxyDao.exists(UniqReqId)) {
        var set:util.Set[String] =null
        set = redisProxyDao.smembers(UniqReqId)

        if (set != null && set.size() > 0) {
     //      println(UniqReqId,set)
          // println()
          val responseMap =getMetricKey(set)

         val  tuple=handlerResponse(responseMap)
         val linkid=tuple._1
          metricMap=tuple._2
          //删除已存储的子链路数据
          deleteData(linkid)
        }
      }
    }catch{
      case ex:JedisException=> {
        println("smembers [" + UniqReqId + "] is error ", ex)
      }
      case e:Exception=>{
        logger.error("getMetricByUniqReqId is error", e)
      }
    }
    metricMap
  }

  def handlerResponse(responseMap:util.HashMap[String, Response[util.Map[String,String]]] ): (String,util.Map[String,util.HashMap[String,MetricAnalyse]]) ={
    val metricMap:util.Map[String,util.HashMap[String,MetricAnalyse]]=new util.HashMap[String,util.HashMap[String,MetricAnalyse]]
    var linkid:String=""
    for (responseEntry <- responseMap.entrySet()) {
      val map = responseEntry.getValue.get()
      if (map != null) {
        val hostMap=new util.HashMap[String,MetricAnalyse]()
        for(entry<-map.entrySet()){
          val string=entry.getValue
          val metricAnalyse=buildMetricAnalyse(string)

          hostMap.put(entry.getKey,metricAnalyse)
        }
        val metrickey = responseEntry.getKey

        linkid=metrickey.split(">").apply(0)
        metricMap.put(metrickey.split(">").apply(1), hostMap)

      }
    }
    (linkid,metricMap)
  }

  def getMetricKey(set:util.Set[String]): util.HashMap[String, Response[util.Map[String,String]]] ={
    val shardedJedis = RedisService.getRedisClient().getConnection
    val pipeline = shardedJedis.pipelined()
    val responseMap = new util.HashMap[String, Response[util.Map[String,String]]]()
    try {
      for (metricKey <- set) {
        val response = pipeline.hgetAll(metricKey)
        responseMap.put(metricKey, response)
      }
      pipeline.sync()
    } catch {
      case ex: Exception =>
        logger.error("getMetricByUniqReqId is error", ex)
    } finally {
      RedisService.getRedisClient().returnResource(shardedJedis)
    }
    responseMap
  }

  def deleteData(linkId:String): Unit ={
    val linkDatakey=linkId+ "@" + LinkKeyType._LINK_KEY.getCode
    try {
      RedisService.getRedisDao().del(linkDatakey)
    }catch{
      case ex:Exception=>{
        logger.error("redis del linkid is ["+linkId+"]error",ex)
      }
    }
    try {

      val queryTime = InitMongoDao.mongoDAO.getQuery(classOf[LinkTreeParam])
      queryTime.order("-createTime")
      val param:LinkTreeParam=queryTime.get()
      val time=param.getCreateTime

      val query = InitMongoDao.mongoDAO.getQuery(classOf[LinkTreeParam])
      query.field("linkId").equal(linkId)
      query.field("createTime").equal(time)
      InitMongoDao.mongoDAO.deleteByCriteria(query)
    }catch {
      case ex:Exception=>
        logger.error("mongoDB delete linkId"+"["+linkId+"] is error",ex)
    }
  }

}
