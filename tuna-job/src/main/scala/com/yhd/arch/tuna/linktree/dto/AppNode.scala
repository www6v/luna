package com.yhd.arch.tuna.linktree.dto

import java.io.Serializable
import java.util
/**
  * Created by root on 9/13/16.
  */
class AppNode(var nodeInfo:NodeInfo,var parentName:String,var parentId:String,var spanId:String,var curtLayer:Int) extends Serializable{

  def this(nodeInfo:NodeInfo)={
    this(nodeInfo,null,null,null,0)
  }
  private var childrenList:util.List[AppNode]=new util.ArrayList[AppNode]()

  def setChildrenList(list:util.List[AppNode]): Unit ={
    childrenList=list
  }

  def getChildrenList():util.List[AppNode]={
    childrenList
  }

  def setNodeInfo(nodeInfo:NodeInfo): Unit ={
    this.nodeInfo=nodeInfo;
  }

  def  getNodeInfo(): NodeInfo ={
    nodeInfo
  }

  def setParentId(id:String): Unit ={
    this.parentId=id
  }

  def getParentId():String={
    parentId
  }

  def setParentName(parent:String): Unit ={

    this.parentName=parent
  }
  def getParentName():String={
    parentName
  }

  def setSpanId(span:String): Unit ={
    this.spanId=span
  }
  def getSpanId():String={
    spanId
  }

  def setCurtLayer(layer:Int): Unit ={
    this.curtLayer=layer
  }
  def getCurtLayer():Int={
    curtLayer
  }
}
