package com.yhd.arch.tuna.pressure.zk

import java.util

/**
 * Created by root on 3/3/16.
 */
object ClusterUtils {
  private val domainPathMap=new util.HashMap[String,util.HashMap[String,util.TreeSet[String]]]()

  def setClusterServer(domain: String ,hostService: util.HashMap[String ,util.TreeSet[String]]): Unit ={
    domainPathMap.put(domain,hostService)
  }

  def getHostService(domain: String): util.HashMap[String,util.TreeSet[String]] ={
    val servicePath=domainPathMap.get(domain)
    if(servicePath!=null){
      return domainPathMap.get(domain)
    }
    return null
  }


  def getCluster(): Unit ={
    val keyset=domainPathMap.keySet()
    val iter=keyset.iterator()
    while(iter.hasNext){
      print("   "+iter.next())
    }
  }
}
