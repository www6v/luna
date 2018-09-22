package com.yhd.arch.tuna.linktree.dto

import java.util

import com.google.code.morphia.annotations._
import org.bson.types.BSONTimestamp
/**
  * Created by root on 11/18/16.
  */
@Entity(value = "queryLinkInfoTreeParam",noClassnameStored = true,cap =new CappedAt(value=200000000))
@Indexes (Array (new Index (fields = Array ( new Field ("createTime") ) ) ) )
class LinkTreeParam {
  @Id
  var id:String=null
  def setId(Id:String): Unit ={
    this.id=Id
  }

  def getId():String={
    id
  }
  var appNodeList:util.List[AppNode]=new util.ArrayList[AppNode]()
  var linkInfo:LinkInfo=new LinkInfo()
  var createTime:Long=0L
  var linkId:String=""
  var ts:BSONTimestamp=null

  def setAppNodeList(appNodeList:util.List[AppNode]): Unit ={
    this.appNodeList=appNodeList
  }

  def getAppNodeList():util.List[AppNode]={
    appNodeList
  }

  def setLinkInfo(linkInfo:LinkInfo): Unit ={
    this.linkInfo=linkInfo
  }
  def getLinkInfo(): LinkInfo ={
    linkInfo
  }

  def setCreateTime(time:Long): Unit ={
    this.createTime=time
  }
  def getCreateTime():Long={
    createTime
  }
  def setLinkId(linkId:String): Unit ={
    this.linkId=linkId
  }

  def getLinkId():String={
    linkId
  }
}
