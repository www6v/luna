package com.yhd.arch.tuna.linktree.dao.service.reids

import java.util

import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.linktree.dao.RedisDao
import com.yhd.arch.tuna.util.ParamConstants

import scala.collection.JavaConversions._

/**
  * Created by root on 2/15/17.
  */
class UniqReqIdIndexReidis  extends RedisDao{
  var map:util.Map[util.Set[String],util.Set[String]]=null

  override def handler(pipeline:ShardedJedisPipeline): Unit ={
    if(map!=null){
      for (entry <- map.entrySet()) {
        val uniqReqIdSet=entry.getKey
        val metricKeySet=entry.getValue
        for (uniqReqId <- uniqReqIdSet) {
          for (metricKey <- metricKeySet)
            pipeline.sadd(uniqReqId, metricKey)
            pipeline.expire(uniqReqId,ParamConstants.METRIC_EXPIRE)
        }
      }
    }
  }

  def setData(map:util.Map[util.Set[String],util.Set[String]]): Unit ={
  this.map=map
  }


}
