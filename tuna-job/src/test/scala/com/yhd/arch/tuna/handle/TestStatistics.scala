package com.yhd.arch.tuna.handle

import java.util
/**
 * Created by root on 5/12/16.
 */
class TestStatistics extends Serializable{
  var total=0
  val map=new util.HashMap[String,Integer]()
  def setInitialized(size:Integer): Unit ={
    total=size
  }

  def add(v:TestStatistics): Unit ={
    total=total+v.total
  }
  def getTotal():Integer={
    total
  }
  def putAll(pool:String,size:Integer): Unit ={
    map.put(pool,size)
  }

}
