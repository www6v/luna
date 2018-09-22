package com.yhd.arch.tuna.metric.test

import com.alibaba.fastjson.JSON
import com.yhd.arch.tuna.metric.processor.{Processer, SpanProcesser}
import com.yhd.arch.tuna.processor.SOALogProcessor
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream

/**
 * Created by root on 11/3/16.
 */
class ProcessorImplStub extends ProcessorStub{

  override  def process(dstream:DStream[String]): Unit ={
    val bizLog = dstream.map( t=> textToObject(t) );
    processor(bizLog);
  }

  def textToObject(t:String) : ClientBizLog = {
    System.out.println("clientBizLog :" + t);
    JSON.parseObject(t,classOf[ClientBizLog]);
  }


  private val successLogProcessor = new SOALogProcessor

  private val spanProcesser: Processer = new SpanProcesser();

  def processor(dStream: DStream[ClientBizLog]): Unit = {

   // spanProcesser.process(linkIdStream);

//    linkDataHandle.run(linkIdStream)  // comment
  }
}
