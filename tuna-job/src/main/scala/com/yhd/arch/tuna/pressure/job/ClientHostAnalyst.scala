package com.yhd.arch.tuna.pressure.job

import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.yhd.arch.tuna.linktree.util.Config
import com.yhd.arch.tuna.pressure.dto.{AppHostStatistics, ClientHostStatistics}
import com.yhd.arch.tuna.pressure.util.{ResultReportUtil, SparkHCUtil}
import com.yhd.arch.tuna.pressure.zk.ClusterUtils
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants
import com.yihaodian.architecture.hedwig.common.util.ZkUtil
import com.yihaodian.util.SparkPoolIdParam
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._

/**
 * Created by root on 5/16/16.
 */

class ClientHostAnalyst() {
  private var domainPath=""
  /**记录初始计算的成功率*/
  private val succeedRateMap=new util.HashMap[String,Double]()

  /**记录初始计算的平均耗时*/
  private val initAvgCostMap=new util.HashMap[String,Double]()
  /**对应/TheStore下服务注册路径*/
  private var hostServiceMap:util.HashMap[String,util.TreeSet[String]]=null
  /**压测对象poold*/
  private var pool=""
  /**选取压测host*/
  private var randHost=""
  private var isCheckCount=false
  private val checkoutAtomic=new AtomicInteger(0)  //checkout异常 计数器
  private val sAtomic=new AtomicInteger(0)  //checkout异常 计数器
  private var sparkParam:SparkPoolIdParam=null

  def getLoger():Logger={
    val aclass=this.getClass
    LoggerFactory.getLogger(aclass)
  }
  /**分组下host对应的统计值*/
  private val groupHostMap=new util.HashMap[String,util.HashMap[String,ClientHostStatistics]]()

  /**分组下service对应的tps*/
  private val groupServiceMap=new util.HashMap[String,util.HashMap[String,AtomicInteger]]()
  //private val hostsMap=new util.HashMap[String,HostStatistics]()


  @volatile private var isRun=true
  def isCheck(): Boolean ={
    isRun
  }
  def init(param:SparkPoolIdParam): Unit ={
    sparkParam=param
    domainPath=param.getDomainPath
    pool=param.getPoolId
  }
  def getHostServiceMap():  util.HashMap[String,util.TreeSet[String]] ={
    hostServiceMap=ClusterUtils.getHostService(domainPath)
    if(hostServiceMap!=null){
      getRandHost
    }
    hostServiceMap
  }

  def getLocalHostServiceMap():util.HashMap[String,util.TreeSet[String]]={
    hostServiceMap
  }

