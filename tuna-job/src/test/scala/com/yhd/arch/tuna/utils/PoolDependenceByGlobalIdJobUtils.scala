package com.yhd.arch.tuna.utils

import java.util
import java.util.Date

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.yhd.arch.tuna.dao.MongoServiceTest
import com.yhd.arch.tuna.stream.MergeStreamTest
import com.yhd.arch.tuna.dto.{AppDependenceAnnotation, InvokeClass, PoolDependenceAnnotation}
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
 * Created by root on 3/21/16.
 */
object PoolDependenceByGlobalIdJobUtils {

//  private var context = new ClassPathXmlApplicationContext("applicationContext.xml")
//  private var appDependenceService = context.getBean("appDependenceService").asInstanceOf[IAppDependenceService]
// private var poolDependenceService = context.getBean("poolDependenceService").asInstanceOf[IPoolDependenceService]

  val invokeLinkMap = new util.HashMap[String, InvokeClass]()

  def processAppDependence = (rdd: RDD[(String, Iterable[ClientBizLog])], time: Time) => {
    //do with caller and provider invoke
    val appDependenceMap = new util.HashMap[Long, AppDependenceAnnotation]()
    val appDependenceAccumulator = MergeStreamTest.appDependenceAccumulator
    appDependenceAccumulator.value.clear()
    rdd.foreachPartition(partitionRecords => {
      partitionRecords.foreach(record => {
        val now = new Date
        val itr = record._2
        for (cb <- itr) {
          val key = buildKey(cb)
          if(appDependenceMap.get(key) == null){
            val appDependence = new AppDependenceAnnotation
            appDependence.setClientAppCode(cb.getCallApp)
            appDependence.setClientMethodName(cb.getMethodName)
            appDependence.setServiceGroup(cb.getServiceGroup)
            appDependence.setServiceMethodName(cb.getServiceMethodName)
            appDependence.setServiceName(cb.getServiceName)
            appDependence.setServiceVersion(cb.getServiceVersion)
            appDependence.setAppCode(cb.getProviderApp)
            appDependence.setGmtCreate(new Date())
            appDependence.setGmtModify(new Date())
            appDependenceMap.put(key, appDependence)
          }else{
            val appDependence = appDependenceMap.get(key)
            appDependence.setGmtModify(new Date())
            appDependenceMap.put(key, appDependence)
          }
          appDependenceAccumulator += appDependenceMap
        }

      })
    })
    val appMap = appDependenceAccumulator.value
    println("appMap : "+appMap)
    //write caller and provider data to mongodb
    if(appMap != null && appMap.size() > 0){
      val it = appMap.entrySet().iterator();
      val list = new util.ArrayList[AppDependenceAnnotation]();
      while (it.hasNext){
        val entry = it.next();
        val key = entry.getKey
        val value = entry.getValue
        val appDependenceAnnotation = new AppDependenceAnnotation
        appDependenceAnnotation.setAppCode(value.getAppCode)
        appDependenceAnnotation.setClientAppCode(value.getClientAppCode)
        appDependenceAnnotation.setClientMethodName(value.getClientMethodName)
        appDependenceAnnotation.setGmtCreate(value.getGmtCreate)
        appDependenceAnnotation.setGmtModify(value.getGmtModify)
        appDependenceAnnotation.setMemo(value.getMemo)
        appDependenceAnnotation.setServiceGroup(value.getServiceGroup)
        appDependenceAnnotation.setServiceMethodName(value.getServiceMethodName)
        appDependenceAnnotation.setServiceName(value.getServiceName)
        appDependenceAnnotation.setServiceVersion(value.getServiceVersion)
        list.add(appDependenceAnnotation)
      }
      val keys = MongoServiceTest.getMongoDAO().save(list)
      println("the keys is :"+keys)
    }
  }
  //analyst pool's dependence  and the result of pool dependence write mongoDB
  def processPoolDependence = (rdd: RDD[(String, Iterable[ClientBizLog])], time: Time) => {
    val callerAndProviderMap = new util.HashMap[String, util.List[String]]()
    val invokeLinkMap = new util.HashMap[String, InvokeClass]()

    val callerProviderAccumulator = MergeStreamTest.callerProviderAccumulator
    callerProviderAccumulator.value.clear()
    rdd.foreachPartition(partitionRecords => {
      partitionRecords.foreach(record => {
        val now = new Date
        val itr = record._2
        for (cb <- itr) {
          if(callerAndProviderMap.get(cb.getCallApp) == null || callerAndProviderMap.get(cb.getCallApp).size() == 0){
            val list = new util.ArrayList[String]()
            list.add(cb.getProviderApp)
            callerAndProviderMap.put(cb.getCallApp, list)
          }else{
            val list = callerAndProviderMap.get(cb.getCallApp)
            if(!list.contains(cb.getProviderApp)){
              list.add(cb.getProviderApp)
              callerAndProviderMap.put(cb.getCallApp, list)
            }
          }
          callerProviderAccumulator += callerAndProviderMap

        }
      })
    })

    val callerProviderMap = callerProviderAccumulator.value
    println("callerProviderMap : "+callerProviderMap)
    val map = transferMap(callerProviderMap)
    val list=buildList(map,time)
    if(list.size()>0){
      val keys = MongoServiceTest.getMongoDAO().save(list)
    }

    //println("callerAndProviderMap:" + map)
//    val jsonData = buildJsonData(map, time)
//
//    //write poolDependece to mongodb
//    if(jsonData != null && jsonData.size() > 0){
//      val list = new util.ArrayList[PoolDependenceAnnotation]
//      for(obj <- jsonData){
//        val jobj = obj.asInstanceOf[JSONObject]
//        val imports = jobj.get("imports")
//        val name = jobj.get("name")
//        val size = jobj.get("size")
//        val time = jobj.get("time")
//        val poolDependenceAnnotation = new PoolDependenceAnnotation
//        if(imports != null){
//          val arrayStr = new ArrayBuffer[String]()
//          val _imports = imports.asInstanceOf[util.List[String]]
//          for(s <- _imports){
//            if(s != null){
//              arrayStr += s
//            }
//          }
//          println("arrayStr:"+arrayStr)
//          poolDependenceAnnotation.setImports(arrayStr.toArray)
//        }
//        if(name != null){
//          poolDependenceAnnotation.setName(name.asInstanceOf[String])
//        }
//        if(size != null){
//          poolDependenceAnnotation.setSize(size.asInstanceOf[Integer])
//        }
//
//        println("time is :"+time.isInstanceOf[Long])
//        if(time != null){
//          poolDependenceAnnotation.setTime(new Date(time.asInstanceOf[Long]))
//        }
//        list.add(poolDependenceAnnotation)
//      }
 //     val keys = poolDependenceService.save(list)
      //println("the second keys is :"+keys)
 //   }
  }

