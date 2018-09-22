package com.yhd.arch.tuna.linktree.hanlder

import java.util

import com.yhd.arch.tuna.linktree.dto._
import com.yhd.arch.tuna.linktree.util.LinkTreeUtil
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import org.apache.spark.streaming.Time

import scala.collection.JavaConversions._

/**
  * Created by root on 2/10/17.
  */
object TreeHander {
  /**
    * 处理log数据生成带有父子关系的节点
    *
    * @return String*/
  def handlerLinkTree(map:util.Map[String,MetricAnalyse],
                      linkInfo:LinkInfo,
                      time: Time,
                      treeMap:util.Map[String,String],
                      spanIdMap:util.Map[String,util.Set[String]],
                      treeParamMap:util.Map[String,LinkTreeParam]):String={

    val nodeMap:util.Map[String,AppNode]=new util.HashMap[String,AppNode]()
    val extraSet:util.Set[AppNode]=new util.HashSet[AppNode]()
    for(spanEntry<-map.entrySet()) {
      val spanId = spanEntry.getKey
      val metricAnalyse = spanEntry.getValue

      extraSet.addAll(LinkTreeUtil.createTreeNode(nodeMap,metricAnalyse,spanId))
    }
    extraSet.addAll(nodeMap.values())

    val linkTreeParam=createLinkTreeParam(extraSet,linkInfo,time.milliseconds)

    val treelist=new util.ArrayList[AppNode]()
    treelist.addAll(extraSet)
    val jsonTree=LinkTreeUtil.parseString(treelist)
    if(jsonTree!=null) {
      val linkId = linkInfo.getLinkId()
      if(linkInfo.getErrorCounts()<=0) {
        spanIdMap.put(linkId, map.keySet())
        treeParamMap.put(linkId, linkTreeParam)
      }
      treeMap.put(linkId,jsonTree)
    }
    jsonTree
  }


  def handlerLinkTrees(map:util.Map[String,util.Map[String,MetricAnalyse]],
                      linkInfo:LinkInfo,
                      time: Time,
                      treeMap:util.Map[String,String],
                      spanIdMap:util.Map[String,util.Set[String]],
                      treeParamMap:util.Map[String,LinkTreeParam]):String={

    val nodeMap:util.Map[String,AppNode]=new util.HashMap[String,AppNode]()
    val extraSet:util.Set[AppNode]=new util.HashSet[AppNode]()
    for(spanEntry<-map.entrySet()) {
      val spanId = spanEntry.getKey
      val hostMap= spanEntry.getValue

      val Collection=hostMap.values
      val metricAnalyse=Collection.iterator().next()
      extraSet.addAll(LinkTreeUtil.createTreeNode(nodeMap,metricAnalyse,spanId))
    }
    extraSet.addAll(nodeMap.values())

    val linkTreeParam=createLinkTreeParam(extraSet,linkInfo,time.milliseconds)

    val treelist=new util.ArrayList[AppNode]()
    treelist.addAll(extraSet)
    val jsonTree=LinkTreeUtil.parseString(treelist)
    if(jsonTree!=null) {
      val linkId = linkInfo.getLinkId()
      if(linkInfo.getErrorCounts()<=0) {
        spanIdMap.put(linkId, map.keySet())
        treeParamMap.put(linkId, linkTreeParam)
      }
      treeMap.put(linkId,jsonTree)
    }
    jsonTree
  }

  def createLinkTreeParam(extraSet:util.Set[AppNode],linkInfo: LinkInfo,time:Long): LinkTreeParam ={
    val linkTreeParam=new LinkTreeParam
    linkTreeParam.setCreateTime(time)
    val list:util.List[AppNode]=new util.ArrayList[AppNode]()
    list.addAll(extraSet)
    linkTreeParam.setAppNodeList(list)
    linkTreeParam.setLinkInfo(linkInfo)
    linkTreeParam.setLinkId(linkInfo.getLinkId())
    linkTreeParam
  }
}