  // val groupMap=new util.HashMap[String,]()
  def analyst(list:util.ArrayList[AppHostStatistics])={
    if(hostServiceMap.size()<=1){
      stopCheck()
    }else {
      if (list.size() > 0) {
        val hostSize = hostServiceMap.size()
        val groupCount = new util.HashMap[String, AtomicInteger]()
        println("AppHostStatistics :List.size=" + list.size())
        for (appHostStatistics <- list) {
          val groupMap = appHostStatistics.getGroupsMap()

          for (entry <- groupMap.entrySet()) {
            val groupName = entry.getKey
            val value = entry.getValue
            //计算分组tps
            var gAtomic: AtomicInteger = null
            if (!groupCount.containsKey(groupName)) {
              gAtomic = new AtomicInteger()
              groupCount.put(groupName, gAtomic)
            } else {
              gAtomic = groupCount.get(groupName)
            }

            var serviceMap: util.HashMap[String, AtomicInteger] = null

            var hostMap: util.HashMap[String, ClientHostStatistics] = null
            if (!groupHostMap.containsKey(groupName)) {
              hostMap = new util.HashMap[String, ClientHostStatistics]()
              groupHostMap.put(groupName, hostMap)

              serviceMap = new util.HashMap[String, AtomicInteger]()
              groupServiceMap.put(groupName, serviceMap)
            } else {
              hostMap = groupHostMap.get(groupName)
              serviceMap = groupServiceMap.get(groupName)
            }
            for (en <- value.entrySet()) {
              val host = en.getKey
              val appHostStatistics = en.getValue

              var clientStatistic: ClientHostStatistics = null
              if (!hostMap.containsKey(host)) {
                clientStatistic = new ClientHostStatistics
                clientStatistic.setHost(host)
                clientStatistic.setPoolHostCount(hostSize)
                clientStatistic.setGroupName(groupName)
                clientStatistic.setOperationHost(randHost)
                val treeSet = hostServiceMap.get(host)
                clientStatistic.initWeight(treeSet)

                hostMap.put(host, clientStatistic)
              } else {
                clientStatistic = hostMap.get(host)
              }

              val hmap = appHostStatistics.getHostServiceMap()
              if (serviceMap == null) {
                serviceMap.putAll(hmap)
              } else {
                for (atomicEntry <- hmap.entrySet()) {
                  val key = atomicEntry.getKey
                  val va = atomicEntry.getValue
                  var sAtomic: AtomicInteger = null

                  if (!serviceMap.containsKey(key)) {
                    sAtomic = new AtomicInteger(0)
                    serviceMap.put(key, sAtomic)
                  } else {
                    sAtomic = serviceMap.get(key)
                  }
                  sAtomic.addAndGet(va.intValue())
                  gAtomic.addAndGet(va.intValue())
                }
              }
              clientStatistic.add(appHostStatistics)
            }
          }
        }
        println("groupServiceMap", groupServiceMap)
        //计算成功率,平均耗时
        for (entry <- groupHostMap.entrySet()) {
          val groupName = entry.getKey
          val value = entry.getValue
          for (en <- value.entrySet()) {
            val clientStatistics = en.getValue
            clientStatistics.setGroupCount(groupCount.get(groupName))
            clientStatistics.makeSucceedRate()
            clientStatistics.makeAvgCostTime()
          }
        }
        getGroupName
        checkCount
        increaseWeight
        //不管check是否成功1 min后map都进行一次clear：目的修改weight收集的数据不是最新修改后的数据
        if (checkoutAtomic.incrementAndGet() == Config.CHECK_CLEAR_SIZE) {
          groupHostMap.clear()
          groupServiceMap.clear()
        }

      }
    }
  }
  private var groupName=""
  def getGroupName(): Unit ={
    val iterator=groupHostMap.keySet().iterator()
    if(iterator.hasNext){
      groupName=iterator.next()
    }

  }
private var inited=false
  def checkCount: Unit ={
    if(!isCheckCount) {
      println("*********************************")
      println("checkCount"+randHost)
      println("*********************************")
      val hostMap=groupHostMap.get(groupName)
      var clientStatistics:ClientHostStatistics=null
      if(!hostMap.containsKey(randHost)) {
        stopCheck()
        return
      }
      clientStatistics= hostMap.get(randHost)

      val serviceMap=groupServiceMap.get(groupName)
      val size = hostMap.size()
      val check=clientStatistics.checkCount(size,serviceMap)
      if(check==0){
        if(!inited){
          succeedRateMap.putAll(clientStatistics.getSucceedRateMap())
          initAvgCostMap.putAll(clientStatistics.getAvgCostTime())
          println("clientStatistics.getSucceedRateMap()="+clientStatistics.getSucceedRateMap()+
            "initAvgCostMap:: "+initAvgCostMap)
          val serviceSize=hostServiceMap.get(randHost).size()
          if(succeedRateMap.size()==serviceSize){
            inited=true
            isCheckCount=true
            /**将初始weight值写入zk*/
            SparkHCUtil.initializedWeight(pool,clientStatistics.getSerWeight)
          }else{
            succeedRateMap.clear()
            initAvgCostMap.clear()
          }

        }else {
          isCheckCount = true
        }
      }else if(check!=0&&checkoutAtomic.intValue()==Config.CHECK_EXCEPTION_TIMES){
        //调用量检测异常
        println("check called counts is wrong")
        clientStatistics.setFlag(1)
        clientStatistics.makeCountDesc()
        stopCheck
      }else{
        //check count is failed and checkoutAtomic is increase when  check is succeed and checkoutAtomic>MergeStream.CHECK_CLEAR_SIZE
        //the method of putResultAnalyst is not execute
        // the checkoutAtomic can't set zero value which ensure the times equals MergeStream.CHECK_EXCEPTION_TIMES to exit
        groupHostMap.clear()
        groupServiceMap.clear()
        sAtomic.set(0)
      }
    }
  }

//增加权重值
  private var errorCheckCost=0
  def increaseWeight(): Unit ={
    if(isCheckCount){
      val hostStatistics = groupHostMap.get(groupName).get(randHost)
      val checkRate=hostStatistics.checkSucceedRate(this,sparkParam)
      val checkCost=hostStatistics.checkCostTime(initAvgCostMap)

      println("checkSucceedRate:"+checkRate+"  "+sAtomic.intValue()+" checkCost:"+checkCost)
      if(!checkCost){
        errorCheckCost+=1
      }else{
        errorCheckCost=0
      }
      if(sAtomic.incrementAndGet()==Config.CHECK_CLEAR_SIZE*2) {
        putResultAnalyst()
        hostStatistics.increaseWeight()
        isCheckCount=false
        checkoutAtomic.set(0)
        sAtomic.set(0)
        errorCheckCost=0
      }else if(!checkRate&&checkoutAtomic.intValue()>=Config.CHECK_EXCEPTION_TIMES){
        hostStatistics.setFlag(1)
        hostStatistics.makeRateDescription(pool,sparkParam)
        stopCheck()
      }else if (errorCheckCost>=Config.CHECK_CLEAR_SIZE&&checkoutAtomic.intValue()>=Config.CHECK_EXCEPTION_TIMES){
        hostStatistics.setFlag(1)
        hostStatistics.makeCostDesc(pool,sparkParam)
        stopCheck()
      }else if(!checkRate||errorCheckCost>=Config.CHECK_CLEAR_SIZE){
        println("check succedRate method failed and check count succeed")
        sAtomic.set(0)
      }
    }

  }

