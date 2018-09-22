package com.yhd.arch.tuna.utils

import java.util
import java.util.Date
import java.util.concurrent.atomic.AtomicLong

import com.yhd.arch.tuna.dao.MongoServiceTest
import com.yhd.arch.tuna.stream.{MergeStreamTest}
import com.yhd.arch.tuna.dto.MethodAnalyseAnnotation
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
//import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created by root on 5/20/16.
 */
object MethodAnalyseJobUtils {

  //private var context = new ClassPathXmlApplicationContext("applicationContext.xml")
 // private var methodAnalyseService = context.getBean("methodAnalyseService").asInstanceOf[IMethodAnalyseService]

  def processMethodAnalyse = (rdd: RDD[ClientBizLog], time: Time) =>{
    //val col = rdd.collect()
    val callCounts = new AtomicLong(0)
    val successedCounts = new AtomicLong(0)
    val failedCounts = new AtomicLong(0)
    val totalCostTime = new AtomicLong(0)
    var avgCost = 0
    val fastCounts = new AtomicLong(0)
    val commonCounts = new AtomicLong(0)
    val slowCounts = new AtomicLong(0)
    var maxCostTime = 0
    var minCostTime = 10000
    val map = new util.HashMap[Long, MethodAnalyseAnnotation]()
    val methodAnalyseAccumulator = MergeStreamTest.methodAnalyseAccumulator
    methodAnalyseAccumulator.value.clear()
    rdd.foreachPartition(partitionRecords => {
      partitionRecords.foreach(record => {
        val t = record
        if(t.getLayerType != null && t.getLayerType.intValue() == 1){
          if(t.getSuccessed != null && t.getSuccessed.intValue() == -1){
            failedCounts.getAndIncrement();
          }else{
            successedCounts.getAndIncrement();
          }
          callCounts.getAndIncrement();
          totalCostTime.addAndGet(t.getCostTime.longValue());
          val costTime = t.getCostTime
          if(costTime >=0 && costTime < 40){
            fastCounts.getAndIncrement();
          }else if(costTime >= 40 && costTime < 80){
            commonCounts.getAndIncrement();
          }else{
            slowCounts.getAndIncrement();
          }

          if(costTime > maxCostTime){
            maxCostTime = costTime
          }
          if(costTime < minCostTime){
            minCostTime = costTime
          }
          val key = buildKey(t)
          if(map.get(key) == null){
            val methodAnalyseAnnotation = new MethodAnalyseAnnotation
            //methodAnalyseAnnotation.setAppHost(t.getProviderHost)
            methodAnalyseAnnotation.setProviderHost(t.getProviderHost)
            methodAnalyseAnnotation.setClientAppCode(t.getCallApp)
            methodAnalyseAnnotation.setClientMethodName(t.getMethodName)
            methodAnalyseAnnotation.setServiceGroup(t.getServiceGroup)
            methodAnalyseAnnotation.setServiceMethodName(t.getServiceMethodName)
            methodAnalyseAnnotation.setServiceName(t.getServiceName)
            methodAnalyseAnnotation.setServiceVersion(t.getServiceVersion)
            methodAnalyseAnnotation.setAppCode(t.getProviderApp)
            methodAnalyseAnnotation.setCalledCounts(callCounts.longValue())
            methodAnalyseAnnotation.setFailedCounts(failedCounts.longValue())
            methodAnalyseAnnotation.setSuccessedCounts(successedCounts.longValue())
            methodAnalyseAnnotation.setTotalCostTime(totalCostTime.longValue())
            //methodAnalyseAnnotation.setAgvCostTime(getAvgCost(callCounts.longValue(), totalCostTime.longValue()))
            methodAnalyseAnnotation.setAvgCost(getAvgCost(callCounts.longValue(), totalCostTime.longValue()))
            methodAnalyseAnnotation.setStartTime(getStartTime(t.getReqTime, 2))
            methodAnalyseAnnotation.setEndTime(getEndTime(t.getReqTime, 2))
            map.put(key, methodAnalyseAnnotation)
          }else{
            val methodAnalyse = map.get(key)
            val _calledCounts = methodAnalyse.getCalledCounts
            val _failedCounts = methodAnalyse.getFailedCounts
            val _successedCounts = methodAnalyse.getSuccessedCounts
            val _totalCostTime = methodAnalyse.getTotalCostTime
            methodAnalyse.setCalledCounts(_calledCounts + callCounts.longValue())
            methodAnalyse.setFailedCounts(_failedCounts + failedCounts.longValue())
            methodAnalyse.setSuccessedCounts(_successedCounts+successedCounts.longValue())
            methodAnalyse.setTotalCostTime(_totalCostTime+totalCostTime.longValue())
            //methodAnalyse.setAgvCostTime(getAvgCost(methodAnalyse.getCalledCounts, methodAnalyse.getTotalCostTime))
            methodAnalyse.setAvgCost(getAvgCost(methodAnalyse.getCalledCounts, methodAnalyse.getTotalCostTime))
            methodAnalyse.setStartTime(getStartTime(methodAnalyse.getStartTime, 2))
            methodAnalyse.setEndTime(getEndTime(methodAnalyse.getStartTime, 2))
            map.put(key, methodAnalyse)
          }
          methodAnalyseAccumulator += map
        }
      })
    })

    var methodAnalyseMap = methodAnalyseAccumulator.value

    println("methodAnalyseMap:"+methodAnalyseMap)
    if(methodAnalyseMap != null && methodAnalyseMap.size() > 0){
      val it = methodAnalyseMap.entrySet().iterator()
      val list = new util.ArrayList[MethodAnalyseAnnotation]()
      while (it.hasNext){
        val entry = it.next()
        val key = entry.getKey
        val value = entry.getValue
        val methodAnalyseAnnotation = new MethodAnalyseAnnotation
        methodAnalyseAnnotation.setAppCode(value.getAppCode)
        methodAnalyseAnnotation.setServiceName(value.getServiceName)
        methodAnalyseAnnotation.setClientAppCode(value.getClientAppCode)
        methodAnalyseAnnotation.setClientMethodName(value.getClientMethodName)
        methodAnalyseAnnotation.setServiceMethodName(value.getServiceMethodName)
        methodAnalyseAnnotation.setServiceGroup(value.getServiceGroup)
        methodAnalyseAnnotation.setServiceVersion(value.getServiceVersion)
        methodAnalyseAnnotation.setProviderHost(value.getProviderHost)
        methodAnalyseAnnotation.setCalledCounts(value.getCalledCounts)
        methodAnalyseAnnotation.setFailedCounts(value.getFailedCounts)
        methodAnalyseAnnotation.setTotalCostTime(value.getTotalCostTime)
        methodAnalyseAnnotation.setAvgCost(value.getAvgCost)
        methodAnalyseAnnotation.setStartTime(value.getStartTime)
        methodAnalyseAnnotation.setEndTime(value.getEndTime)
        methodAnalyseAnnotation.setSuccessedCounts(value.getSuccessedCounts)
        methodAnalyseAnnotation.setGmtCreate(value.getGmtCreate)
        list.add(methodAnalyseAnnotation)
      }
      //methodAnalyseService.saveMethodAnalyse(list)
    //  val keys = methodAnalyseService.save(list)
      val keys= MongoServiceTest.getMongoDAO().save(list)
      println("the methodAnalyse keys is :"+keys)
    }
  }

