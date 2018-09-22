package com.yhd.arch.tuna.pressure.dto

import java.util

import com.google.code.morphia.annotations.{Entity, Id}
/**
 * Created by root on 5/26/16.
 */
@Entity(value="PressureReport",noClassnameStored = true)
class ReportInstance extends Serializable{
  @Id
  private var Id:String=null

  private var poolId:String=null

  private var version:Integer=null

  private var info:util.ArrayList[util.HashMap[String,Object]]=null
  def setId(id:String): Unit ={
    this.Id=id
  }

  def getId():String={
    Id
  }

  def setVersion(version:Integer): Unit ={
    this.version=version
  }
  def getVersion():Integer={
    version
  }

  def setPoolId(poolId:String): Unit ={
    this.poolId=poolId
  }
  def getPoolId():String={
    poolId
  }

  def setInfo(info:util.ArrayList[util.HashMap[String,Object]]): Unit ={
    this.info=info
  }
  def getInfo():util.ArrayList[util.HashMap[String,Object]]={
    return info
  }
}
