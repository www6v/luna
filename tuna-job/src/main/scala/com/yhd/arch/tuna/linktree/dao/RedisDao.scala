package com.yhd.arch.tuna.linktree.dao

import com.ycache.redis.clients.jedis.ShardedJedisPipeline
import com.yhd.arch.tuna.dao.impl.RedisService
import org.slf4j.LoggerFactory

/**
  * Created by root on 2/10/17.
  */
trait RedisDao extends Serializable{
  val logger= LoggerFactory.getLogger(classOf[RedisDao])

  def saveToRdis(): Unit ={
    val shardedJedis=RedisService.getRedisClient().getConnection
    val pipeline = shardedJedis.pipelined()
    try {
      handler(pipeline)
      pipeline.sync()
    }catch{
      case ex:Exception=>{
        logger.error("saveToRdis is error!!!",ex)
      }
    }finally {
      RedisService.getRedisClient().returnResource(shardedJedis)
    }
  }

  def handler(pipeline:ShardedJedisPipeline)

}