  def buildKey(cb: ClientBizLog) : Long = {
    val prime = 31;
    var result = 1;
    result = prime * result +(if(cb.getCallApp == null) 0 else cb.getCallApp.hashCode)
    result = prime * result +(if(cb.getMethodName == null) 0 else cb.getMethodName.hashCode)
    result = prime * result +(if(cb.getProviderApp == null) 0 else cb.getProviderApp.hashCode)
    result = prime * result +(if(cb.getProviderHost == null) 0 else cb.getProviderHost.hashCode)
    result = prime * result +(if(cb.getServiceName == null) 0 else cb.getServiceName.hashCode)
    result = prime * result +(if(cb.getServiceMethodName == null) 0 else cb.getServiceMethodName.hashCode)
    result = prime * result +(if(cb.getServiceGroup == null) 0 else cb.getServiceGroup.hashCode)
    result = prime * result +(if(cb.getServiceVersion == null) 0 else cb.getServiceVersion.hashCode)
    return result
  }

  def getAvgCost(callCounts : Long, totalCostTime : Long) : Int = {
    val curt = callCounts
    if(curt != 0){
      val avgCost = totalCostTime / curt
      return avgCost.toInt
    }
    return 0
  }

  def getStartTime(logTime : Date, intervalTime: Long) : Date = {
    return new Date(getSecsStartTime(logTime, intervalTime))
  }

  def getEndTime(logTime : Date, intervalTime: Long) : Date = {
    return new Date(getSecsStartTime(logTime, intervalTime) + intervalTime * 1000)
  }

  def getSecsStartTime(logTime : Date, intervalTime: Long) : Long = {
    val secs = logTime.getTime / 1000
    val newSecs = (secs / intervalTime) * intervalTime
    return newSecs * 1000
  }
}
