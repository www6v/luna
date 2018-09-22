package com.yhd.arch.tuna.linktree.util

import java.util
import java.util.Properties

import com.yhd.arch.tuna.util.ParamConstants
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._

/**
  * 加载系统配置文件
  * Created by root on 9/14/16.
  */
object Config{
  val LOG: Logger = LoggerFactory.getLogger(Config.getClass)
  var topicCount                       = 0
  var topicMulti                       = 0
  var windowduration:Long              = 0L
  var slideduration:Long               = 0L
  var batchduration:Long               = 0L
  var CHECK_CLEAR_SIZE                 = 2
  var CHECK_EXCEPTION_TIMES            = 2*4

  var JedisProp:Properties             =null
  var consumerName:String              =""

  val configClass:Config=Config()

  def getConusmerName():String={
    consumerName
  }

  def getTopicCount(): Integer ={
    topicCount
  }

  def getTopicMultiple(): Integer ={
    topicMulti
  }

  def getWindowsduration(): Long ={
    windowduration
  }

  def getSlideduration():Long={
    slideduration
  }

  def getBatchDuration():Long ={
    batchduration
  }

  def getSparkConfMap(prefix:String):util.Map[String,String]={
    val map=configClass.basicMap
    map
  }

  def apply() = {
    val config=new Config
    config.init()
    config
  }

}

class Config {

  val basicMap:util.Map[String,String]=new util.HashMap[String,String]()
  val sparkprop=ConfigLoader.getSparkENVProperties()

  def init(){

    for(conf<-sparkprop.entrySet()){
      val key=conf.getKey.toString
      val value=conf.getValue.toString
      if(key.equals(ParamConstants.JUMPER_TOPIC_COUNT)){
        Config.topicCount=value.toInt
      }else if(key.equals(ParamConstants.JUMPER_TOPIC_MULTIPLE)){
        Config.topicMulti=value.toInt
      }else if(key.equals(ParamConstants.WINDOW_DURATION)){
        Config.windowduration=value.toLong
      }else if(key.equals(ParamConstants.SLIDE_DURATION)){
        Config.slideduration=value.toLong
      }else if(key.equals(ParamConstants.BATCH_DURATION) ) {
        Config.batchduration=value.toLong
      }else if(key.equals(ParamConstants.JUMPER_CONSUMER_NAME)){
        Config.consumerName=value
      }else{
        basicMap.put(key,value)
      }
    }
    Config.LOG.warn("basicMap:"+basicMap)
  }
}
