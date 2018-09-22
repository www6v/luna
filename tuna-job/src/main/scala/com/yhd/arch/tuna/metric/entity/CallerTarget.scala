package com.yhd.arch.tuna.metric.entity

import com.alibaba.fastjson.JSONObject
import com.yhd.arch.tuna.metric.util.SpanUtil
import org.slf4j.{Logger, LoggerFactory}


/**
 * Created by wangwei14 on 2016/9/20.
 */
class CallerTarget extends KeyData with Serializable {

  val logger: Logger = LoggerFactory.getLogger(classOf[CallerTarget]);

  var caller: SpanData = _;
  var target: SpanData = _;
  var data: SpanData = _;

  def this(data: SpanData) {
    this;
    logger.info("CallerTarget construct. ")

    val spanType = data.getSpanType();
    logger.info("spanType: " + spanType)

    if (SpanUtil.CALLER.contains(spanType)) {
      this.caller = data;
    } else if (SpanUtil.TARGET.contains(spanType)) {
      this.target = data;
    } else {
      this.data = data;
    }

    //this.target = data; // mock up
  }

  def getEffectData(): SpanData = {
    //    caller != null ? caller : (target != null ? target : data);
    if (caller != null) {
      caller
    } else {
      if (target != null) {
        target
      }
      else {
        data
      }
    }
  }

  def addData(data: SpanData): Unit = {
    val type1 = data.getSpanType();
    logger.info("type1: " + type1)

    if (SpanUtil.CALLER.contains(type1)) {
      this.caller = data;
    } else if (SpanUtil.TARGET.contains(type1)) {
      this.target = data;
    } else {
      this.data = data;
    }
  }

  /////////////////////////////////////
  def getTarget(): SpanData = {
    return target;
  }

  def setTarget(target: SpanData) {
    this.target = target;
  }

  def getCaller(): SpanData = {
    caller;
  }

  def setCaller(caller: SpanData): Unit = {
    this.caller = caller;
  }

  /////////////////////////////////////
  override def getTimestamp(): Long = {
    0L // null
  }

  def isValid(baseTime: Long): Boolean = {
    false;
  }

  override def getTraceId(): String = {  ///
    if (this.target != null) {
      return this.getTarget().traceId
    }
    "0"
  }

  override def getSpanId(): String = {  ///
    if (this.target != null) {
      return this.getTarget().getSpanId();
    }
    "0"
  }

  override def toString(): String = {
    val json = new JSONObject();
    json.put("caller", caller);
    json.put("target", target);
    json.put("data", data);
    return json.toString();
  }
}