  /**
    * 将统计后结果写入mongoDB*/
  def putResultAnalyst(): Unit ={
    val cloneMap = new util.HashMap[String,util.HashMap[String, ClientHostStatistics]]
    for (en <- groupHostMap.entrySet()) {
      val hash=new util.HashMap[String,ClientHostStatistics]()
      val value=en.getValue
      for(entry<-value.entrySet())
        hash.put(entry.getKey,entry.getValue)
      cloneMap.put(en.getKey, hash)
    }
    ResultReportUtil.putResultMap(pool, cloneMap)
  }

//  def checkSucceedRate(hostStatistics:ClientHostStatistics): Boolean ={
//    val b=hostStatistics.checkSucceedRate(this,sparkParam)
//    b
//  }
  def stopCheck(): Unit ={
    isRun=false
    putResultAnalyst
    if(groupHostMap.containsKey(groupName)){
      val hostStatistics = groupHostMap.get(groupName).get(randHost)
      if(hostStatistics!=null)
      hostStatistics.recoverWeight()
    }
    recover()
  }
  def recover(): Unit ={
    try {
      val poolId = pool.replaceAll("/", "#")
      val path = InternalConstants.BASE_ROOT_FLAGS + "/" + poolId + ParamConstants.TUNA_MESSURE_PATH
      val sparkparam: SparkPoolIdParam = ZkUtil.getZkClientInstance.readData(path)
      sparkparam.setStatus(0)
      ZkUtil.getZkClientInstance.writeData(path, sparkparam)
      SparkHCUtil.delete(pool)
      println("########################The End##################")
    } catch {
      case ex: Exception => getLoger.error("recover failed",ex)
    }
  }

  def getRandHost(): Unit ={
    val iterator = hostServiceMap.keySet().iterator()
    val size = hostServiceMap.size()
    val num = makeRandom(size)
    var k = 0
    while (iterator.hasNext) {
      if (k == num) {
        randHost =iterator.next()
      }
      k += 1
    }
  }
  def makeRandom(size:Integer): Integer ={
    val rand=(Math.random()*size).toInt
    rand
  }
  def getSucceedRateMap():util.HashMap[String,Double]={
    println("initialized succeedRateMap::"+succeedRateMap)
    succeedRateMap
  }
}
