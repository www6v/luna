package com.yhd.arch.tuna.pressure.dto

import java.util

import com.google.code.morphia.annotations.{Entity, Id}
/**
 * Created by root on 5/27/16.
 */
@Entity(value="CapacityMSReport",noClassnameStored = true)
class CapacityMSReport {
  private val serialVersionUID=1L
  @Id
  private var Id:String=null

  private var poolId:String=null

  //flag the report is useful
  private var flag:Integer=0

  private var version=0L

  private var desc:String=null

  private var info:util.ArrayList[util.HashMap[String,Object]]=null

  private var measureHost: String = null

  def setId(id:String): Unit ={
    this.Id=id
  }

  def getId():String={
    Id
  }

  def setPoolId(poolId:String): Unit ={
    this.poolId=poolId
  }
  def getPoolId():String={
    poolId
  }

  def setFlag(flag:Integer){
    this.flag=flag
  }
  def getFlag():Integer={
    flag
  }

  def setVersion(version:Long): Unit ={
    this.version=version
  }

  def getVersion():Long={
    version
  }

  def setDesc(desc:String): Unit ={
    this.desc=desc
  }
  def getDesc():String={
    desc
  }

  def setMeasureHost(measureHost: String) {
    this.measureHost = measureHost
  }

  def getMeasureHost: String = {
    return measureHost
  }

  def setInfo(info:util.ArrayList[util.HashMap[String,Object]]): Unit ={
    this.info=info
  }
  def getInfo():util.ArrayList[util.HashMap[String,Object]]={
    return info
  }
}
