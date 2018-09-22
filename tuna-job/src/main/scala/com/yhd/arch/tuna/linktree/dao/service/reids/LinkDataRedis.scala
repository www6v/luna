package com.yhd.arch.tuna.linktree.dao.service.reids

import java.util
import java.util.Date

import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.constants.LinkKeyType
import com.yhd.arch.tuna.linktree.balancer.BalancerFactory
import com.yhd.arch.tuna.linktree.dao.RedisDao
import com.yhd.arch.tuna.util.ParamConstants

import scala.collection.JavaConversions._

/**
  * Created by root on 2/10/17.
  */
class LinkDataRedis extends RedisDao{
  var treeMap:util.Map[String,String]=null
  var spanIdMap:util.Map[String,util.Set[String]]=null


  val balancer= {
    val hashBalancer = BalancerFactory.getInstance().getConsistantHashBalancer
    hashBalancer.initIndex(ParamConstants.CONTAINER_SIZE)
    hashBalancer
  }

  override def handler(pipeline:ShardedJedisPipeline): Unit ={
    handlerTreeData(pipeline)
    handlerSpanData(pipeline)
  }
  def handlerTreeData(pipeline:ShardedJedisPipeline): Unit ={
    val map=getTreeData()
    for (linkData <- map.entrySet()) {
      val key = linkData.getKey
      //为扫描所有linkid建立索引
      val linkKey = LinkKeyType._LINK_KEY.getCode+ "@" + getLinkIndex(key)
      pipeline.sadd(linkKey, key)

      val linkdataId = key + "@" + LinkKeyType._LINK_KEY.getCode
      pipeline.set(linkdataId, linkData.getValue)
      pipeline.expire(linkdataId, ParamConstants.KEY_EXPIRE)
    }
  }

  def handlerSpanData(pipeline:ShardedJedisPipeline): Unit ={
    val map=getSpanData()
    val time=new Date()
    for (span <- spanIdMap.entrySet()) {
      val linkId = span.getKey
      val spanSet = span.getValue
      for (spanid <- spanSet) {
        val spankey = LinkKeyType._SPARNID_INDEX_KEY.getCode + "@" + getLinkIndex(spanid)
        pipeline.sadd(spankey, spanid)

        //存储索引信息：linkid与spanid互相映射
        pipeline.hset(spanid, linkId, "" + time.getTime)
        pipeline.sadd(linkId, spanid)
        pipeline.expire(spanid, ParamConstants.ONE_HOUR_EXPIRE)
        pipeline.expire(linkId, ParamConstants.KEY_EXPIRE)
      }
    }
  }

  def getLinkIndex(key:String): Int ={
    val value=balancer.select(key)
    value
  }

  def setTreeData(treeMap:util.Map[String,String]): Unit ={
    this.treeMap=treeMap
  }
  def getTreeData():util.Map[String,String]={
    return treeMap
  }

  def setSpanData(spanIdMap:util.Map[String,util.Set[String]]): Unit ={
    this.spanIdMap=spanIdMap
  }
  def getSpanData():util.Map[String,util.Set[String]]={
    return spanIdMap
  }

}
