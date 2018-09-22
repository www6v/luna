package com.yhd.arch.tuna.pressure.util

import com.yihaodian.architecture.hedwig.common.dto.ServiceProfile
import com.yihaodian.architecture.hedwig.common.util.ZkUtil
import org.slf4j.{Logger, LoggerFactory}

/**
 * Created by root on 3/16/16.
 */
object HostWeightUtil {
  private val LOG: Logger = LoggerFactory.getLogger(HostWeightUtil.getClass)
  def increaseWeight(serPath: String): Integer ={
    var weight=0
    try {
      if (ZkUtil.getZkClientInstance.exists(serPath)) {
        val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
        if(profile==null){
         weight=(-1)
        }else {
          weight = profile.getWeighted+1
          profile.setWeighted(weight)
          ZkUtil.getZkClientInstance.writeData(serPath, profile)
          println(weight+" writedata is successful!")
          LOG.warn("["+serPath+"]"+weight+"writedata is successful!")
        }
      }
    }catch{
      case ex:Exception=> {
        weight = 0
        LOG.error("increaseWeight failed ",ex)
      }
    }
    return weight
  }

  def increaseWeight(serPath: String,Oweight:Integer): Integer ={
    var weight=Oweight
    try {
      if (ZkUtil.getZkClientInstance.exists(serPath)) {
        val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
        if(profile==null){
          weight=(-1)
        }else {
         // weight = profile.getWeighted+1
          profile.setWeighted(Oweight)
          ZkUtil.getZkClientInstance.writeData(serPath, profile)
          println(weight+" writedata is successful!")
          LOG.warn("["+serPath+"]"+weight+"writedata is successful!")
        }
      }
    }catch{
      case ex:Exception=> {
        weight = 0
        LOG.error("increaseWeight failed ",ex)
      }
    }
    return weight
  }

  def decreaseWeight(serPath: String): Integer ={
    var weight=0
    try {
      if (ZkUtil.getZkClientInstance.exists(serPath)) {
        val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
        if(profile!=null) {
          weight = profile.getWeighted
          profile.setWeighted(weight - 1)
          ZkUtil.getZkClientInstance.writeData(serPath, profile)
          println("writedata is successful!")
        }
      }
    }catch{
      case ex:Exception=>weight=0

    }
    weight
  }

  def recoverWeight(serPath: String): Integer ={
    var weight=1
    try {
      val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
      val weight = profile.getWeighted
      profile.setWeighted(1)
      ZkUtil.getZkClientInstance.writeData(serPath, profile)
      println("1 writedata is successful!")
      LOG.warn("["+serPath+"] 1 recoverWeight is successful!")
    }catch{
      case ex:Exception=>{
        println("recoverWeight "+ex.getMessage)
        LOG.error("recoverWeight failed",ex)
        weight=0
      }
    }
    weight
  }

  def recoverWeight(serPath: String,we:Integer): Integer ={
    var weight=1
    try {
      val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
      val weight = profile.getWeighted
      profile.setWeighted(we)
      ZkUtil.getZkClientInstance.writeData(serPath, profile)
      println("1 writedata is successful!")
      LOG.warn("["+serPath+"]"+weight+"recoverWeight is successful!")
    }catch{
      case ex:Exception=>{
        println("recoverWeight "+ex.getMessage)
        LOG.error("recoverWeight failed",ex)
        weight=0
      }
    }
    weight
  }

  def getWeight(serPath: String): Integer ={
    try {
      val profile: ServiceProfile = ZkUtil.getZkClientInstance.readData(serPath)
      profile.getWeighted
    }catch{
      case ex:Exception=>println("")
        0
    }
  }
//  def getWeight(serviceProfile: ServiceProfile):Integer={
//    val weight=serviceProfile.getWeighted
//    println("weight="+weight)
//    serviceProfile.setWeighted(1)
//    ZkUtil.getZkClientInstance.writeData(path,serviceProfile)
//    return weight
//  }

}
