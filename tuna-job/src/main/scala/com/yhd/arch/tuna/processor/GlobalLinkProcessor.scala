package com.yhd.arch.tuna.processor

import com.yhd.arch.tuna.linktree.processor.{DependencyPoolProcessor}
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream

/**
 * 全链路计算类
 * Created by root on 9/14/16.
 */
class GlobalLinkProcessor extends Processor {

  private val soaLogProcessor = new SOALogProcessor

  private val dependencyPoolProcessor=new DependencyPoolProcessor

  def processor(dStream: DStream[ClientBizLog]): Unit = {
  //  val linkIdStream = soaLogProcessor.processorByUniqReqid(dStream)

    val linkIdStream=soaLogProcessor.process(dStream)
    dependencyPoolProcessor.processor(linkIdStream)




  }

}