  //prepare for the result for saving mongoDB
  def buildList(callerAndProviderMap : util.HashMap[String, util.List[String]], time: Time) : util.ArrayList[PoolDependenceAnnotation] = {
    val list = new util.ArrayList[PoolDependenceAnnotation]
    if(callerAndProviderMap != null && callerAndProviderMap.size() != 0){
      val callerAndProviderIterator = callerAndProviderMap.entrySet().iterator()
      while (callerAndProviderIterator.hasNext){
        val entry = callerAndProviderIterator.next()
        val callerApp = entry.getKey  //callerApp
        val providers = entry.getValue //provider list
        var size=0
        if(callerApp != null) {
          size= callerApp.length
        }
        val timeLong = time.milliseconds

        val poolDependenceAnnotation = new PoolDependenceAnnotation
        if(providers != null){
          val arrayStr = new ArrayBuffer[String]()
          val _imports = providers
          for(s <- _imports){
            if(s != null){
              arrayStr += s
            }
          }
          println("arrayStr:"+arrayStr)
          poolDependenceAnnotation.setImports(arrayStr.toArray)
        }
        if(callerApp != null){
          poolDependenceAnnotation.setName(callerApp)
          poolDependenceAnnotation.setSize(size)
        }
        println("time is :"+time.isInstanceOf[Long])
        poolDependenceAnnotation.setTime(new Date(timeLong))

        list.add(poolDependenceAnnotation)
      }
    }
    return list
  }
  def buildJsonData(callerAndProviderMap : util.HashMap[String, util.List[String]], time: Time) : JSONArray = {
    val jsonData = new JSONArray()
    if(callerAndProviderMap != null && callerAndProviderMap.size() != 0){
      val callerAndProviderIterator = callerAndProviderMap.entrySet().iterator()
      while (callerAndProviderIterator.hasNext){
        val entry = callerAndProviderIterator.next()
        val callerApp = entry.getKey  //callerApp
        val providers = entry.getValue //provider list
        val obj = new JSONObject()
        val size = callerApp.length
        val timeLong = time.milliseconds
        obj.put("time", timeLong);
        obj.put("name", callerApp)
        obj.put("size", size)
        obj.put("imports", providers)
        jsonData.add(obj)
      }
    }
    return jsonData
  }

