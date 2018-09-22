package com.yhd.arch.tuna.pressure.util

/**
 * Created by root on 5/24/16.
 */
import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.alibaba.fastjson.JSONObject
import com.yhd.arch.tuna.linktree.util.{Config, InternalConstant}
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.util.{HedwigUtil, ZkUtil}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.StringBuilder


object SparkHCUtil {
  private val LOG: Logger = LoggerFactory.getLogger(SparkHCUtil.getClass)
  private val healthCheck=new AtomicInteger(0)
  def main(args:Array[String]): Unit ={
    val gp: String = System.getProperty("global.config.path")
    if (HedwigUtil.isBlankString(gp)) {
      System.setProperty("global.config.path", InternalConstant.global_config_path)
    }
    createTMP()
    try{
      Thread.sleep(10000)
    }catch{
      case a:Exception=>println(a.getMessage)
    }
  }
  private val parentPath=new StringBuilder(ParamConstants.SPARK_HEALTH_ROOT).append(ParamConstants.SPARK_HEALTH_PATH).toString()
  private val roll = parentPath + ParamConstants.SPARK_HEALTH_ROLL_PATH
  private val sparkHCUtil=SparkHCUtil()
  def apply()={
    val sp=new SparkHCUtil
    sp.runCheckHCJob()
    sp
  }
  def createTMP(): Unit ={
    try {
      println(parentPath)
      if (!ZkUtil.getZkClientInstance.exists(parentPath)) {
        ZkUtil.getZkClientInstance.createPersistent(parentPath, true)
        ZkUtil.getZkClientInstance.createPersistent(parentPath + ParamConstants.SPARK_HEALTH_PARAM_PATH, true)
      }

      LOG.warn("createEphemeral node  "+roll)
      if(!ZkUtil.getZkClientInstance.exists(parentPath)) {
        ZkUtil.getZkClientInstance.createEphemeral(roll)
      }else{
        LOG.warn("["+roll+"] node exits")
      }
    }catch{
      case ex:Exception=>{
        println(ex.getMessage)
        LOG.error("errorMsg:"+ex.getMessage,ex)
      }
    }

  }
  def deleteEphemeral(): Unit ={
    try{
      if(ZkUtil.getZkClientInstance.exists(roll))
        ZkUtil.getZkClientInstance.delete(roll)
    }catch {
      case e:Exception=>
        LOG.error("deleteEphemeral is error ",e)
    }
  }
  private val obj=new Object()
  def initializedWeight(pool:String,map:util.HashMap[String,Integer]): Unit ={
    obj.synchronized{
      try {
        val paramPath = parentPath + ParamConstants.SPARK_HEALTH_PARAM_PATH
        if(!ZkUtil.getZkClientInstance.exists(paramPath)){
          ZkUtil.getZkClientInstance.createPersistent(paramPath,true)
        }
        var paramMap = ZkUtil.getZkClientInstance.readData(paramPath).
          asInstanceOf[util.HashMap[String, util.HashMap[String, Integer]]]
        if (paramMap == null) {
          paramMap = new util.HashMap[String, util.HashMap[String, Integer]]()
        }
        paramMap.put(pool, map)
        ZkUtil.getZkClientInstance.writeData(paramPath, paramMap)
        println("write initializedWeight is successful")
        LOG.warn("write initializedWeight is successful")
      }catch{
        case ex:Exception=>{
          println(ex.getMessage)
          LOG.error("errorMsg:"+ex.getMessage,ex)
        }
      }
    }
  }
  def delete(pool:String): Unit ={
    obj.synchronized {
      try {
        val paramPath = parentPath + ParamConstants.SPARK_HEALTH_PARAM_PATH
        val paramMap = ZkUtil.getZkClientInstance.readData(paramPath).
          asInstanceOf[util.HashMap[String, JSONObject]]
        if (paramMap != null) {
          paramMap.remove(pool)
          ZkUtil.getZkClientInstance.writeData(paramPath, paramMap)
        }
        println(pool+" delete initializedWeight is successful")
        LOG.warn("delete initializedWeight is successful")
      }catch{
        case ex:Exception=> {
          println(ex.getMessage)
          LOG.error("errorMsg:"+ex.getMessage,ex)
        }
      }
    }
  }
  def addCheck(): Unit ={
    healthCheck.incrementAndGet()
  }
}
class SparkHCUtil{
  private var isAlive=true
  def runCheckHCJob(): Unit ={
    val t=new Thread(new Runnable {
      override def run(): Unit ={
        while(true) {
          try {
            Thread.sleep(Config.getSlideduration() * 1000 * 5)
          } catch {
            case ex: Exception => {
              SparkHCUtil.LOG.error(ex.getMessage)
            }
          }
          if (SparkHCUtil.healthCheck.intValue() <= 0) {

            SparkHCUtil.deleteEphemeral()
            isAlive = false
          } else {
            if (!isAlive) {
              println("executors is recover and create ephemeal !!!")
              SparkHCUtil.LOG.warn("executors is recover and create ephemeal !!!")
              SparkHCUtil.createTMP()
              isAlive = true
            }
          }
          SparkHCUtil.healthCheck.set(0)
        }
      }
    }).start()
  }
}
