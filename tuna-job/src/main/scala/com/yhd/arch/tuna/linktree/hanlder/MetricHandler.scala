package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.yhd.arch.tuna.linktree.dto.LinkInfo
import com.yhd.arch.tuna.metric.dto.{SpanMetricAnalyse}
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.CommonUtils
import org.apache.spark.streaming.Time
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by root on 2/9/17.
  */
object MetricHandler {
  val logger= LoggerFactory.getLogger(MetricHandler.getClass)

  def handlerMetric(linkId:String,
                    linkInfo:LinkInfo,
                    metricMap:util.Map[String,MetricAnalyse],time:Time): (Boolean,util.Map[String,String]) ={
    var initSave=false
    var linklayer=0
    var linkCount=0L
    val statisticMap:util.Map[String,String]=new util.HashMap[String,String]()

    for(metricEntry<-metricMap.entrySet()){
      val spanId=metricEntry.getKey
      val metricAnalyse=metricEntry.getValue

      val spanAnalyse=new SpanMetricAnalyse()
      metricAnalyse.toEntity(spanAnalyse)
      spanAnalyse.setGmtCreate(time.milliseconds)
      spanAnalyse.setSpanId(spanId)

      val curtLayer=spanAnalyse.getCurtLayer
      linkCount=spanAnalyse.getLinkCounts
      //记录链路调用开始
      if(curtLayer==1){
        initSave=true
      }
      if(linklayer<curtLayer){
        linklayer=curtLayer
      }
      val metricKey=linkId+">"+spanId
      statisticMap.put(metricKey,JSON.toJSONString(spanAnalyse,SerializerFeature.DisableCircularReferenceDetect))
    }
    linkInfo.setLinkCounts(linkCount)
    linkInfo.setLinkcurtLayer(linklayer+1)
    (initSave,statisticMap)
  }

  def getADByError(linkId:String,spanIds:util.Set[String]): util.HashMap[String,util.Set[String]] ={
    val linkIdSet=CommonUtils.readRedis2Linkid(spanIds)
    val set:util.Set[String] =new util.HashSet[String]()
    for(spanid<-spanIds){
      set.add(linkId+":"+spanid)
    }
    val ADMap=new util.HashMap[String,util.Set[String]]
    for(newLinkid<-linkIdSet){
      ADMap.put(newLinkid,set)
    }
    ADMap
  }

}
