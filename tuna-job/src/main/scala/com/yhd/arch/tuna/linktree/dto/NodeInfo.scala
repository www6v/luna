package com.yhd.arch.tuna.linktree.dto

/**
  * 节点信息
  * Created by  root on 10/14/16.
  */
class NodeInfo(poolId:String,
               methodName:String,
               serviceName:String,
               serviceGroups:String,
               serviceVersion:String) {

  def this(nodeName:String,
           methodName:String
          )={
    this(nodeName,methodName,null,null,null)
  }
  def getPoolId():String={
    poolId
  }


  def getServiceName():String={
    serviceName
  }

  def getServiceGroups():String={
    serviceGroups
  }

  def getServiceVersion():String={
    serviceVersion
  }
  def getMethodName():String={
    methodName
  }
}
