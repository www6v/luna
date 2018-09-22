package com.yhd.arch.tuna.driver

import com.yhd.arch.tuna.linktree.stream.JumperStream
import com.yhd.arch.tuna.metric.processor.{MetricStream, SpanProcesser}
import com.yhd.arch.tuna.processor.GlobalLinkProcessor
import com.yhd.arch.tuna.util.StreamingUtils
import org.apache.spark.streaming.StreamingContext

@deprecated
object MetricMergeStream {
  def main(args: Array[String]): Unit ={
    val ssc=StreamingUtils.createStreamingContext("Tuna_Metric_Job")

    val sp =new SpanProcesser
    MetricStream.startStream(ssc, sp)

//    val Linkprocessor=new GlobalLinkProcessor
//    JumperStream.startStream(ssc,Linkprocessor)
  }
}
