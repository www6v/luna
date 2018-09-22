package com.yhd.arch.tuna.linktree.dao.service

import java.util
import java.util.concurrent.atomic.AtomicLong

import com.yhd.arch.tuna.constants.LinkKeyType
import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.linktree.balancer.BalancerFactory
import com.yhd.arch.tuna.linktree.dto.LinkTreeParam
import com.yhd.arch.tuna.linktree.util.Config
import com.yhd.arch.tuna.metric.entity.StatisticsDataPoint
import com.yhd.arch.tuna.util.ParamConstants
import org.slf4j.LoggerFactory
import org.apache.spark.streaming.Time

import scala.collection.JavaConversions._

/**
  * Created by root on 12/2/16.
  */
object LinkDataToRedis {
  val logger= LoggerFactory.getLogger(LinkDataToRedis.getClass)

  val balancer= {
    val hashBalancer = BalancerFactory.getInstance().getConsistantHashBalancer
    hashBalancer.initIndex(ParamConstants.CONTAINER_SIZE)
    hashBalancer
  }
    /**
    * 写入redis：
    * 1.链路Tree数据
    * 2.创建链路索引
    *
    * @param treeMap 链路数据
    *  @param spanIdMap 请求id（spanid）与linkd映射 建立索引
    *  @param treeParamMap 链路信息数据
    * */
  def saveMapToRedis(treeMap:util.Map[String,String],
                     spanIdMap:util.Map[String,util.Set[String]],
                     treeParamMap:util.Map[String,LinkTreeParam], time:Time): Unit = {
    val start=System.currentTimeMillis()
    val shardedJedis=RedisService.getRedisClient().getConnection

    val pipeline = shardedJedis.pipelined()
      try {
        for (linkData <- treeMap.entrySet()) {
          val key = linkData.getKey
          //为扫描所有linkid建立索引
          val linkKey = LinkKeyType._LINK_KEY.getCode + "@" + getLinkIndex(key)
          pipeline.sadd(linkKey, key)

          val linkdataId = key + "@" + LinkKeyType._LINK_KEY.getCode
          pipeline.set(linkdataId, linkData.getValue)
          pipeline.expire(linkdataId, ParamConstants.KEY_EXPIRE)
        }
        for (span <- spanIdMap.entrySet()) {
          val linkId = span.getKey
          val spanSet = span.getValue
          for (spanid <- spanSet) {
            val spankey = LinkKeyType._SPARNID_INDEX_KEY.getCode + "@" + getLinkIndex(spanid)
            pipeline.sadd(spankey, spanid)

            //存储索引信息：linkid与spanid互相映射
            pipeline.hset(spanid, linkId, "" + time.milliseconds)
            pipeline.expire(spanid, ParamConstants.KEY_EXPIRE)
            pipeline.sadd(linkId, spanid)
            pipeline.expire(linkId, ParamConstants.KEY_EXPIRE)
          }
        }
      pipeline.sync()
    } catch {
      case ex: Exception => {
        println("saveMapToRedis pipeline sync is error", ex)
        logger.error("saveMapToRedis pipeline sync is error", ex)
      }
    }finally {
      RedisService.getRedisClient().returnResource(shardedJedis)
    }

    InitMongoDao.mongoDAO.save(treeParamMap.values())
    logger.info("saveMapToRedis costTime:"+(System.currentTimeMillis()-start)+", size:"+treeMap.size())
  }

  def getLinkIndex(key:String): Int ={
    val value=balancer.select(key)
    value
  }


  def saveMetric(resultmap:util.HashMap[String,util.List[StatisticsDataPoint]]): Unit ={
    val shardedJedis=RedisService.getRedisClient().getConnection
    val pipeline = shardedJedis.pipelined()
    try {
      for (entry <- resultmap.entrySet()) {
        val metricKey = entry.getKey
        val list = entry.getValue
        for (statistic <- list) {
          pipeline.hset(metricKey, statistic.metric, statistic.toString())
          pipeline.expire(metricKey, ParamConstants.METRIC_EXPIRE)
        }
      }
      pipeline.sync()
    }catch{
      case ex:Exception=>{
        println("saving metric error!!!",ex)
        logger.error("saving metric error!!!",ex)
      }
    }finally {
      RedisService.getRedisClient().returnResource(shardedJedis)
    }
  }
}
