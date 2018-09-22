package com.yhd.arch.tuna.processor

import java.util

import com.yhd.arch.tuna.linktree.dto.BatchAnalyseDto
import com.yhd.arch.tuna.linktree.hanlder._
import com.yhd.arch.tuna.metric.statistics.MetricAnalyse
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.dstream.DStream


/**
  * Created by root on 10/18/16.
  */
class SOALogProcessor extends Serializable{

  def processorByUniqReqid(dStream: DStream[ClientBizLog]): DStream[(String,(util.Map[String,MetricAnalyse],util.Set[String],Int))] ={

    val mapStream=dStream.map{  curtLog =>
      curtLog.getUniqReqId->LinkStreamHandler.makeSpanLog(curtLog)
    }
    val reduceByUniqReqId=mapStream.reduceByKey((a,b)=>LinkStreamHandler.mergeSpan(a,b))

    val linkidStream=reduceByUniqReqId.map(tmpRdd=>LinkStreamHandler.createLinkId(tmpRdd._1,tmpRdd._2))

    val mergeStream= linkidStream.reduceByKey((linkRddA,linkRddB)=>LinkStreamHandler.mergeMetricAndUniqReqidByKey(linkRddA,linkRddB))

    mergeStream.foreachRDD(LinkDataHandler.handlerByPartitions)

    mergeStream
  }


  def process(dStream: DStream[ClientBizLog]): DStream[(String,BatchAnalyseDto)] ={
    val mapStream=dStream.map{  curtLog => curtLog.getUniqReqId->AnalyseLogHandler.makeSpan(curtLog)}

    val reduceByUniqReqId=mapStream.reduceByKey((a,b)=>AnalyseLogHandler.mergeSpans(a,b))

    val linkIdStream=reduceByUniqReqId.map(tmpRdd=>AnalyseLogHandler.createLinkId(tmpRdd._1,tmpRdd._2))

    val mergeStream= linkIdStream.reduceByKey((linkRddA,linkRddB)=>AnalyseLogHandler.addMetricByKey(linkRddA,linkRddB))

    mergeStream.foreachRDD(HostStreamHandler.handlerByPartition)
    mergeStream
  }

}