  def transferMap(callerAndProviderMap : util.HashMap[String, util.List[String]]) : util.HashMap[String, util.List[String]] = {
    val map = new util.HashMap[String, util.List[String]]() //store caller and provider
    val callerList = new util.ArrayList[String]()
    val providerList = new util.ArrayList[String]()
    var tempMap = new util.HashMap[String, util.List[String]]()
    if (callerAndProviderMap != null && callerAndProviderMap.size() != 0){
      map.putAll(callerAndProviderMap)
      val callerAndProviderIterator = callerAndProviderMap.entrySet().iterator()
      while (callerAndProviderIterator.hasNext){
        val entry = callerAndProviderIterator.next()
        val callerApp = entry.getKey  //callerApp
        val providers = entry.getValue //provider list
        callerList.add(callerApp)
        if(providers != null && providers.size() != 0){
          providerList.addAll(providers)
        }
      }
      tempMap = processKeyAndValueList(callerList, providerList)
    }
    if(tempMap != null && tempMap.size() != 0){
      map.putAll(tempMap)
    }
    return map
  }

  def processKeyAndValueList(keyList : util.List[String], valueList : util.List[String]) : util.HashMap[String, util.List[String]] = {
    val tempMap = new util.HashMap[String, Int]()
    val map = new util.HashMap[String, util.List[String]]() //store caller and provider
    if(keyList != null && keyList.size() != 0 && valueList != null && valueList.size() != 0){
      for (str <- keyList){
        tempMap.put(str, 1)
      }
      for (str <- valueList){
        if (tempMap.get(str) != 1){
          map.put(str, new util.ArrayList[String]())
        }
      }
    }
    return map
  }

  def transferInvokeLinkData(invokeLinkData : util.HashMap[String, InvokeClass]) : JSONArray = {
    val invokeLinkJsonArrayData = new JSONArray()
    if(invokeLinkData != null && invokeLinkData.size() != 0){
      val invokeIterator = invokeLinkData.entrySet().iterator()
      while (invokeIterator.hasNext){
        val entry = invokeIterator.next()
        val invokeLink = entry.getKey
        val invokeClass = entry.getValue
        val invokeLinkJsonData = new JSONObject()
        invokeLinkJsonData.put("invokeLink", invokeLink)
        invokeLinkJsonData.put("invokeCount", invokeClass.getInvokeCount)
        invokeLinkJsonData.put("totalCostTime", invokeClass.getTotalCostTime)
        invokeLinkJsonArrayData.add(invokeLinkJsonData)
      }
    }
    return invokeLinkJsonArrayData
  }

  def buildKey(cb: ClientBizLog) : Long = {
    val prime = 31;
    var result = 1;
    result = prime * result +(if(cb.getCallApp == null) 0 else cb.getCallApp.hashCode)
    result = prime * result +(if(cb.getMethodName == null) 0 else cb.getMethodName.hashCode)
    result = prime * result +(if(cb.getProviderApp == null) 0 else cb.getProviderApp.hashCode)
    //result = prime * result +(if(cb.getProviderHost == null) 0 else cb.getProviderHost.hashCode)
    result = prime * result +(if(cb.getServiceName == null) 0 else cb.getServiceName.hashCode)
    result = prime * result +(if(cb.getServiceMethodName == null) 0 else cb.getServiceMethodName.hashCode)
    result = prime * result +(if(cb.getServiceGroup == null) 0 else cb.getServiceGroup.hashCode)
    result = prime * result +(if(cb.getServiceVersion == null) 0 else cb.getServiceVersion.hashCode)
    return result
  }
}