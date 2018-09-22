package com.yhd.arch.tuna.metric.test

import org.apache.spark.streaming.dstream.DStream

/**
 * Created by wangwei14 on 9/13/16.
 */
trait ProcessorStub extends Serializable{

  def process(dstream:DStream[String])
}
