package com.yhd.arch.tuna.metric.metrics

import com.yhd.arch.tuna.metric.entity.KeyData
import com.yhd.arch.tuna.metric.metrics.Metrics

import scala.beans.BeanProperty

/**
  * Created by wangwei14 on 2016/9/22.
  */
abstract class AbstractMetrics[T <: KeyData]  extends Metrics[T] {

  @BeanProperty var name: String = _;

  def  getLong(traceId:String ):Long ={

        return traceId.toLong;
//      return 0L;
  }
}