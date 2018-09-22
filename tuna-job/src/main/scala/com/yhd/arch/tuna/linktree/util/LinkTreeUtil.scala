package com.yhd.arch.tuna.linktree.util

import java.io.ByteArrayOutputStream
import java.util

import com.alibaba.fastjson.{JSON, JSONObject}
import com.alibaba.fastjson.serializer.SerializerFeature
import com.google.gson.Gson
import com.yhd.arch.tuna.linktree.dto.{AppNode, NodeInfo}
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.monitor.dto.ClientBizLog

import scala.collection.JavaConversions._

/**
  * Created by root on 9/13/16.
  *
  * @param srcAppNodes 无关系节点集
  */
class LinkTreeUtil(var srcAppNodes:util.List[AppNode],var linkLayer:Integer) {
  def this(srcAppNodes:util.List[AppNode])={
    this(srcAppNodes,0)
  }

  /**
    * 节点按树结构存储*/
  private var treeAppNodes:util.List[AppNode]=new util.ArrayList[AppNode]()

  private var treeAppNode:AppNode=null

 // private var linkLayer:Integer=0

  def getSrcAppNodes(): util.List[AppNode] ={
    srcAppNodes
  }

  def setLinkLayer(linkLayer:Integer): Unit ={
    this.linkLayer=linkLayer
  }

  def getLinkLayer():Integer={
    return linkLayer
  }

  def setTreeAppNode(treeAppNode:AppNode): Unit ={
    this.treeAppNode=treeAppNode
  }

  def getTreeAppNode():AppNode={
    treeAppNode
  }

  def setTreeAppNodes(treeAppNode:util.List[AppNode]): Unit ={
    this.treeAppNodes=treeAppNode
  }

  def getTreeAppNodes():util.List[AppNode]={
    treeAppNodes
  }

  def createTree(): Unit ={
    for(i<-0 to linkLayer){
      for(parentNode<-srcAppNodes){
        val layer=parentNode.getCurtLayer()
        if(layer==i){
          val nodeInfo=parentNode.getNodeInfo()
          val poolId=nodeInfo.getPoolId()
          for(node<-srcAppNodes){
            val parentName=node.getParentName()
              if(parentName!=null)
                if(parentName.equals(poolId)) {
                  parentNode.getChildrenList().add(node)
                }
          }
        }
        if(parentNode.getParentName()==null){
          setTreeAppNode(parentNode)
        }
      }
    }
  }

  def buildTree(): Unit ={
    for(appNode<-srcAppNodes){
      if(appNode.getParentName()==null){
        var children=getChildren(appNode )
        appNode.setChildrenList(children)

        setTreeAppNode(appNode)
       // treeAppNodes.add(appNode)
      }
    }
  }

  def getChildren(appNode:AppNode):util.List[AppNode]={
    val childrenList=new util.ArrayList[AppNode]()
    for(node<-srcAppNodes){
      if(node.getParentName()!=null) {
        val nodeInfo=appNode.getNodeInfo()
        val poolId=nodeInfo.getPoolId()
        if (node.getParentName().equals(poolId)) {
          val children = getChildren(node)

          node.setChildrenList(children)
          childrenList.add(node)
        }
      }
    }
    childrenList
  }

}
object LinkTreeUtil{
  /**
    * 创建tree并转化为json格式
    *
    * */
  def createTreeToJSON(nodeLists:util.List[AppNode]): String ={
    val storeTree=new LinkTreeUtil(nodeLists)
    storeTree.buildTree()
    val appNode= storeTree.getTreeAppNode()

    val gson=new Gson()
//    val map=new util.HashMap[String,AppNode]()
//    map.put(ParamConstants.LINK_TREE_TOJSON,appNode)
    gson.toJson(appNode)
//    val jSONObject=new JSONObject()
//    jSONObject.put(ParamConstants.LINK_TREE_TOJSON,appNode)
//    jSONObject.toJSONString
  }

