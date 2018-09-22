package com.yhd.arch.tuna.metric.entity

/**
  * Created by wangwei14 on 2016/9/20.
  */
trait KeyData extends Serializable {
  def  isValid(baseTime:Long ): Boolean;

  def  getTraceId():String;
  def getSpanId():String; ///

//  def  getTraceId():Option[String];
//  def getSpanId():Option[String]; ///

  def  getTimestamp():Long;
}
