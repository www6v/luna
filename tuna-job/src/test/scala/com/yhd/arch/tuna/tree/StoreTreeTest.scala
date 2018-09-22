package com.yhd.arch.tuna.tree

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.JSON
import com.yhd.arch.tuna.linktree.dto.{AppNode, NodeInfo}
import com.yhd.arch.tuna.linktree.serialize.SerializerFactory
import com.yhd.arch.tuna.linktree.util.LinkTreeUtil
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.monitor.dto.ClientBizLog

import scala.collection.JavaConversions._

/**
  * Created by root on 9/13/16.
  */
object StoreTreeTest {
  def main(args: Array[String]) {
    testClientBizLog
  }
  def test(): Unit ={
    val list=new util.ArrayList[AppNode]()
    for(i<- 1 to 4){
      val nodeInfo=new NodeInfo("A_"+i,null)
      var parent:String=null
      if(i-1>0){
        parent="A_"+1
      }
      val appNode=new AppNode(nodeInfo,parent,null,null,0)
      list.add(appNode)
    }

    val storeTree=new LinkTreeUtil(list)
    storeTree.buildTree()
    val endNodes=storeTree.getTreeAppNodes()

    printTree(endNodes)
  }
  def printTree(endNodes:util.List[AppNode]): Unit ={
    for(node<-endNodes){
      println(node.getParentName()+">>"+node.getNodeInfo().getPoolId())
      val children=node.getChildrenList()
      if(!children.isEmpty){
        printTree(children)
      }
    }
  }

  def testClientBizLog(): Unit ={
    val nodeMap:util.Map[String,AppNode]=new util.HashMap[String,AppNode]()
    val extraList=new util.ArrayList[AppNode]()
    for(i<- 1 to 6) {
      val clientBizLog = new MetricAnalyse
      var callapp="yihaodian/callApp"
      var providerApp="yihaodian/ProviderA_"
      var layer=1
      if(i%2==0){
        callapp="yihaodian/ProviderA_"
        providerApp="yihaodian/B_"
        layer=2
      }

      clientBizLog.setCallApp(callapp)
      clientBizLog.setProviderApp(providerApp)
      clientBizLog.setServiceMethodName("ServiceMethodName_"+i)
      clientBizLog.setServiceGroup("group_"+i)
      clientBizLog.setServiceName("serviceName_"+i)
      clientBizLog.setCurtLayer(layer)
      extraList.addAll(LinkTreeUtil.createTreeNode(nodeMap,clientBizLog,"45645asdas5465asdasda_"+i))
    }

    val nodeLists: util.List[AppNode] = new util.ArrayList[AppNode]()
    nodeLists.addAll(nodeMap.values())
    nodeLists.addAll(extraList)
    println(nodeLists.size())
//    val storeTree=new LinkTreeUtil(nodeLists,2)
//    storeTree.createTree()
//    val appNode= storeTree.getTreeAppNode()
  //  val  child=storeTree.getChildren(appNode)
  //  println(appNode.getNodeInfo().getPoolId())
   // printTree(child)

    val jsonTree = LinkTreeUtil.createTree2JSON(nodeLists,2)
    println(jsonTree)


  }
}
