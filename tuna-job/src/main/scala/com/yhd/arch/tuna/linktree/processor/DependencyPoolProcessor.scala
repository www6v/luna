package com.yhd.arch.tuna.linktree.processor

import java.util

import com.yhd.arch.tuna.linktree.dao.service.reids.PoolDependencyRedis
import com.yhd.arch.tuna.linktree.dto.BatchAnalyseDto
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by root on 3/2/17.
  */
class DependencyPoolProcessor extends Serializable{
  val logger= LoggerFactory.getLogger(classOf[DependencyPoolProcessor])
  val poolDependencyRedis=new PoolDependencyRedis
  def processor(dstream:DStream[(String,BatchAnalyseDto)]): Unit ={

    dstream.foreachRDD(handlerByPatition)
  }

  def handlerByPatition=(rdd:RDD[(String,BatchAnalyseDto)],time:Time)=>{
    rdd.foreachPartition(
      records => {
        val poolMap=new util.HashMap[String,util.Set[String]]()
        records.foreach(
          record => {
            val map=record._2.getSpanHostMapMap()
            for (entry<-map.entrySet()){
              val hostMap=entry.getValue
              handlePoolDependency(hostMap,poolMap)
            }
          })
        saveData(poolMap)
      })
  }

  def handlePoolDependency(map:util.Map[String,MetricAnalyse],poolMap:util.Map[String,util.Set[String]]){
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
  }

  def saveData(poolMap:util.HashMap[String,util.Set[String]]): Unit ={
    if(poolMap.size()>0){
      poolDependencyRedis.setData(poolMap)
      poolDependencyRedis.saveToRdis()
    }
  }
}
