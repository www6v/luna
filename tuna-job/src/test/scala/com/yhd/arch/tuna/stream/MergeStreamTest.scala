package com.yhd.arch.tuna.stream

import java.util

import com.yhd.arch.tuna.dto.{AppDependenceAnnotation, MethodAnalyseAnnotation}
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.Accumulator
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import com.yhd.arch.tuna.accumulator._
import com.yhd.arch.tuna.pressure.accumulator
import com.yhd.arch.tuna.pressure.dto.AppHostStatistics
import com.yhd.arch.tuna.utils.SOALogUtil
/**
 * Created by root on 3/16/16.
 */
object MergeStreamTest {
  val count=6
  var CHECK_CLEAR_SIZE=2
  var CHECK_EXCEPTION_TIMES=2*4
  var SPARK_WINDOWDURATION:Integer=0

  var SPARK_SLIDEDURATION:Integer=0
  var SPARK_RECEIVER_COUNT=6
  var SPARK_RECEIVER_MULTIPLE=1

  var SPARK_BROADCAST:Broadcast[String]=null

  var accumulators:Accumulator[util.ArrayList[AppHostStatistics]]=null
  var callerProviderAccumulator : Accumulator[util.HashMap[String, util.List[String]]] = null
  var appDependenceAccumulator : Accumulator[util.HashMap[Long, AppDependenceAnnotation]] = null
  var methodAnalyseAccumulator : Accumulator[util.HashMap[Long, MethodAnalyseAnnotation]] = null

  def main(args: Array[String]): Unit ={
  //  init()
    createStream()
  }
//  def init(): Unit ={
//    //every 2 minutes scaning the /TheStore node
//    ClusterManager.run()
//    //check out the spark job health
//    SparkHCUtil.createTMP()
//    //Storage mongo Job the capacity pressure test report
//    ResultReportUtil.startJob()
//
//  }


  def createStream(): Unit ={
   val ssc = SOALogUtil.createStreamingContext("Tuna_Job")
    if(ssc!=null) {
      accumulators = ssc.sparkContext.accumulator(new util.ArrayList[AppHostStatistics], "as")(accumulator.AppAccumulatorParam)
      callerProviderAccumulator = ssc.sparkContext.accumulator(new util.HashMap[String, util.List[String]])(CallerProviderAccumulatorParam)
      appDependenceAccumulator = ssc.sparkContext.accumulator(new util.HashMap[Long, AppDependenceAnnotation])(AppDependenceAccumulatorParam)
      methodAnalyseAccumulator = ssc.sparkContext.accumulator(new util.HashMap[Long, MethodAnalyseAnnotation])(MethodAnalyseAccumulatorParam)

      val count = SPARK_RECEIVER_COUNT * SPARK_RECEIVER_MULTIPLE
      val arr = new Array[DStream[List[ClientBizLog]]](count)
      val list = new Array[DStream[ClientBizLog]](count)
      var topic = ""
      var k=0
      for(i<- 0 until SPARK_RECEIVER_COUNT){
        for(j<-0 until SPARK_RECEIVER_MULTIPLE){

          topic = ParamConstants.DEFAULT_TOPIC + "_" + i
          SOALogUtil.LOG.warn("topic "+topic)
          list(k)=SOALogUtil.createSOALogStream(ssc, 2, topic).flatMap(x=>x)
          k=k+1
        }
      }
      val mss = ssc.union(list)
      val winStream=mss.window(Seconds(MergeStreamTest.SPARK_WINDOWDURATION*1L)
        ,Seconds(MergeStreamTest.SPARK_SLIDEDURATION*1L))
     // val ms = new ListStreamingMerger[ClientBizLog](arr).merge()
      //对ms：DStream操作
     // DStreamProcess.process(winStream)
      ssc.start()
      ssc.awaitTermination()
    }else{
      SOALogUtil.LOG.warn("ssc is null!!!!")
    }
  }

}
