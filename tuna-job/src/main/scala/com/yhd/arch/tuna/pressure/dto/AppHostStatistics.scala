package com.yhd.arch.tuna.pressure.dto

import java.util
import java.util.Date

import com.yihaodian.monitor.dto.ClientBizLog
import com.yihaodian.util.SparkPoolIdParam

/**
 * Created by root on 5/12/16.
 */

class AppHostStatistics extends Serializable{

  private var poolId=""
  private var poolIdParam:SparkPoolIdParam=null
  val groupHostMap=new util.HashMap[String,util.HashMap[String,HostStatistics]]()

  def statistics(iterable: Iterable[ClientBizLog]): Unit ={

    val newDate=new Date()
    iterable.foreach(msg=> {
      val respTime=msg.getRespTime.getTime

   //   println("msg.getRespTime"+respTime)
    //  if(newDate.getTime-respTime<5*60*1000) {
        var groupName = msg.getServiceGroup
        if (groupName.equals("NoGroup")) {
          groupName = "[refugee]"
        }
        var host = msg.getProviderHost
        if (host.contains(":")) {
          host = host.split(":").apply(0)

        val serviceName = msg.getServiceName
        var hostMap = groupHostMap.get(groupName)
        if (hostMap == null) {
          hostMap = new util.HashMap[String, HostStatistics]()
          groupHostMap.put(groupName, hostMap)
        }
        var hostStatistics = hostMap.get(host)
        if (hostStatistics == null) {
          hostStatistics = new HostStatistics
          hostMap.put(host, hostStatistics)
        }
        hostStatistics.statistics(serviceName, msg.getSuccessed, msg.getCostTime)
      }
          // }else{
//        println("********************************")
//        logInfo("newDate.getTime-respTime>=5*60*1000")
//        println("********************************")
//      }
    })

  }

  def setSparkPoolIdParam(param:SparkPoolIdParam): Unit ={
    poolIdParam=param
  }

  def getSparkPoolIdParam():SparkPoolIdParam={
    poolIdParam
  }

  def setPoolId(pool:String): Unit ={
    this.poolId=pool
  }
  def getPoolId():String={
    poolId
  }
  def getGroupsMap(): util.HashMap[String,util.HashMap[String,HostStatistics]]={
    groupHostMap
  }
}
