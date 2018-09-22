package com.yhd.arch.tuna.pressure.dto

/**
 * Created by root on 4/19/16.
 */

import java.io.Serializable
import java.util
class ResultParam extends Serializable{
  private var host:String=""
  private var serviceName=new util.ArrayList[String]()
  private var weight=new util.ArrayList[Integer]()
  private var succeedRate=new util.ArrayList[Double]()
  private var groupCount=0

  private var groupName=""

  def setGroupName(name:String): Unit ={
    groupName=name
  }
  def getGroupName():String={
    groupName
  }
  private var costMap=new util.ArrayList[Integer]()
  def setHost(host: String): Unit ={
    this.host=host
  }
  def getHost():String={
    host
  }

  def setGroupCount(groupCount:Integer): Unit ={
    this.groupCount=groupCount
  }

  def getGroupCount(): Integer ={
    groupCount
  }

  def setCostTime(cost:util.ArrayList[Integer]): Unit ={
    costMap=cost
  }
  def getCostTime():util.ArrayList[Integer]={
    costMap
  }

  def setServiceName(serviceList:util.ArrayList[String]): Unit ={
    serviceName=serviceList
  }
  def getServiceName():util.ArrayList[String]={
    serviceName
  }

  def setWeight(weightList:util.ArrayList[Integer]): Unit ={
    weight=weightList
  }

  def getWeight():util.ArrayList[Integer]={
    weight
  }

  def setSucceedRate(succeedList:util.ArrayList[Double]): Unit ={
    succeedRate=succeedList
  }

  def getSucceedRate():util.ArrayList[Double]={
    succeedRate
  }

}
