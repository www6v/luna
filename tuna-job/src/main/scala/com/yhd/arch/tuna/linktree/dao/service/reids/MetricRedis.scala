package com.yhd.arch.tuna.linktree.dao.service.reids

import java.util

import java.util.Date
import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.linktree.dao.RedisDao
import com.yhd.arch.tuna.util.ParamConstants

import scala.collection.JavaConversions._

/**
  * Created by root on 2/10/17.
  */
class MetricRedis extends RedisDao{

  var metricmap:util.Map[String,String]=null
  override def handler(pipeline:ShardedJedisPipeline): Unit ={
    val statisticMap=getData()
    if(statisticMap==null) {
      throw new NullPointerException()
    }

    val time=new Date().getTime()

    for (entry <- statisticMap.entrySet()) {
      val metricKey = entry.getKey
      val metricEntry = entry.getValue
      pipeline.set(metricKey,metricEntry)
    //  println(metricKey,metricEntry)
      pipeline.expire(metricKey, ParamConstants.METRIC_EXPIRE)
    }
  }
  def setData(map:util.Map[String,String]): Unit ={
    this.metricmap=map
  }
  def getData():util.Map[String,String]={
    metricmap
  }
}