  def createTree2JSON(nodeLists:util.List[AppNode],layer:Integer): String ={
    val storeTree=new LinkTreeUtil(nodeLists,layer)
    storeTree.createTree()
    val appNode= storeTree.getTreeAppNode()
//    val gson=new Gson()
//    val map=new util.HashMap[String,AppNode]()
//    map.put(ParamConstants.LINK_TREE_TOJSON,appNode)
//    println(gson.toJson(map))
//
//    val jSONObject=new JSONObject()
//   // jSONObject.put(ParamConstants.LINK_TREE_TOJSON,appNode)
//    jSONObject.put(ParamConstants.LINK_TREE_TOJSON,appNode)
//       println( jSONObject.toJSONString)
    JSON.toJSONString(appNode,SerializerFeature.DisableCircularReferenceDetect)
  }


  def createTree2Byte(nodeLists:util.List[AppNode],layer:Integer): Array[Byte] ={
    val storeTree=new LinkTreeUtil(nodeLists,layer)
    storeTree.createTree()
    val appNode= storeTree.getTreeAppNode()
    KryoUtil.parseByte(appNode)
  }

  /**
    * 创建树节点
    * */
  def createTreeNode(nodeMap:util.Map[String,AppNode],curtLog:ClientBizLog,spanId:String):util.ArrayList[AppNode]={
    val callApp = curtLog.getCallApp
    val clientMethod=curtLog.getMethodName
    val providerApp = curtLog.getProviderApp
    val serviceMethod = curtLog.getServiceMethodName
    val serviceName = curtLog.getServiceName
    val serviceGroup = curtLog.getServiceGroup
    val serviceVersion = curtLog.getServiceVersion
    var curtLayer=curtLog.getCurtLayer

    val extraList=new util.ArrayList[AppNode]()
    //查看map中是否已存储节点信息,如果没有创建节点，有则建立其他节点与该节点的关系
    var callerAppNode:AppNode=null
    var targetAppNode:AppNode=null
    if (!nodeMap.containsKey(callApp)) {
      val nodeInfo=new NodeInfo(callApp,clientMethod)
      callerAppNode = new AppNode(nodeInfo)
      nodeMap.put(callApp,callerAppNode)
    }
    val parentName=callApp
    val nodeInfo2=new NodeInfo(providerApp,serviceMethod,serviceName,serviceGroup,serviceVersion)
    targetAppNode = new AppNode(nodeInfo2,parentName,""+(curtLayer-1),spanId,curtLayer)
    if (!nodeMap.containsKey(providerApp)) {
      nodeMap.put(providerApp,targetAppNode)
    }else{
      extraList.add(targetAppNode)
    }
    extraList
  }

  def createTreeNode(nodeMap:util.Map[String,AppNode], metricAnalyse: MetricAnalyse, spanId:String):util.Set[AppNode]={
    val callApp = metricAnalyse.getCallApp
    val clientmethod=metricAnalyse.getClientMethodName
    val providerApp = metricAnalyse.getProviderApp
    val serviceMethod = metricAnalyse.getServiceMethodName
    val serviceName = metricAnalyse.getServiceName
    val serviceGroup = metricAnalyse.getServiceGroup
    val serviceVersion = metricAnalyse.getServiceVersion
    val curtLayer=metricAnalyse.getCurtLayer

    val set:util.Set[AppNode]=new util.HashSet[AppNode]()
    //查看map中是否已存储节点信息,如果没有创建节点，有则建立其他节点与该节点的关系
    var callerAppNode:AppNode=null
    var targetAppNode:AppNode=null
    if (!nodeMap.containsKey(callApp)) {
      val nodeInfo=new NodeInfo(callApp,clientmethod)
      callerAppNode = new AppNode(nodeInfo)
      nodeMap.put(callApp,callerAppNode)
    }
    val parentName=callApp

    val nodeInfo2=new NodeInfo(providerApp,serviceMethod,serviceName,serviceGroup,serviceVersion)
    targetAppNode = new AppNode(nodeInfo2,parentName,""+(curtLayer-1),spanId,curtLayer)

    if(callApp.equals(providerApp)){
      set.add(targetAppNode)
      return set
    }
    val extraNode = nodeMap.get(providerApp)
    if (extraNode != null) {
      if (extraNode.getCurtLayer() == 0){
        nodeMap.put(providerApp,targetAppNode)
      }else{
        set.add(targetAppNode)
      }
    }else{
      nodeMap.put(providerApp,targetAppNode)
    }

    set
  }

  def parseString[T](infoObject:T): String ={
    JSON.toJSONString(infoObject,SerializerFeature.DisableCircularReferenceDetect)
  }


}
