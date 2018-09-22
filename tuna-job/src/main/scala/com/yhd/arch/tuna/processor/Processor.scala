package com.yhd.arch.tuna.processor

import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream

/**
  * DStream数据处理接口
  * Created by root on 9/13/16.
  */
trait Processor extends Serializable{

  def processor(dstream:DStream[ClientBizLog])
}
