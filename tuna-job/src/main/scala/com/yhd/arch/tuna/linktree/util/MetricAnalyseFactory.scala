package com.yhd.arch.tuna.linktree.util

import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yihaodian.monitor.dto.ClientBizLog

/**
  * Created by root on 2/24/17.
  */
object MetricAnalyseFactory {

  /**
    * 获取spanId
    *
    * @param curtLog
    */
  def makeMetricAnalyse(curtLog:ClientBizLog): (String,MetricAnalyse) ={
    val curtLayer=curtLog.getCurtLayer
    val sbuilder=new StringBuilder
    var callApp = curtLog.getCallApp
    var providerApp = curtLog.getProviderApp
    val serviceMethod = curtLog.getServiceMethodName
    val serviceName = curtLog.getServiceName
    val serviceGroup = curtLog.getServiceGroup

    val serviceVersion=curtLog.getServiceVersion

    val callHost=curtLog.getCallHost
    val providerHost=curtLog.getProviderHost
    val costTime=curtLog.getCostTime
    var succeed=0
    if(curtLog.getSuccessed==null){
      succeed=0
    }else{
      succeed=curtLog.getSuccessed
    }
    if(callApp.contains("#")) {
      callApp=callApp.replaceAll("#","/")
    }
    if(providerApp.contains("#")) {
      providerApp=providerApp.replaceAll("#","/")
    }

    val metricAnalyse=new MetricAnalyse()
    metricAnalyse.setCallApp(callApp)
    metricAnalyse.setClientMethodName(curtLog.getMethodName)
    metricAnalyse.setProviderApp(providerApp)
    metricAnalyse.setCurtLayer(curtLayer)
    metricAnalyse.setServiceGroup(serviceGroup)
    metricAnalyse.setServiceMethodName(serviceMethod)
    metricAnalyse.setServiceName(serviceName)
    metricAnalyse.setServiceVersion(serviceVersion)
    metricAnalyse.setSucceed(succeed)
    metricAnalyse.setCostTime(costTime)
    metricAnalyse.setCallHost(callHost)
    metricAnalyse.setProviderHost(providerHost)
    metricAnalyse.execMerge(succeed,costTime)

    sbuilder.append(callApp).append(providerApp).append(serviceMethod).append(serviceName).append(serviceGroup).append(curtLayer)
    val spanId=SignIDUtils.createSpanId(sbuilder.toString())
    (spanId,metricAnalyse)

  }

}
