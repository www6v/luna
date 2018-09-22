package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.yhd.arch.tuna.linktree.dto.BatchAnalyseDto
import com.yhd.arch.tuna.linktree.util.{MetricAnalyseFactory, SignIDUtils}
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.CommonUtils
import com.yihaodian.monitor.dto.ClientBizLog

import scala.collection.JavaConversions._
import scala.util.control.Breaks
/**
  * Created by root on 3/13/17.
  */
object AnalyseLogHandler {

  //生成Span id
  def makeSpan(curtLog:ClientBizLog):BatchAnalyseDto={
    val batchAnalyseDto=new BatchAnalyseDto
    val map:util.Map[String, util.Map[String,MetricAnalyse]] = new util.HashMap[String, util.Map[String,MetricAnalyse]]()
    batchAnalyseDto.setSpanHostMap(map)
    val hostMap=new util.HashMap[String,MetricAnalyse]()
    if(curtLog.getCurtLayer!=null) {
      val tuple = MetricAnalyseFactory.makeMetricAnalyse(curtLog)
      val metricAnalyse=tuple._2
      hostMap.put(metricAnalyse.getProviderHost,metricAnalyse)
      map.put(tuple._1,hostMap)
    }
    batchAnalyseDto
  }

  //统计计算
  def mergeSpans(dto1:BatchAnalyseDto, dto2:BatchAnalyseDto):BatchAnalyseDto={
    val a=dto1.getSpanHostMapMap()
    val b=dto2.getSpanHostMapMap()

    for(entryB<-b.entrySet()){
      val spanid=entryB.getKey
      val hostMapB=entryB.getValue

      if(a.containsKey(spanid)) {
        val hostMapA=a.get(spanid)
        mergeMetric(hostMapA, hostMapB)
      }else{
        a.put(spanid,hostMapB)
      }
    }
   // dto1.setSpanHostMap(a)
    dto1
  }

  def mergeMetric(a:util.Map[String, MetricAnalyse], b:util.Map[String, MetricAnalyse]): util.Map[String, MetricAnalyse] ={
    for(entryB<-b.entrySet()){
      val key=entryB.getKey
      val analyseB=entryB.getValue
      if(a.containsKey(key)){
        val analyseA=a.get(key)
        analyseA.metricMerge(analyseB)
      }else{
        a.put(key,analyseB)
      }
    }
    a
  }

  /**
    * 根据spanid生成linkid
    * linkid与UniqReqid关联
    *
    * @param dto BatchAnalyseDto
    * @param UniqReqId
    *  @return (String,BatchAnalyseDto)
    * */
  def createLinkId(UniqReqId:String,dto:BatchAnalyseDto):(String,BatchAnalyseDto)={
    dto.addGlobalIdSet(UniqReqId)
    val map=dto.getSpanHostMapMap()
    //获取存储的不完整的链路数据
    val metricMap=CommonUtils.getMetric(UniqReqId)
    map.putAll(metricMap)
    val tmpTuple=createLinkId(dto)
    (tmpTuple._1,tmpTuple._2)
  }

  /**
    * 根据spanid生成linkid
    *
    * @param dto BatchAnalyseDto
    *  @return (String,BatchAnalyseDto)
    * */
  def createLinkId(dto:BatchAnalyseDto):(String,BatchAnalyseDto)={
    val stringBuilder=new StringBuilder
    var status=false
    val map=dto.getSpanHostMapMap()

    for (entry <- map.entrySet()) {
      val spanid = entry.getKey
      val hostmap = entry.getValue
      //判断链路是否出错
      if(!status) {
        status=isSuccess(hostmap)
      }
      stringBuilder.append(spanid)
    }
    val newlinkId: String = SignIDUtils.createLinkId(stringBuilder.toString())
    if(status){
      dto.setLinkError(1)
      (newlinkId,dto)
    }else{
      (newlinkId,dto)
    }
  }

  //判断链路是否出错
  def isSuccess(hostmap:util.Map[String,MetricAnalyse]): Boolean ={
    var status=false
    val loop = new Breaks
    loop.breakable {
      for (hostMetric <- hostmap.entrySet()) {
        val metricAnalyse = hostMetric.getValue
        if (metricAnalyse.getSucceed == (-1)) {
          status = true
          loop.break()
        }
      }
    }
    status
  }

  def addMetricByKey(dto1: BatchAnalyseDto,dto2: BatchAnalyseDto): BatchAnalyseDto ={
    dto1.getGlobalIdSet().addAll(dto2.getGlobalIdSet())
    val linkError=dto1.getLinkError()+dto2.getLinkError()
    dto1.setLinkError(linkError)

    val mapA=dto1.getSpanHostMapMap()
    val mapB=dto2.getSpanHostMapMap()
    for(entryB<-mapB.entrySet()){
      val spanId=entryB.getKey
      val hostMapB=entryB.getValue
      if(mapA.containsKey(spanId)){
        val hostMapA=mapA.get(spanId)
        mergeMetric(hostMapA,hostMapB)
      }else{
        mapA.put(spanId, hostMapB)
      }
    }
    dto1
  }

}
