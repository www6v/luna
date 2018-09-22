package com.yhd.arch.tuna.pressure.zk

import java.util
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants
import com.yihaodian.architecture.hedwig.common.util.ZkUtil
import com.yihaodian.architecture.zkclient.{IZkChildListener, IZkDataListener}
import com.yihaodian.util.SparkPoolIdParam

import scala.collection.JavaConversions._
/**
 * Created by root on 3/15/16.
 */

object PoolDataWatcher{
  def main(args: Array[String]): Unit ={
    try{
      Thread.sleep(1000000)
    }catch{
      case ex:Exception =>println(ex.getMessage)
    }
  }
  private val poolWatcher=PoolDataWatcher()
  private var inited=false
//  private var poolWatcher:PoolDataWatcher=null
  /***/
  def getPoolIdParamMap():ConcurrentHashMap[String,SparkPoolIdParam] ={
    poolWatcher.getPoolIdParamMap()
  }

  def changePoolIdParam(path: String,param: SparkPoolIdParam): Boolean ={
    poolWatcher.changePoolIdParam(path,param)
  }
//  def init(): Unit ={
//    poolWatcher=new PoolDataWatcher
//    poolWatcher.doInit()
//  }

  def apply()={
    println(new Date(),"PoolDataWatcher initialized")
    val watcher=new PoolDataWatcher
    watcher.doInit()
    watcher
  }
}

class PoolDataWatcher{
  private val parentPath:String=InternalConstants.BASE_ROOT_FLAGS
  private val poolIdParam=ParamConstants.TUNA_MESSURE_PATH

  private val poolIdList=new util.ArrayList[String]()
  private val poolIdParamMap=new ConcurrentHashMap[String,SparkPoolIdParam]()

  def doInit(): Unit ={
    try{
      poolIdList.addAll(ZkUtil.getZkClientInstance.getChildren(parentPath))
      println("poolIdList",poolIdList.size())
      addListener(poolIdList)
      //是否添加
      ZkUtil.getZkClientInstance.subscribeChildChanges(parentPath,new IZkChildListener {
        override def handleChildChange(s: String, list: util.List[String]): Unit = {
          list.removeAll(poolIdList)
          poolIdList.addAll(list)
          addListener(list)
        }
      })
    }catch{
      case ex:Exception => {
        println(ex.getMessage)
      }
    }
    println(new Date(),"PoolDataWatcher initialized finsh")

  }

  def addListener(childList:util.List[String]): Unit ={
    if(childList!=null&&childList.size()>0){
      for(childPath<-childList){
       // if(childPath.contains("#")) {
          val path = parentPath + "/" + childPath  + poolIdParam
          if(ZkUtil.getZkClientInstance.exists(path)) {
            val ss: SparkPoolIdParam = ZkUtil.getZkClientInstance.readData(path)
            if (ss != null) {
              addPoolIdParamMap(path, ss)
            }
            ZkUtil.getZkClientInstance.subscribeDataChanges(path, new IZkDataListener {
              override def handleDataChange(s: String, o: Object): Unit = {
                if (o != null) {

                  addPoolIdParamMap(path, o.asInstanceOf[SparkPoolIdParam])
                }
              }

              override def handleDataDeleted(s: String): Unit = {
              }
            })
          }
      }
    }
  }

  def addPoolIdParamMap(path:String,param:SparkPoolIdParam): Unit ={
    println("handleDataChange:###############"+path+"###############")
    val pool = param.getPoolId
    if (param.getStatus == 1) {
      println("serPath:" + path)
      poolIdParamMap.put(pool, param)
    } else if (param.getStatus == 0) {

      if (poolIdParamMap.containsKey(pool)) {
        poolIdParamMap.remove(pool)
      }
    }
  }

  /**
   * 改变poolidParam并修改zk节点数据
   * logger*/
  def changePoolIdParam(path: String,param: SparkPoolIdParam): Boolean ={
    param.setStatus(0)
    try {
      ZkUtil.getZkClientInstance.writeData(path, param)
      true
    }catch{
      case ex:Exception=>println()
    }
    false
  }

  def removePoolIdParam(path: String): Unit ={
    if(poolIdParamMap.contains(path )) {
      poolIdParamMap.remove(path)
    }
  }
  /**
   * 获取Map中一个元素*/

  def getOnePoolIdPath():String={
    println("poolIdParamMap.size="+poolIdParamMap.size())
    if(poolIdParamMap!=null&&poolIdParamMap.size()>0){
      val iter=poolIdParamMap.keySet().iterator()
      if(iter.hasNext)
        return iter.next()
    }
    null
  }

  def getPoolIdParamMap():ConcurrentHashMap[String,SparkPoolIdParam] ={
    poolIdParamMap
  }
  def getSparkParam(poolidPath: String): SparkPoolIdParam={
    poolIdParamMap.get(poolidPath)
  }

  def testSetPoolIdMap(poolidMap:ConcurrentHashMap[String,SparkPoolIdParam]): Unit ={
    poolIdParamMap.putAll(poolidMap)
  }
}
