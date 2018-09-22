package com.yhd.arch.tuna.metric.processor

import com.yhd.arch.tuna.linktree.util.Config
import com.yhd.arch.tuna.util.{ParamConstants, StreamingUtils}
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}

@deprecated
object MetricStream {

  private val topic_count=Config.getTopicCount()

  private val topic_multiple=Config.getTopicMultiple()

  val LOG : Logger = LoggerFactory.getLogger(MetricStream.getClass)

  def startStream(ssc:StreamingContext,processor: Processer) {
    var k=0
    println("topic_count",topic_count,"topic_multiple",topic_multiple)
    val list = new Array[DStream[ClientBizLog]](topic_count*topic_multiple)
    for(i<- 0 until topic_count){
      for(j<-0 until topic_multiple){
        var topic = ParamConstants.DEFAULT_TOPIC + "_" + i
        println("topic",topic)
        list(k)=StreamingUtils.createSOALogStream(ssc, 2, topic,"tunaName").flatMap(x=>x)
        k=k+1
      }
    }
    val mss = ssc.union(list)

//    mss.foreachRDD( t=> println(" msg from jumper " + t.toString()) ); //
    print("Config.getWindowsduration():" + Config.getWindowsduration())
    print("Config.getSlideduration():" + Config.getSlideduration())

    val winStream=mss.window(Seconds(Config.getWindowsduration()),Seconds(Config.getSlideduration()))

//    processor.processForMetric(winStream);

    ssc.start()
    ssc.awaitTermination()
  }
}
