package com.yhd.arch.tuna.pressure.dto

/**
 * Created by root on 4/13/16.
 */
class ReportParam {

  private var host:String=""
  private var serviceName:String=""
  private var weight:Integer=0
  private var groupCount=0
  private var groupName=""
  private var succeedRate=0.0

  private var serviceCount=0
  private var hostCount=0
  def setGroupName(name:String): Unit ={
    groupName=name
  }
  def setHost(host: String): Unit ={
    this.host=host
  }
  def setServiceName(service:String): Unit ={
    serviceName=service
  }
  def setWeight(weight:Integer): Unit ={
    this.weight=weight
  }
  def setServiceCount(serviceCount:Integer): Unit ={
    this.serviceCount=serviceCount
  }
  def setHostCount(hostCount:Integer): Unit ={
    this.hostCount=hostCount
  }

  def setGroupCount(group:Integer): Unit ={
    groupCount=group
  }


  def setSucceedRate(rate:Double): Unit ={
    succeedRate=rate
  }

  def getSuccedRate(): Double ={
    succeedRate
  }
  def getServiceCount():Integer={
    serviceCount
  }
  def getHostCount():Integer={
    hostCount
  }
  def getHost():String={
    host
  }
  def getGroupName():String={
    groupName
  }
  def getServiceName():String= {
    serviceName
  }
  def getGroupCount():Integer={
    groupCount
  }
  def getWeight():Integer={
    weight
  }
}
