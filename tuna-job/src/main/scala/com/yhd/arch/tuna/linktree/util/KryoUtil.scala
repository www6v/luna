package com.yhd.arch.tuna.linktree.util

import java.util

import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.linktree.dto.{AppNode, LinkInfo, LinkTreeParam}
import com.yhd.arch.tuna.linktree.serialize.KryoSerializers
import com.yhd.arch.tuna.util.ParamConstants
import org.apache.spark.streaming.Time
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by root on 12/2/16.
  */
object KryoUtil {
  val logger= LoggerFactory.getLogger(KryoUtil.getClass)

  def parseByte[T](infoObject:T): Array[Byte] ={
    val kryoSerializers=new KryoSerializers
    kryoSerializers.serialize(infoObject)
  }

}
