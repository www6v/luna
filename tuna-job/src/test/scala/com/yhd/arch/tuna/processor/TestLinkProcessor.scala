package com.yhd.arch.tuna.processor

import java.util

import com.yhd.arch.tuna.handle.DependencyByGlobalIdHandler
import com.yhd.arch.tuna.linktree.dto.MethodAnalyse
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream

/**
  * Created by root on 9/18/16.
  */
class TestLinkProcessor extends Processor {
  def processor(dStream: DStream[ClientBizLog]): Unit ={
    dependency(dStream)
  }
  def dependency(stream:DStream[ClientBizLog]): Unit ={

    val successStream=stream.filter(curtLog=>{poolDependencefilters(curtLog) })

    successStream.cache()

    val globalIdStream=successStream.map{msg:ClientBizLog =>msg.getUniqReqId->createMethodAnalyse(msg)}

    globalIdStream.reduceByKey((a,b)=>handleAdds(a,b)).foreachRDD(DependencyByGlobalIdHandler.handlerRDDByPartitions)
  }
  // filter the error log
  private def poolDependencefilters(tuple: ClientBizLog) : Boolean = {
    if (tuple != null ){
      return tuple.getSuccessed != -1
    }
    return false
  }
  def createMethodAnalyse(msg:ClientBizLog): (util.Map[Integer,MethodAnalyse],String) ={
    val map: util.Map[Integer, MethodAnalyse] = new util.TreeMap[Integer, MethodAnalyse]()
    val stringBuilder=new StringBuilder
    if (msg.getCurtLayer != null) {
      println("curtLayer:",msg.getCurtLayer+" localLayer:"+msg.getLocalLayer)
      val methodAnalyse = new MethodAnalyse
      val callApp=msg.getCallApp
      val methodName=msg.getMethodName
      val providerApp=msg.getProviderApp
      val serviceMethodName=msg.getServiceMethodName
      val service=msg.getServiceName
      val group=msg.getServiceGroup
      methodAnalyse.setCallApp(callApp)
      methodAnalyse.setCallHost(msg.getCallHost)
      methodAnalyse.setClientMethodName(methodName)
      methodAnalyse.setProviderApp(providerApp)
      methodAnalyse.setProviderHost(msg.getProviderHost)
      methodAnalyse.setServiceMethodName(serviceMethodName)
      methodAnalyse.setServiceGroup(group)
      methodAnalyse.setServiceName(service)
      methodAnalyse.setReqTime(msg.getReqTime.getTime)
      methodAnalyse.setRespTime(msg.getRespTime.getTime)
      methodAnalyse.setCurtLayer(msg.getCurtLayer)

      map.put(msg.getCurtLayer, methodAnalyse)

      stringBuilder.append(callApp).append(providerApp)
        .append(serviceMethodName).append(service).append(group)
    }
    (map,stringBuilder.toString())
  }
  def handleAdds(a:(util.Map[Integer,MethodAnalyse],String),b:(util.Map[Integer,MethodAnalyse],String)): (util.Map[Integer,MethodAnalyse],String)={
    a._1.putAll(b._1)
    val str=a._2+b._2
    (a._1,str)
  }
}
