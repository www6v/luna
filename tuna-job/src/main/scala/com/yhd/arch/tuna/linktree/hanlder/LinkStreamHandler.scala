package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.yhd.arch.tuna.linktree.util.{MetricAnalyseFactory, SignIDUtils}
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.CommonUtils
import com.yihaodian.monitor.dto.ClientBizLog

import scala.collection.mutable
import scala.collection.mutable.MutableList
import scala.collection.JavaConversions._

/**
  * Created by root on 12/21/16.
  */
object LinkStreamHandler {

  def makeSpanLog(curtLog:ClientBizLog): util.HashMap[String, MetricAnalyse] = {
    val map = new util.HashMap[String, MetricAnalyse]()
    if(curtLog.getCurtLayer!=null) {
      val tuple = MetricAnalyseFactory.makeMetricAnalyse(curtLog)
      map.put(tuple._1, tuple._2)
    }
    map
  }

  def mergeSpan(a:util.HashMap[String, MetricAnalyse],b:util.HashMap[String, MetricAnalyse]): util.HashMap[String, MetricAnalyse] ={
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
    *
    * @param map util.Map[String,MetricAnalyse]
    *  @return (String,(util.Map[String,MetricAnalyse],Int))  linkid与链路错误数
    * */
  def createLinkId(map:util.Map[String,MetricAnalyse]):(String,(util.Map[String,MetricAnalyse],Int))={
    val stringBuilder=new StringBuilder
    var status=false

    for (entry <- map.entrySet()) {
      val spanid = entry.getKey
      val metricAnalyse = entry.getValue
      //判断链路是否出错
      if (metricAnalyse.getSucceed == (-1)) {
        status = true
      }
      stringBuilder.append(spanid)
    }
    val newlinkId: String = SignIDUtils.createLinkId(stringBuilder.toString())
    if(status){
      (newlinkId,(map,1))
    }else{
      (newlinkId,(map,0))
    }
  }

  /**
    * 根据spanid生成linkid
    * linkid与UniqReqid关联
    * @param map util.Map[String,MetricAnalyse]
    *  @return (String,(util.Map[String,MetricAnalyse],util.Set[String],Int))  (linkid,UniqReqId,链路错误数)
    * */
  def createLinkId(UniqReqId:String,map:util.Map[String,MetricAnalyse]):(String,(util.Map[String,MetricAnalyse],util.Set[String],Int))={
    val set:util.Set[String]=new util.HashSet[String]()
    set.add(UniqReqId)
    //获取存储的不完整的链路数据
    val metricMap=CommonUtils.getMetricByUniqReqId(UniqReqId)
    map.putAll(metricMap)

    val tmpTuple=createLinkId(map)
    (tmpTuple._1,(tmpTuple._2._1,set,tmpTuple._2._2))
  }


  def errorMapBylinkId(tuple:(String,(util.Map[String,MetricAnalyse],Int))):MutableList[(String,(util.Map[String,MetricAnalyse],Int))] ={
    val result:MutableList[(String,(util.Map[String,MetricAnalyse],Int))]=
      mutable.MutableList[(String,(util.Map[String,MetricAnalyse],Int))]()
    val linkerror=tuple._2._2
    val map=tuple._2._1
    if(linkerror>0){
      val spanset=map.keySet()
      val linkIdSet=CommonUtils.readRedis2Linkid(spanset)

      for(linkid<-linkIdSet){
        result+=((linkid,(map,1)))
      }
    }else{
      result+=((tuple._1,(map,0)))
    }
    result
  }

  /**
    * 根据spanid生成linkid
    *
    * @param map util.Map[String,MetricAnalyse]
    *  @return (String,(util.Map[String,MetricAnalyse],Int))  linkid与链路错误数
    * */
  def makeLinkId(map:util.Map[String,MetricAnalyse]):MutableList[(String,(util.Map[String,MetricAnalyse],Int))]  ={
    val stringBuilder=new StringBuilder
    var status=false

    for (entry <- map.entrySet()) {
      val spanid = entry.getKey
      val metricAnalyse = entry.getValue
      //判断链路是否出错
      if (metricAnalyse.getSucceed == (-1)) {
        status = true
      }
      stringBuilder.append(spanid)
    }
    val result:MutableList[(String,(util.Map[String,MetricAnalyse],Int))]=
      errorMapBylinkId(status,map,stringBuilder.toString())
    result
  }

  //错误数据关联
  private def errorMapBylinkId(status:Boolean,map:util.Map[String,MetricAnalyse],spanidString:String):
  MutableList[(String,(util.Map[String,MetricAnalyse],Int))] ={
    val result:MutableList[(String,(util.Map[String,MetricAnalyse],Int))]=
      mutable.MutableList[(String,(util.Map[String,MetricAnalyse],Int))]()
    if(status){
      val spanset=map.keySet()
      val linkIdSet=CommonUtils.readRedis2Linkid(spanset)

      for(linkid<-linkIdSet){
        result+=((linkid,(map,1)))
      }
    }else {
      val newlinkId: String = SignIDUtils.createLinkId(spanidString)

      result+=((newlinkId,(map,0)))

    }
    result
  }

  def mergeMetricAndUniqReqidByKey(a:(util.Map[String,MetricAnalyse],util.Set[String],Int),
                       b:(util.Map[String,MetricAnalyse],util.Set[String],Int)):
          (util.Map[String,MetricAnalyse],util.Set[String],Int) ={
    val setA=a._2
    val setB=b._2
    setA.addAll(setB)
    val tmp=mergeMetricByKey((a._1,a._3),(b._1,b._3))
    (tmp._1,setA,tmp._2)
  }

  def mergeMetricByKey(a:(util.Map[String,MetricAnalyse],Int),b:(util.Map[String,MetricAnalyse],Int)): (util.Map[String,MetricAnalyse],Int) ={
    val mapA=a._1
    val mapB=b._1
    for(entryB<-mapB.entrySet()){
      val spanId=entryB.getKey
      val metricB=entryB.getValue
      if(mapA.containsKey(spanId)){
        val metricA=mapA.get(spanId)
        metricA.merge(metricB)
      }else{
        mapA.put(spanId, metricB)
      }
    }
    val linkError=a._2+b._2
    (mapA,linkError)
  }

}
