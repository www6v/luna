package com.yhd.arch.tuna.linktree.dto

import java.util

import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
/**
  * Created by root on 3/8/17.
  */
class BatchAnalyseDto extends Serializable{
  private var linkerror=0

  private var globalIdSet:util.Set[String]=new util.HashSet[String]()

  private var spanHostMap:util.Map[String,util.Map[String,MetricAnalyse]]=new util.HashMap[String,util.Map[String,MetricAnalyse]]()

  private var spanMap:util.Map[String,MetricAnalyse]=new util.HashMap[String,MetricAnalyse]()

  def setLinkError(error:Int): Unit ={
      this.linkerror=error
  }
  def getLinkError():Int={
    linkerror
  }

  def addGlobalIdSet(globalId:String): Unit ={
    globalIdSet.add(globalId)
  }

  def setGlobalIdSet(set:util.Set[String]): Unit ={
    globalIdSet=set
  }

  def getGlobalIdSet():util.Set[String]={
    globalIdSet
  }

  def setSpanHostMap(spanMap:util.Map[String,util.Map[String,MetricAnalyse]]): Unit ={
    this.spanHostMap=spanMap
  }

  def getSpanHostMapMap():util.Map[String,util.Map[String,MetricAnalyse]]={
    spanHostMap
  }

  def setSpanMap(map:util.Map[String,MetricAnalyse]): Unit ={
    this.spanMap=map
  }

  def getSpanMap(): util.Map[String,MetricAnalyse] ={
    spanMap
  }

}
