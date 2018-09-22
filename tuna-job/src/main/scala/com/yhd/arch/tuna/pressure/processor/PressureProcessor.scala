package com.yhd.arch.tuna.pressure.processor

import java.util

import com.yhd.arch.tuna.pressure.accumulator.AppAccumulatorParam
import com.yhd.arch.tuna.pressure.dto.AppHostStatistics
import com.yhd.arch.tuna.pressure.handler.StreamReceiver
import com.yhd.arch.tuna.pressure.util.{ResultReportUtil, SparkHCUtil}
import com.yhd.arch.tuna.pressure.zk.{ClusterManager, PoolDataWatcher}
import com.yhd.arch.tuna.processor.Processor
import com.yhd.arch.tuna.pressure.zk.PoolDataWatcher
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.Accumulator
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

/**
  * Created by root on 9/18/16.
  */
object PressureProcessor extends Processor{
  var accumulators:Accumulator[util.ArrayList[AppHostStatistics]]=null
  def init(): Unit ={

      //every 2 minutes scaning the /TheStore node
      ClusterManager.run()
      //check out the spark job health
      SparkHCUtil.createTMP()
      //Storage mongo Job the capacity pressure test report
      ResultReportUtil.startJob()
  }
  def createAccumulator(ssc:StreamingContext)={
    accumulators = ssc.sparkContext.accumulator(new util.ArrayList[AppHostStatistics], "as")(AppAccumulatorParam)
  }

  def processor(dStream: DStream[ClientBizLog]): Unit ={
    pressureJob(dStream)
  }

  private[processor] def pressureJob(stream: DStream[ClientBizLog]): Unit = {
    val mapstream=stream.map{msg:ClientBizLog=>(msg.getProviderApp,msg)}
    val groupStream=mapstream.filter(filterMsg=>{
      pressurefilter(filterMsg._2)
    }).groupByKey()

    groupStream.foreachRDD(StreamReceiver.operationRDD)

  }
  private[processor] def pressurefilter=(msg:ClientBizLog)=>{
    val map=PoolDataWatcher.getPoolIdParamMap()
    var appCode=msg.getProviderApp
    appCode=appCode.replaceAll("#","/")
    if(map.containsKey(appCode)){
      true
    }else {
      false
    }
  }
}
