package com.yhd.arch.tuna.linktree.processor

import java.util

import com.yhd.arch.tuna.constants.LinkKeyType
import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.linktree.dao.service.reids.PoolDependencyRedis
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.ParamConstants
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by root on 1/23/17.
  */
class PoolDependencyProcessor extends Serializable{
  val logger= LoggerFactory.getLogger(classOf[PoolDependencyProcessor])

  val poolDependencyRedis=new PoolDependencyRedis

  def processor(dStream: DStream[(String,(util.Map[String,MetricAnalyse],util.Set[String],Int))]): Unit ={
    dStream.foreachRDD(handlerByPartitions)
  }
  def handlerByPartitions=(rdd:RDD[(String,(util.Map[String,MetricAnalyse],util.Set[String],Int))],time:Time) =>{
    rdd.foreachPartition(rdds=> {
      val poolMap=new util.HashMap[String,util.Set[String]]()
      rdds.foreach(tuple=>{
        val map:util.Map[String,MetricAnalyse]=tuple._2._1
        val tmpMap=handlePoolDependency(map)
        poolMap.putAll(tmpMap)

      })
      if(poolMap.size()>0){
        poolDependencyRedis.setData(poolMap)
        poolDependencyRedis.saveToRdis()
      }
    })
  }

  def handlePoolDependency(map:util.Map[String,MetricAnalyse]): util.Map[String,util.Set[String]] ={
    val poolMap:util.Map[String,util.Set[String]]=new util.HashMap[String,util.Set[String]]()
    for(analyseEntry<-map.entrySet()){
      val metricAnalyse=analyseEntry.getValue
      val callapp=metricAnalyse.getCallApp
      val providerApp=metricAnalyse.getProviderApp
      var set=poolMap.get(providerApp)
      if(set!=null){
        set.add(callapp)
      }else{
        set=new util.HashSet[String]()
        set.add(callapp)
        poolMap.put(providerApp,set)
      }
    }
    poolMap
  }
}
