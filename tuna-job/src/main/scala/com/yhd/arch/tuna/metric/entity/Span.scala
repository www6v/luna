package com.yhd.arch.tuna.metric.entity

import com.yhd.arch.tuna.metric.SpanType

import scala.beans.BeanProperty

/**
  * Created by wangwei14 on 2016/9/21.
  */
class Span extends Serializable {
  @BeanProperty var  traceId : String= _;
  @BeanProperty var  id: String= _;
  @BeanProperty var  parentId:String= _;
  @BeanProperty var  spanName:String= _;
  @BeanProperty var  serviceId:String= _;
  @BeanProperty var  isSample:Boolean= _;
  @BeanProperty var  spanType: SpanType= _;
  @BeanProperty var  company: String = _;
  @BeanProperty var  secLevel:String= _;
  @BeanProperty var  app:String= _;
  @BeanProperty var  host: String= _;
  @BeanProperty var  port:String= _;
  @BeanProperty var  sfq:Float= _; // sample frequency

  @BeanProperty var annotations: List[Annotation] = _ ;
  @BeanProperty var binaryAnnotations:List[BinaryAnnotation] = _ ;
  @BeanProperty var metricAnnotations:List[MetricAnnotation] = _ ;
}
