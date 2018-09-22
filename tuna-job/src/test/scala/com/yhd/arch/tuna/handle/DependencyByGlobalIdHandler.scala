package com.yhd.arch.tuna.handle

import java.util

import com.yhd.arch.tuna.dao.LinkAnalyseDao
import com.yhd.arch.tuna.linktree.dto.{LinkAnalyse, MethodAnalyse}
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time

import scala.collection.JavaConversions._


/**
  * Created by root on 8/12/16.
  */
object DependencyByGlobalIdHandler {
  def globalIdHandlerRDD=(rdd:RDD[(String,Iterable[ClientBizLog])],time:Time)=>{
    rdd.foreachPartition(rddIterator=>{
      rddIterator.foreach(
        rddRecord=> {
          val bizIterable=rddRecord._2

        })
    })
  }

  def handlerRDDPartition=(rdd:RDD[(String,(util.Map[Integer,MethodAnalyse],Int))],time:Time)=>{
    rdd.foreachPartition(rdds=> {
      val linkAnalyseList = new util.ArrayList[LinkAnalyse]()
      rdds.foreach(
        record=> {
          val result = record._2
          val calledCounts=result._2
          val treeMap=result._1
          val elem:util.List[MethodAnalyse]=new util.ArrayList[MethodAnalyse]()
          val curtLayer=treeMap.size()+1  //链路层级数 +1是因为1个methodAnalyse就存在2层级
          elem.addAll(treeMap.values())
          val linkAnalyse=new LinkAnalyse
          linkAnalyse.setCalledCounts(calledCounts)
          linkAnalyse.setCurtLayer(curtLayer)
          linkAnalyse.setGmtCreate(time.milliseconds)
          if(elem.size>0)
            linkAnalyse.setReqTime(elem.get(0).getReqTime)
          linkAnalyse.setLinkList(elem)
          linkAnalyseList.add(linkAnalyse)
        })
      if(linkAnalyseList.size()>0) {
      //  println(time,linkAnalyseList)
       LinkAnalyseDao.getBMongoDao().save(linkAnalyseList)
      }
    })
  }

  def mergeMsgByKey(curtLogIterable:Iterable[ClientBizLog]):String={
    var call=""
    val tree:util.Map[Integer,String]=new util.TreeMap[Integer,String]()
    curtLogIterable.foreach(curtLog=> {
      if(curtLog.getCurtLayer!=null) {
        tree.put(curtLog.getCurtLayer, curtLog.getCallApp + curtLog.getMethodName + curtLog.getProviderApp + curtLog.getServiceMethodName)
      }
    })
    for(en<-tree.entrySet()){
      call=call+">>"+en.getValue
    }
    call
  }

  def handlerRDDByPartitions=(rdd:RDD[(String,(util.Map[Integer,MethodAnalyse],String))],time:Time)=>{
    rdd.foreachPartition(rdds=> {
       val linkAnalyseMap = new util.HashMap[String,LinkAnalyse]()
     // println(new Date(),"records.length",rdds.length)
      rdds.foreach(
        record => {
          val map=record._2
          val methodAnalyseMap=map._1
          val keyword=map._2
          val elem:util.List[MethodAnalyse]=new util.ArrayList[MethodAnalyse]()
          val curtLayer=methodAnalyseMap.size()+1  //链路层级数 +1是因为1个methodAnalyse就存在2层级
          elem.addAll(methodAnalyseMap.values())
          val linkAnalyse=new LinkAnalyse
          linkAnalyse.setCalledCounts(1)
          linkAnalyse.setCurtLayer(curtLayer)
          val ctime=time.milliseconds
          linkAnalyse.setGmtCreate(ctime)
          if(elem.size>0)
            linkAnalyse.setReqTime(elem.get(0).getReqTime)
          linkAnalyse.setLinkList(elem)
          linkAnalyse.setKey(keyword)
          val analyse = linkAnalyseMap.get(keyword)
          if (analyse == null) {
            linkAnalyseMap.put(keyword, linkAnalyse)
          } else {
            analyse.setCalledCounts(analyse.getCalledCounts + 1)
          }

        })
      if(linkAnalyseMap.size()>0) {
       LinkAnalyseDao.getBMongoDao().save(linkAnalyseMap.values())
      }
    })
  }

  def handlerRDDByPartition=(rdd:RDD[(String,util.Map[Integer,MethodAnalyse])],time:Time)=>{
    rdd.foreachPartition(rdds=> {
     // val linkAnalyseList = new util.ArrayList[LinkAnalyse]()
      rdds.foreach(
        record => {
          val methodAnalyseMap=record._2
          val elem:util.List[MethodAnalyse]=new util.ArrayList[MethodAnalyse]()
          val curtLayer=methodAnalyseMap.size()+1  //链路层级数 +1是因为1个methodAnalyse就存在2层级
          elem.addAll(methodAnalyseMap.values())
          val linkAnalyse=new LinkAnalyse
          linkAnalyse.setCalledCounts(1)
          linkAnalyse.setCurtLayer(curtLayer)
          val ctime=time.milliseconds
          linkAnalyse.setGmtCreate(ctime)
          if(elem.size>0)
            linkAnalyse.setReqTime(elem.get(0).getReqTime)
          linkAnalyse.setLinkList(elem)
       //   LinkAnalyseDao.update(linkAnalyse,ctime)
        })
    })
  }

}
