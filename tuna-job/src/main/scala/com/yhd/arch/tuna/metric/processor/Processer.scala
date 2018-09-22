package com.yhd.arch.tuna.metric.processor

import java.util

import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream

/**
  * Created by wangwei14 on 2016/9/20.
  */
trait Processer extends Serializable{
  def process(datas: DStream[(String,util.Map[String,ClientBizLog])] );
}
