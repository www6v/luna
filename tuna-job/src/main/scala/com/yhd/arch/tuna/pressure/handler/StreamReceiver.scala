package com.yhd.arch.tuna.pressure.handler

import java.util
import java.util.Date

import com.yhd.arch.tuna.pressure.dto.AppHostStatistics
import com.yhd.arch.tuna.pressure.processor.PressureProcessor
import com.yhd.arch.tuna.pressure.util.{AnalystUtils, SparkHCUtil}
import com.yhd.arch.tuna.pressure.zk.PoolDataWatcher
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by root on 3/30/16.
 */
object StreamReceiver {

  private val LOG: Logger = LoggerFactory.getLogger(StreamReceiver.getClass)
  def printCostTime = (rdd: RDD[(String,Iterable[Integer])], time:Time) => {
    val now = new Date
    println("rdd.partitions.length",rdd.partitions.length)
    val col = rdd.collect()
    println ("-------------------------------------------")
    println("    "+now)
    println ("-------------------------------------------")
    var to=0
    for(t<-col){
      val key = t._1
      val valus = t._2
      var min= Int.MaxValue
      var max = 0
      var total = 0
      to=to+valus.size
      for(v <- valus){
        if(v>max){
          max = v
        }
        if(v<min){
          min = v
        }
        total = total+v
      }

      val avg = total/valus.size
      println("cost:"+(key,min,avg,max))
    }
    println("calculate:"+to)

  }

  def operationRDD=(rdd:RDD[(String,Iterable[ClientBizLog])],time:Time)=>{
    val accumulator= PressureProcessor.accumulators
    accumulator.value.clear()
    rdd.foreachPartition(partitionRecords=>{
      val map=PoolDataWatcher.getPoolIdParamMap()
      partitionRecords.foreach( record => {
        var appCode=record._1
        val clientBizLogs = record._2
        val newDate=new Date()

        appCode=appCode.replaceAll("#","/")
        if(map.containsKey(appCode)) {
        println("******************"+appCode+"**********************")

        val msg = clientBizLogs.toList.apply(0)
        val respTime = msg.getRespTime.getTime
        if (newDate.getTime - respTime <= 5 * 60 * 1000) {
          val list = new util.ArrayList[AppHostStatistics]()
          val appHostStatistics = new AppHostStatistics
          appHostStatistics.setPoolId(appCode)
          appHostStatistics.setSparkPoolIdParam(map.get(appCode))
          appHostStatistics.statistics(clientBizLogs)
          list.add(appHostStatistics)
          accumulator += list
        }else{
          println("newDate.getTime-respTime>=5*60*1000:"+new Date(respTime))
        }
        println("respTime: "+new Date(respTime)+" now:"+new Date())
        }
      })
    })
    println()
    println("-------------------------------------------")
    print(new Date())
    println("-------------------------------------------")
    val list=accumulator.value

    println("accumulator="+list)
    //LOG.warn("accumulator="+list.size+"  ")
    SparkHCUtil.addCheck()

    val array=new util.ArrayList[AppHostStatistics]()
    array.addAll(list)
    AnalystUtils.analyse(array)
  }
  def printSI = (rdd: RDD[(String, Iterable[ClientBizLog])], time: Time) => {
    val col = rdd.collect()
    println("-------------------------------------------")
    val now = new Date
    print(time)
    println(" " + now)
    println("-------------------------------------------"+col.size)
   // val map=PoolDataWatcher.getPoolIdParamMap()
    var total=0
    col.foreach(values=>{
      var key=values._1
      val value=values._2
      key=key.replaceAll("#","/")
      //if(map.containsKey(key)){
        total=total+value.size
    //  }
    })
    println(" printSI "+total)
  }

  def printSITuple = (rdd: RDD[(String, Int)], time: Time) => {
    val col = rdd.collect()
    println("-------------------------------------------")
    val now = new Date
    print(time)
    println(" " + now)
    println("-------------------------------------------"+col.size)
    val map=PoolDataWatcher.getPoolIdParamMap()
    var total=0
    col.foreach(values=>{
      var key=values._1
      key=key.replaceAll("#","/")
      val value=values._2
      if(map.containsKey(key)){
        total=value
      }
    })
    println(" printSITuple "+total)

  }
}
