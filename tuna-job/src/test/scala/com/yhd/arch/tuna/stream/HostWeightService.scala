package com.yhd.arch.tuna.stream

import com.yhd.arch.tuna.handle.ListStreamingMerger
import com.yhd.arch.tuna.utils.SOALogUtil
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

/**
 * Created by root on 3/18/16.
 */
object HostWeightService {
  val count=6
  def main(args: Array[String]): Unit ={
    val ssc = SOALogUtil.createStreamingContext("Hostweight")
    val arr = new Array[DStream[List[ClientBizLog]]](count)
    val defaultTopic = "clientqueue"
    var topic=defaultTopic
    for(i <- 0 until count) {
      if(i>0){
        topic = defaultTopic+"_"+(i-1)
      }
      arr(i) = SOALogUtil.createSOALogStream(ssc,2,topic)
    }
    val ms = new ListStreamingMerger[ClientBizLog](arr).merge()

    //可以对ms：DStream操作

    ssc.start()
    ssc.awaitTermination()
  }

  def process(stream: DStream[ClientBizLog]):Unit={
    val winStream=stream.window(Seconds(5),Seconds(1))

  }
}
