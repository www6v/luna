package com.yhd.arch.tuna.linktree.dto

/**
  * Created by root on 10/26/16.
  */
class LinkInfo {

  private var linkId:String =""
  private var linkCounts:Long=0L

  private var errorCounts:Long=0L

  private var linkcurtLayer:Int=0

  def setLinkId(linkId:String): Unit ={
    this.linkId=linkId
  }

  def getLinkId():String={
    linkId
  }
  def setLinkCounts(linkCounts:Long): Unit ={
    this.linkCounts=linkCounts
  }

  def getLinkCounts():Long={
    linkCounts
  }

  def setErrorCounts(errorCounts:Long): Unit ={
    this.errorCounts=errorCounts
  }

  def getErrorCounts():Long={
    errorCounts
  }

  def setLinkcurtLayer(linkcurtLayer:Int): Unit ={
    this.linkcurtLayer=linkcurtLayer
  }

  def getLinkcurtLayer():Int={
    linkcurtLayer
  }

  override def toString():String={
    val string="linkId="+linkId+",linkCounts="+linkCounts+",errorCounts="+errorCounts+",linkcurtLayer="+linkcurtLayer
    string
  }

}
