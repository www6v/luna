package com.yhd.arch.tuna.pressure.util

import java.util
import java.util.concurrent.ConcurrentHashMap

import com.yhd.arch.tuna.pressure.dto.AppHostStatistics
import com.yhd.arch.tuna.pressure.job.ClientHostAnalyst
import com.yihaodian.util.SparkPoolIdParam

import scala.collection.JavaConversions._

/**
 * Created by root on 5/13/16.
 */
object AnalystUtils {
  private val poolAnalytMap=new ConcurrentHashMap[String,ClientHostAnalyst]()
  private val poolParamMap=new ConcurrentHashMap[String,SparkPoolIdParam]()
  def analyse(results:util.ArrayList[AppHostStatistics]): Unit ={
   val hashMap=new util.HashMap[String,util.ArrayList[AppHostStatistics]]()
   // val poolParamMap=new util.HashMap[String,SparkPoolIdParam]()
    for(statistics<-results){
      val pool=statistics.getPoolId()
      poolParamMap.put(pool,statistics.getSparkPoolIdParam())
      var list=hashMap.get(pool)
      if(list==null){
        list=new util.ArrayList[AppHostStatistics]()
        hashMap.put(pool,list)
      }
      list.add(statistics)
    }
   for(entry<-hashMap.entrySet()){
     val key=entry.getKey
     val value=entry.getValue
     var clientHostAnalyst=poolAnalytMap.get(key)
     if(clientHostAnalyst==null) {
       clientHostAnalyst = new ClientHostAnalyst()
       clientHostAnalyst.init(poolParamMap.get(key))
       poolAnalytMap.put(key, clientHostAnalyst)

     }

     if(clientHostAnalyst.isCheck()) {
       if (clientHostAnalyst.getLocalHostServiceMap != null) {
         clientHostAnalyst.analyst(value)
       } else if (clientHostAnalyst.getHostServiceMap() != null) {
           clientHostAnalyst.analyst(value)
       }else{
         println("getHostServiceMap is null!!!")
       }
     }else{
       removePoolAnalytMap(key)
     }
   }
  }
  def removePoolAnalytMap(pool:String): Unit ={
    poolAnalytMap.remove(pool)
  }
  def getPoolParamMap(): ConcurrentHashMap[String,SparkPoolIdParam] ={
    poolParamMap
  }
}
