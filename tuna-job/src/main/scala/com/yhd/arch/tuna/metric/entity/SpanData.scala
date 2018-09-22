package com.yhd.arch.tuna.metric.entity

import com.alibaba.fastjson.JSONObject
import com.yhd.arch.tuna.metric.util.Constants

import scala.beans.BeanProperty

/**
  * Created by wangwei14 on 2016/9/20.
  */
class SpanData extends Span with KeyData with  Serializable{

  @BeanProperty var duration:Long = _;
  @BeanProperty var spanTimestamp: Long= _;
  @BeanProperty var callStatusCode:String= _;
  @BeanProperty var url:String= _;
  @BeanProperty var statement:String= _;
  @BeanProperty var optype:String= _;

  override def getSpanId() : String = { ///
    this.id
  }

  override def isValid(baseTime: Long): Boolean = {

    if (this.isExpired(baseTime)) {
       false;
    } else {
       true;
    }

//  init();
//  if (StringUtils.isEmpty(this.getApp()) || StringUtils.isEmpty(this.getId())
//    || StringUtils.isEmpty(this.getHost()) || StringUtils.isEmpty(this.getServiceId())
//    || StringUtils.isEmpty(this.getTraceId()) || CollectionUtils.isEmpty(this.getAnnotations())
//    || this.getSpanType() == null || this.getDuration() == -1 || this.getTimestamp() == 0
//    || this.isExpired(baseTime)) {
//    return false;
//  } else {
//    return true;
//  }

  };

  //
  private def  isExpired( baseTime:Long): Boolean = {
    val gap = baseTime - this.getTimestamp();
    if (gap > 0 && gap <= 60 * 1000) { // 时间要读配置
       false;
    } else {
       true;
    }
  }

  override def getTimestamp(): Long = { this.spanTimestamp };

  def  isCallSuccess() : Boolean = {
    return Constants.SUCCESS.equals(this.getCallStatusCode()) || Constants.TRUE.equals(this.getCallStatusCode());
  }

  override def  toString():String = {
    val json = new JSONObject();

    json.put("id", this.getId)
    json.put("parentId", this.getParentId)
    json.put("spanType", this.getSpanType)
    json.put("app", this.getApp)
    json.put("name", this.getSpanName)
    json.put("serviceId", this.getServiceId)
    json.put("duration", this.getDuration)
    json.put("callStatuscode", this.getCallStatusCode)
    json.put("timestamp", this.getTimestamp)
    return json.toString
  }
}
