package com.yhd.arch.tuna.pressure.util

import java.util
import java.util.concurrent.ConcurrentHashMap

import com.alibaba.fastjson.JSONObject
import com.yhd.arch.tuna.pressure.dao.BasicMongoDAO
import com.yhd.arch.tuna.pressure.dto.{CapacityMSReport, ClientHostStatistics}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
 * Created by root on 5/17/16.
 */
object ResultReportUtil{
  private val handlerMap=new ConcurrentHashMap[String,util.HashMap[String,util.HashMap[String, ClientHostStatistics]]]()
  def putResultMap(pool:String,map:util.HashMap[String,util.HashMap[String, ClientHostStatistics]]): Unit ={
    handlerMap.put(pool,map)
  }
  val classResultReport=ResultReportUtil()
  def apply()={
    val resultRepportUtil=new ResultReportUtil

    resultRepportUtil
  }
  def startJob(): Unit ={
    classResultReport.startJob()
  }
  def stopJob(): Unit ={
    classResultReport.setStop()
  }
  def main(args: Array[String]) {
    ResultReportUtil.startJob()
  }
}
class ResultReportUtil {
  private val LOG: Logger = LoggerFactory.getLogger(ResultReportUtil.getClass)
  private var job = false

  def run(): Unit = {
    val t = new Thread(new Runnable {
      override def run(): Unit = {
        while (job) {
          handler
          // createReport()
          try {
            Thread.sleep(10)
          } catch {
            case ex: Exception => println(ex.getMessage)
          }
        }
      }
    })
    t.start()
  }

  def handler(): Unit = {
    val map = ResultReportUtil.handlerMap
    val iterator = map.keySet().iterator()
    if (iterator.hasNext) {
      println("***********************************")
      println("handler")
      println("***********************************")
      val poolId = iterator.next()
      val groupMap = map.get(poolId)
      val capacityMSReport=new CapacityMSReport

      val json=new JSONObject

      val listss = new util.ArrayList[util.HashMap[String, Object]]()
      var description:String=null
      var flag=0
      if(groupMap.size()==0){
        description="不符合压测条件,可能原因：1.发布服务只有一台服务器，无法进行压测；" +
          "2.随机选择压测的机器没有被调用，请在detector查看机器调用详情"
        flag=1
      }
      var measureHost=""
      for (en <- groupMap.entrySet()) {

        val groupName = en.getKey
        val hostMap = en.getValue

        for (entry <- hostMap.entrySet()) {
          val host = entry.getKey
          val appStatistic = entry.getValue
          flag=appStatistic.getFlag()
          if(flag!=0){
            description=appStatistic.getDesc()
          }
          measureHost=appStatistic.getOperationHost()
          val weight = appStatistic.getWeightMap()
          val groupCount:Integer = appStatistic.getGroupCount().intValue()
          val succeedRateMap = appStatistic.getSucceedRateMap()

          val serviceNameList = new util.ArrayList[String]()
          val succeedRateList = new util.ArrayList[Double]()
          val weightList = new util.ArrayList[Integer]()
          val costList = new util.ArrayList[Double]()

          serviceNameList.addAll(succeedRateMap.keySet())
          succeedRateList.addAll(succeedRateMap.values())
          weightList.addAll(weight.values())
          costList.addAll(appStatistic.getAvgCostTime().values())
          val paramMap = new util.HashMap[String, Object]()
          listss.add(paramMap)
          paramMap.put("host", host)
          paramMap.put("serviceName", serviceNameList)
          paramMap.put("succeedRate", succeedRateList)
          paramMap.put("weight", weightList)
          paramMap.put("costTime", costList)
          paramMap.put("groupName", groupName)
          paramMap.put("groupCount", groupCount)

        }

      }
      capacityMSReport.setDesc(description)
      capacityMSReport.setFlag(flag)
      capacityMSReport.setInfo(listss)
      capacityMSReport.setPoolId(poolId)
      capacityMSReport.setMeasureHost(measureHost)
      val version=AnalystUtils.getPoolParamMap.get(poolId).getVersion()
      capacityMSReport.setVersion(version)
      val obj=BasicMongoDAO.getBMongoDao().save(capacityMSReport)
      LOG.warn("mongo Save:"+obj)
      json.put("flag",flag)
      json.put("pool",poolId)
      json.put("desc",description)
      json.put("info",listss)
      LOG.warn("****************************************")
      LOG.warn("json: "+json)
      LOG.warn("capacityMSReport.version="+capacityMSReport.getVersion())
      LOG.warn("****************************************")
      iterator.remove()
    }
  }
  def makeRateDescription(poolId:String,statistics:ClientHostStatistics): String ={
    val param=AnalystUtils.getPoolParamMap.get(poolId)

    val max=param.getMaxHCalledCount
    val hostServiceMap=statistics.getHostServiceMap()
    val hostSize=hostServiceMap.size()

    var hostAll=0
    for(entry<-hostServiceMap.entrySet()){
      hostAll+=entry.getValue.intValue()
    }
    hostAll=hostAll*60*hostSize
    var sc=""
    if(hostAll>=max){
      val calledCount=hostAll/max
      sc="减少"+calledCount
    }else{
      val calledCount=max/hostAll
      sc="增加"+calledCount
    }

    //"service："+failedMap.keySet()+"成功率下降超过"+param.getSucceedRate+"，
    val desc="所有与host:"+statistics.getHost() +
      "节点相同配置调用量为"+hostAll+" 与最大调用量"+max+"比较后[ "+poolId+" ]需要"+sc+"台机器"
    desc
  }

  def makeCountDesc():String={
    val desc="修改权重值后tps不符合hedwig路由策略计算的值"
    desc
  }

  def makeCostDesc():String={
    val desc="成功率在可容忍下降的区间内，平均耗时不符合设置的阀值"
    desc
  }

  def startJob(): Unit = {
    job = true
    run()
  }

  def setStop(): Unit = {
    job = false
  }
}
