package com.yhd.arch.tuna.pressure.dto

import java.util
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by root on 5/12/16.
 */
class HostStatistics extends Serializable{
  private val hostServiceMap=new util.HashMap[String,AtomicInteger]()
  private val succeedMap=new util.HashMap[String,AtomicInteger]()
  private val costMap=new util.HashMap[String,AtomicInteger]()


  def getHostServiceMap():util.HashMap[String,AtomicInteger]={
    hostServiceMap
  }

  def getSucceedMap():util.HashMap[String,AtomicInteger]={
    succeedMap
  }
  

  def getCostMap():util.HashMap[String,AtomicInteger]={
    costMap
  }

  def statistics(key:String,
                 succeed: Integer,
                 cost:Integer): Unit ={
    var hostSAtomic=hostServiceMap.get(key)
    var sucAtomic=succeedMap.get(key)
    var costAtomic=costMap.get(key)
   // var serviceAtomic=serviceCountMap.get(key)
    if(hostSAtomic==null){
      hostSAtomic=new AtomicInteger(0)
      hostServiceMap.put(key,hostSAtomic)

      sucAtomic=new AtomicInteger(0)
      succeedMap.put(key,sucAtomic)

      costAtomic=new AtomicInteger(0)
      costMap.put(key,costAtomic)

  //    serviceAtomic=new AtomicInteger(0)
   //   serviceCountMap.put(key,serviceAtomic)
    }
    hostSAtomic.incrementAndGet()
    costAtomic.addAndGet(cost)
  //  serviceAtomic.set(serviceCount)
    if(succeed>0){
      sucAtomic.incrementAndGet()
    }
  }

}
