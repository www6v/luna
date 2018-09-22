package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.yhd.arch.tuna.linktree.dao.service.InitMongoDao
import com.yhd.arch.tuna.linktree.dao.service.reids._
import com.yhd.arch.tuna.linktree.dto.{BatchAnalyseDto, LinkInfo, LinkTreeParam}
import com.yhd.arch.tuna.linktree.util.SignIDUtils
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.CommonUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by root on 2/24/17.
  */
object HostStreamHandler {

  val logger= LoggerFactory.getLogger(HostStreamHandler.getClass)

  val linkDataRedis=new LinkDataRedis
  val metricErrRedis=new MetricErrRedis
  val hostmetricRedis=new HostMetricRedis
  val uniqReqIdIndexReidis=new UniqReqIdIndexReidis

  def handlerByPartition=(rdd:RDD[(String,BatchAnalyseDto)],time:Time) => {
    rdd.foreachPartition(rdds => {
      val treeMap: util.Map[String, String] = new util.HashMap[String, String]()

      val spanIdMap: util.Map[String, util.Set[String]] = new util.HashMap[String, util.Set[String]]()

      val linkInfoMap: util.Map[String, LinkTreeParam] = new util.HashMap[String, LinkTreeParam]()

      val statisticMap: util.Map[String, util.Map[String, String]] = new util.HashMap[String, util.Map[String, String]]()
      val errorList = new util.ArrayList[util.HashMap[String, util.Set[String]]]()

      val uniqReqIdMap = new util.HashMap[util.Set[String], util.Set[String]]()
      rdds.foreach(record => {
        val linkId=record._1
        val UniqRedIdSet=record._2.getGlobalIdSet()
        val linkErrorCount=record._2.getLinkError()
        val metricMap=record._2.getSpanHostMapMap()

        val linkInfo=createLinkInfo(linkId,linkErrorCount)

        val tmpTuple=HostMetricHandler.handlerMetric(linkId,linkInfo,metricMap,time)
        val isStart=tmpTuple._1
        val tmpStatisticMap=tmpTuple._2
        statisticMap.putAll(tmpStatisticMap)
        uniqReqIdMap.put(UniqRedIdSet,tmpStatisticMap.keySet())

        if(linkErrorCount>0){
          errorList.add(HostMetricHandler.getADByError(tmpStatisticMap.keySet(),metricMap.keySet()))
          //   println("errorLinkid:"+linkId)
        }
        //错误链路不记录spanid与linkid对应关系，错误链路为子链路
        if(isStart)
          TreeHander.handlerLinkTrees(metricMap,linkInfo,time,treeMap,spanIdMap,linkInfoMap)
        uniqReqIdMap.put(UniqRedIdSet,statisticMap.keySet())
      })

      saveData(treeMap,spanIdMap,statisticMap,linkInfoMap,errorList,uniqReqIdMap)
    })
  }

  def saveData(treeMap: util.Map[String, String],
               spanIdMap: util.Map[String, util.Set[String]],
               statisticMap: util.Map[String, util.Map[String, String]],
               linkInfoMap:util.Map[String,LinkTreeParam],
               errorList:util.ArrayList[util.HashMap[String, util.Set[String]]],
               uniqReqIdMap:util.Map[util.Set[String],util.Set[String]]
              ): Unit ={
    if(treeMap.size()>0){
      linkDataRedis.setTreeData(treeMap)
      linkDataRedis.setSpanData(spanIdMap)
      linkDataRedis.saveToRdis()
    }
    if(statisticMap.size()>0){
      hostmetricRedis.setData(statisticMap)
      hostmetricRedis.saveToRdis()

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
  }

  def createLinkInfo(linkId:String,errorCount:Int): LinkInfo ={
    val linkInfo=new LinkInfo
    linkInfo.setErrorCounts(errorCount)
    linkInfo.setLinkId(linkId)
    linkInfo
  }
}
