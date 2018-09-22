package com.yhd.arch.tuna.metric.processor

import com.yhd.arch.tuna.metric.cal.TargetCalculator
import com.yhd.arch.tuna.metric.task.CallerTargetTask
import com.yihaodian.monitor.dto.ClientBizLog

import org.apache.spark.streaming.dstream.DStream
import java.util.Map

import org.slf4j.{LoggerFactory, Logger}

/**
  * Created by wangwei14 on 2016/9/20.
  */
class SpanProcesser extends Processer {
  val target = new TargetCalculator();

  val  logger : Logger = LoggerFactory.getLogger(classOf[SpanProcesser]);

  override def process(datas: DStream[(String, Map[String,ClientBizLog])] ): Unit = {
//    CallerTargetTask.traceAndSpanMappingPersist(datas);
//    CallerTargetTask.errorHandler(datas);

    val cts = CallerTargetTask.modelMapping(datas);
    cts.cache();
    target.process(cts);
  };
}


