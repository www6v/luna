package com.yhd.arch.tuna.pressure.dto

import java.math.BigDecimal
import java.util
import java.util.concurrent.atomic.AtomicInteger

import com.yhd.arch.tuna.pressure.job.ClientHostAnalyst
import com.yhd.arch.tuna.pressure.util.HostWeightUtil
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.util.SparkPoolIdParam

import scala.collection.JavaConversions._
/**
 * Created by root on 5/19/16.
 */
class ClientHostStatistics {
  //host下所有服务调用量map
  private val hostServiceMap = new util.HashMap[String, AtomicInteger]()
  //host下所有服务成功调用量map
  private val succeedMap = new util.HashMap[String, AtomicInteger]()
  //host 消耗时间
  private val costMap = new util.HashMap[String, AtomicInteger]()

  private val avgCostMap = new util.HashMap[String, Double]()
  //host所属分组调用量
  private val groupCount = new AtomicInteger()
  //host下所有服务权重值
  private val weightMap = new util.HashMap[String, Integer]()

  private val originWeighMap = new util.HashMap[String, Integer]()
  private val serPathMap = new util.HashMap[String, String]()

  private val succeedRateMap = new util.HashMap[String, Double]()
  private var groupName = ""

  /** 用于标记计算成功率 */
  private var runStatus = true

  private val failedMap = new util.HashMap[String, Double]()

  //某一pool下所有host的个数
  private var poolHostCount = 0

  private var host = ""

  private var operationHost=""
  /** flag=0 the result is succeed
    * flag=1 the result is failed */
  private var flag = 0

  private var desc=""


  def setHost(host: String): Unit = {
    this.host = host
  }

  def getHost(): String = {
    host
  }

  def setOperationHost(host:String): Unit ={
    this.operationHost=host
  }

  def getOperationHost():String={
    operationHost
  }

  def setPoolHostCount(hostCount: Integer): Unit = {
    poolHostCount = hostCount
  }

  def getPoolHostCount(): Integer = {
    poolHostCount
  }

  def setGroupName(gName: String): Unit = {
    this.groupName = gName
  }

  def getGroupName(): String = {
    groupName
  }

  def getFiledMap(): util.HashMap[String, Double] = {
    failedMap
  }

  def getHostServiceMap(): util.HashMap[String, AtomicInteger] = {
    hostServiceMap
  }

  def getSucceedMap(): util.HashMap[String, AtomicInteger] = {
    succeedMap
  }

  def getWeightMap(): util.HashMap[String, Integer] = {
    weightMap
  }

  def getSucceedRateMap(): util.HashMap[String, Double] = {
    succeedRateMap
  }

  def setGroupCount(atomic: AtomicInteger): Unit = {
    groupCount.set(atomic.intValue())
  }

  def getGroupCount(): AtomicInteger = {
    groupCount
  }

  def getOriginWeighMap(): util.HashMap[String, Integer] = {
    originWeighMap
  }

  def getSerPathMap(): util.HashMap[String, String] = {
    serPathMap
  }

  def getAvgCostTime(): util.HashMap[String, Double] = {
    avgCostMap
  }

  def getCostMap(): util.HashMap[String, AtomicInteger] = {
    costMap
  }


  def getSerWeight(): util.HashMap[String, Integer] = {
    val serWeight = new util.HashMap[String, Integer]()
    for (en <- serPathMap.entrySet()) {
      val serviceName = en.getKey
      val serpath=en.getValue
      serWeight.put(serpath, originWeighMap.get(serviceName))
    }
    serWeight
  }

  def add(v: HostStatistics): Unit = {
    val vhostServiceMap = v.getHostServiceMap()
    val vcostMap = v.getCostMap()
    val vsuccessMap = v.getSucceedMap()

    for (entry <- vhostServiceMap.entrySet()) {
      val key = entry.getKey
      val value = entry.getValue
      val atomic = getHostServiceMap().get(key)
      if (atomic == null) {
        hostServiceMap.put(key, value)
        costMap.put(key, vcostMap.get(key))
        succeedMap.put(key, vsuccessMap.get(key))
        //      getServiceCountMap.put(key,serviceCountMap.get(key))
      } else {
        atomic.addAndGet(value.intValue())
        costMap.get(key).addAndGet(vcostMap.get(key).intValue())
        succeedMap.get(key).addAndGet(vsuccessMap.get(key).intValue())
        //      getServiceCountMap.get(key).addAndGet(serviceCountMap.get(key).intValue())
      }
    }
  }

  /**
   * 用于检测修改weight值后，tps是否符合hedwig设计的路由策略*/
  def checkCount(groupSize: Integer, serviceMap: util.HashMap[String, AtomicInteger]): Integer = {
    var checkCount = 0
    for (entry <- getHostServiceMap().entrySet()) {
      val serviceName = entry.getKey
      val hAtomic = entry.getValue
      val sAtomic = serviceMap.get(serviceName)

      val countRate = hAtomic.intValue() * 1.0 / sAtomic.intValue()
      val weight = weightMap.get(serviceName)

      val weightRate = weight * 1.0 / (groupSize + weight - 1)
      val minRate = (weight - 1) * 1.0 / (groupSize + weight - 1 - 1)

      println(serviceName + " >>>>> serviceCount==" + sAtomic + " hostServiceCount=" + hAtomic
        + " countRate= " + countRate + "  weightRate= " + weightRate + "  minRate=" + minRate)

      if (countRate <= minRate) {
        println("change weight is not succeedful")
        checkCount += 1
        //    HostWeightUtil.increaseWeight(serPathMap.get(serName), weight)
      }
    }
    checkCount
  }

