package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.yhd.arch.tuna.linktree.dao.service.InitMongoDao
import com.yhd.arch.tuna.linktree.dao.service.reids._
import com.yhd.arch.tuna.linktree.dto.{LinkInfo, LinkTreeParam}
import com.yhd.arch.tuna.metric.dto.StatisticData
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.slf4j.LoggerFactory

/**
  * Created by root on 2/8/17.
  */
object LinkDataHandler {
  val logger= LoggerFactory.getLogger(LinkDataHandler.getClass)
  val linkDataRedis=new LinkDataRedis
  val metricErrRedis=new MetricErrRedis
  val metricRedis=new MetricRedis

  val uniqReqIdIndexReidis=new UniqReqIdIndexReidis

  def createLinkInfo(linkId:String,errorCount:Int): LinkInfo ={
    val linkInfo=new LinkInfo
    linkInfo.setErrorCounts(errorCount)
    linkInfo.setLinkId(linkId)
    linkInfo
  }

  def handlerByPartitions=(rdd:RDD[(String,(util.Map[String,MetricAnalyse],util.Set[String],Int))],time:Time) =>{
    rdd.foreachPartition(rdds=> {
      val treeMap:util.Map[String,String]=new util.HashMap[String,String]()

      val spanIdMap:util.Map[String,util.Set[String]] = new util.HashMap[String, util.Set[String]]()

      val linkInfoMap:util.Map[String,LinkTreeParam] = new util.HashMap[String, LinkTreeParam]()

      val statisticMap:util.Map[String,String]=new util.HashMap[String,String]()
      val errorList=new util.ArrayList[util.HashMap[String,util.Set[String]]]()

      val uniqReqIdMap=new util.HashMap[util.Set[String],util.Set[String]]()
      rdds.foreach(record=>{
        val linkId=record._1
        val UniqRedIdSet=record._2._2
        val linkErrorCount=record._2._3
        val metricMap=record._2._1

        val linkInfo=createLinkInfo(linkId,linkErrorCount)

        val tmpTuple=MetricHandler.handlerMetric(linkId,linkInfo,metricMap,time)
        val isStart=tmpTuple._1
        val tmpStatisticMap=tmpTuple._2
        statisticMap.putAll(tmpStatisticMap)
        uniqReqIdMap.put(UniqRedIdSet,tmpStatisticMap.keySet())

        if(linkErrorCount>0){
          errorList.add(MetricHandler.getADByError(linkId,metricMap.keySet()))
          println("errorLinkid:"+linkId)
        }
        //错误链路不记录spanid与linkid对应关系，错误链路为子链路
        if(isStart)
          TreeHander.handlerLinkTree(metricMap,linkInfo,time,treeMap,spanIdMap,linkInfoMap)
       // uniqReqIdMap.put(UniqRedIdSet,statisticMap.keySet())
      })

      if(treeMap.size()>0){
        linkDataRedis.setTreeData(treeMap)
        linkDataRedis.setSpanData(spanIdMap)
        linkDataRedis.saveToRdis()
      }
      if(statisticMap.size()>0){
        metricRedis.setData(statisticMap)
        metricRedis.saveToRdis()

        uniqReqIdIndexReidis.setData(uniqReqIdMap)
        uniqReqIdIndexReidis.saveToRdis()
      }
      if(errorList.size()>0){
        metricErrRedis.setErrorList(errorList)
        metricErrRedis.saveToRdis()
      }
      if(linkInfoMap.size()>0){
        InitMongoDao.mongoDAO.save(linkInfoMap.values())
      }
    })
  }
}
