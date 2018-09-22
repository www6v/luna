package com.yhd.arch.tuna.stream

import java.util

import com.yhd.arch.tuna.handle.DependencyByGlobalIdHandler
import com.yhd.arch.tuna.linktree.dto.MethodAnalyse
import com.yhd.arch.tuna.pressure.handler.StreamReceiver
import com.yhd.arch.tuna.pressure.zk.PoolDataWatcher
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream
/**
  * Created by root on 8/17/16.
  */
object DStreamProcess {
  def process(stream: DStream[ClientBizLog]): Unit ={
    //capacity measure
   // pressureJob(stream)

    //pool dependence
  //  poolDependenceJob(stream)
    //log report
  //  methodAnalystJob(stream)

 //   dependenceJob(stream)

    dependency(stream)
  }

  private[stream] def pressureJob(stream: DStream[ClientBizLog]): Unit = {
    val mapstream=stream.map{msg:ClientBizLog=>(msg.getProviderApp,msg)}
    val groupStream=mapstream.filter(filterMsg=>{
      pressurefilter(filterMsg._2)
    }).groupByKey()

    groupStream.foreachRDD(StreamReceiver.operationRDD)

  }
  private[stream] def pressurefilter= (msg:ClientBizLog)=>{
    val map=PoolDataWatcher.getPoolIdParamMap()
    var appCode=msg.getProviderApp
    appCode=appCode.replaceAll("#","/")
    if(map.containsKey(appCode)){
      true
    }else {
      false
    }
  }

//  private[driver] def poolDependenceJob(ms : DStream[ClientBizLog]) = {
//    val uniqIdTupleStream = ms.map{msg: ClientBizLog => (msg.getUniqReqId, msg)}
//    uniqIdTupleStream.filter(MsgTuple=>{
//      poolDependencefilters(MsgTuple._2)
//    }).groupByKey().foreachRDD(PoolDependenceByGlobalIdJobUtils.processPoolDependence)
//  }



//  private[driver] def methodAnalystJob(ms : DStream[ClientBizLog]) = {
//    ms.foreachRDD(MethodAnalyseJobUtils.processMethodAnalyse)
//  }

  def dependenceJob(stream: DStream[ClientBizLog]): Unit ={
    val successStream=stream.filter(curtLog=>{poolDependencefilters(curtLog) })

    val globalIdStream=successStream.map{msg:ClientBizLog =>msg.getUniqReqId->msg}.groupByKey()
    val dependecyStream=globalIdStream.map{msgMaps:(String,Iterable[ClientBizLog])=>
      DependencyByGlobalIdHandler.mergeMsgByKey(msgMaps._2)->(createMethodAnalyse(msgMaps._2)->1)}

    val reduceStream=dependecyStream.reduceByKey((a,b)=>handleAdd(a,b))
    reduceStream.foreachRDD(DependencyByGlobalIdHandler.handlerRDDPartition)
  }

  // filter the error log
  private[stream] def poolDependencefilters(tuple: ClientBizLog) : Boolean = {
    if (tuple != null ){
      return tuple.getSuccessed != -1
    }
    return false
  }


  def handleAdd(a:(util.Map[Integer,MethodAnalyse],Int),b:(util.Map[Integer,MethodAnalyse],Int)): (util.Map[Integer,MethodAnalyse],Int) ={
    val c=a._2+b._2
    (a._1,c)
  }

  def handleAdds(a:(util.Map[Integer,MethodAnalyse],String),b:(util.Map[Integer,MethodAnalyse],String)): (util.Map[Integer,MethodAnalyse],String)={
    a._1.putAll(b._1)
    val str=a._2+b._2
    (a._1,str)
  }
  def createMethodAnalyse(iterableMsg:Iterable[ClientBizLog]): util.Map[Integer,MethodAnalyse] ={
    val map: util.Map[Integer, MethodAnalyse] = new util.TreeMap[Integer, MethodAnalyse]()

    iterableMsg.foreach(
      msg=> {
        if (msg.getCurtLayer != null) {
          val methodAnalyse = new MethodAnalyse
          methodAnalyse.setCallApp(msg.getCallApp)
          methodAnalyse.setCallHost(msg.getCallHost)
          methodAnalyse.setClientMethodName(msg.getMethodName)
          methodAnalyse.setProviderApp(msg.getProviderApp)
          methodAnalyse.setProviderHost(msg.getProviderHost)
          methodAnalyse.setServiceMethodName(msg.getServiceMethodName)
          methodAnalyse.setServiceGroup(msg.getServiceGroup)
          methodAnalyse.setServiceName(msg.getServiceName)
          methodAnalyse.setReqTime(msg.getReqTime.getTime)
          methodAnalyse.setRespTime(msg.getRespTime.getTime)
          methodAnalyse.setCurtLayer(msg.getCurtLayer)
          map.put(msg.getCurtLayer, methodAnalyse)
        }
      })
    map
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

  def dependency(stream:DStream[ClientBizLog]): Unit ={

    val successStream=stream.filter(curtLog=>{poolDependencefilters(curtLog) })

    successStream.cache()

    val globalIdStream=successStream.map{msg:ClientBizLog =>msg.getUniqReqId->createMethodAnalyse(msg)}

    globalIdStream.reduceByKey((a,b)=>handleAdds(a,b)).foreachRDD(DependencyByGlobalIdHandler.handlerRDDByPartitions)
  }


  def fullLinkedAnalyse(stream:DStream[ClientBizLog]): Unit ={
    val successStream=stream.filter(curtLog=>{poolDependencefilters(curtLog) })

  //  val globalIdStream=successStream.map{msg:ClientBizLog =>msg.getUniqReqId->makeRLinkList(msg)}
  }


}