  /** 计算host下所有service 的成功率 */
  def makeSucceedRate(): Unit = {
    for (entry <- succeedMap.entrySet()) {
      val serviceName = entry.getKey
      val succeed = entry.getValue.intValue()
      val hostServiceCount = hostServiceMap.get(serviceName).intValue()
      val tmp = succeed * 1.0 / hostServiceCount
      val bigD=new BigDecimal(tmp)
      val succeedRate=bigD.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue()

      succeedRateMap.put(serviceName, succeedRate)
    }
  }

  def checkSucceedRate(clientHostAnalyst: ClientHostAnalyst, sparkPoolIdParam: SparkPoolIdParam): Boolean = {
    println("*************************************")
    println("ClientHostStatistics.checkSucceedRate")
    println("*************************************")
    var result = true
    for (entry <- clientHostAnalyst.getSucceedRateMap().entrySet()) {
      val serviceName = entry.getKey
      val originRate = entry.getValue
      val rate = succeedRateMap.get(serviceName)
      println("checkSucceedRate======> rate=" + rate + " history.rate=" + originRate)
      val d = originRate - rate
      if (d > sparkPoolIdParam.getDownRate / 100) {
        println("checkSucceedRate is failed======> serviceName=" + serviceName + " rate=" + rate + " d=" + d)
        failedMap.put(serviceName, rate)
        result = false
      }
    }
    result
  }

  def makeAvgCostTime(): Unit = {
    for (costEN <- costMap.entrySet()) {
      val serName = costEN.getKey
      val costTime = costEN.getValue.intValue()
      val count = hostServiceMap.get(serName).intValue()
    //  if (count > 0) {
      val tmp = costTime*1.0 / count
      val bigD=new BigDecimal(tmp)
      val costAgv=bigD.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue()
   //   val costAgv = costTime*1.0 / count
   //     println(host+" makeAvgCostTime >>>>>>>>>>service= " + serName + " costAgv=" + costAgv)
        avgCostMap.put(serName, costAgv)
    //  }
    }
  }

  /** 检测平均耗时是否超过阀值
    * succeed return 0
    * failed return 1*/
  def checkCostTime(initAvgCostMap: util.HashMap[String, Double]): Boolean = {
    var isOk = true
    for (avgCostEN <- initAvgCostMap.entrySet()) {
      val serName = avgCostEN.getKey
      val initAvgCost = avgCostEN.getValue
      val avgcostTime = avgCostMap.get(serName)
      if (avgcostTime / initAvgCost >= ParamConstants.COST_TIME_MULTIPLE) {
        println(" checkAvgCostTime >>>>>>>>>>service= " + serName + " avgcostTime=" + avgcostTime+" initAvgCost="+initAvgCost )
        isOk = false
      }
    }
    isOk
  }

  def increaseWeight(): Integer = {
    var weight = 0
    for (en <- serPathMap.entrySet()) {
      weight = HostWeightUtil.increaseWeight(en.getValue)
      val key = en.getKey
      weightMap.put(key, weight)
    }
    weight
  }

  def recoverWeight(): Unit = {
    for (ser <- serPathMap.entrySet()) {
      HostWeightUtil.recoverWeight(ser.getValue)
      // HostWeightUtil.recoverWeight(ser.getValue,OriginweighMap.get(ser.getKey))
    }
  }

  def initWeight(treeSet: util.TreeSet[String]): Unit = {
    for (value <- treeSet) {
      val serviceName = getServiceName(value)
      serPathMap.put(serviceName, value)
      val weight = HostWeightUtil.getWeight(value)
      weightMap.put(serviceName, weight)
      originWeighMap.put(serviceName, weight)
    }
  }

  private def getServiceName(serpath: String): String = {
    serpath.split("/").apply(4)
  }

  def setFlag(f: Integer): Unit = {
    flag = f
  }

  def getFlag(): Integer = {
    flag
  }

  def getDesc():String={
    desc
  }


  def makeRateDescription(poolId:String,param:SparkPoolIdParam){
    desc="service："+failedMap.keySet()+"成功率下降超过"+param.getDownRate+
      "  "+makeCostDesc(poolId,param)
  }

  def makeCountDesc(){
    desc="修改权重值后tps不符合hedwig路由策略计算的值,造成该问题原因可能是修改zk上的数值未成功，请稍后再试！"
  }

  def makeCostDesc(poolId:String,param:SparkPoolIdParam){
    desc="成功率在可容忍下降的区间内，平均耗时超过未进行压测时的"+ParamConstants.COST_TIME_MULTIPLE+"倍" +
      "  "+makeDescription(poolId,param)
  }

  def makeDescription(poolId:String,param:SparkPoolIdParam): String ={
    val maxHostCount=param.getMaxHCalledCount
    //val hostServiceMap=statistics.getHostServiceMap()
    val hostSize=hostServiceMap.size()

    var hostAll=0
    for(entry<-hostServiceMap.entrySet()){
      hostAll+=entry.getValue.intValue()
    }
    hostAll=hostAll*60*hostSize
    var sc=""
    if(hostAll>=maxHostCount){
      val calledCount=hostAll/maxHostCount
      sc="减少"+calledCount
    }else{
      val calledCount=maxHostCount/hostAll
      sc="增加"+calledCount
    }
    // desc="service："+failedMap.keySet()+"成功率下降超过"+param.getSucceedRate
    val description="  所有与host:"+host +
      "节点相同配置调用量为"+hostAll+" 与近7天内最大调用量:"+maxHostCount+"比较后[ "+poolId+" ]需要"+sc+"台机器"
    return description
  }
}
