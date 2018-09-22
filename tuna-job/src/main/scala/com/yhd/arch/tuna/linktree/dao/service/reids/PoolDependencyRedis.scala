package com.yhd.arch.tuna.linktree.dao.service.reids

import java.util

import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.constants.LinkKeyType
import com.yhd.arch.tuna.linktree.dao.RedisDao
import com.yhd.arch.tuna.util.ParamConstants
import scala.collection.JavaConversions._

/**
  * Created by root on 2/14/17.
  */
class PoolDependencyRedis extends RedisDao{
  var map:util.HashMap[String,util.Set[String]]=null
  override def handler(pipeline:ShardedJedisPipeline): Unit = {
    val dataMap = getData()

    for (mapEntry <- dataMap.entrySet()) {
      val provider=mapEntry.getKey
      val key = provider+ "@" + LinkKeyType.DEPENDENCY_SUFFIX_KEY.getCode
      val set = mapEntry.getValue
      for (value <- set) {
        val valueKey=value+"@"+LinkKeyType.CALLERDEPENDENCY_SUFFIX_KEY.getCode
        pipeline.sadd(key, value)

        pipeline.sadd(valueKey,provider)

        pipeline.expire(key, ParamConstants.KEY_EXPIRE)
        pipeline.expire(valueKey, ParamConstants.KEY_EXPIRE)
      }
    }
  }
  def setData(map:util.HashMap[String,util.Set[String]]): Unit ={
    this.map=map
  }

  def getData():util.HashMap[String,util.Set[String]]={
    return map
  }
}
