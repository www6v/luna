package com.yhd.arch.tuna.utils

import java.util
import java.util.Properties

import com.yhd.arch.streaming.{JumperReceiver, MessageProcessor, StreamingConstant}
import com.yhd.arch.tuna.linktree.util.ConfigLoader
import com.yhd.arch.tuna.stream.MergeStreamTest
import com.yhd.arch.tuna.util.{ConsumerUtil, ParamConstants}
import com.yihaodian.architecture.jumper.common.message.Message
import com.yihaodian.monitor.dto.ClientBizLog
import com.yihaodian.monitor.util.MonitorConstants
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
/**
 * Created by root on 7/30/16.
 */
object SOALogUtil {
  val LOG: Logger = LoggerFactory.getLogger(SOALogUtil.getClass)
  def createSOALogStream(ssc:StreamingContext,partition:Int,topicName:String): DStream[List[ClientBizLog]]={
    val mp = new MessageProcessor[List[ClientBizLog]] {
      override def process(msg: Message): List[ClientBizLog] = {
        var l: List[ClientBizLog] = null;
        val list: util.ArrayList[ClientBizLog] = msg.transferContentToBean(new util.ArrayList[ClientBizLog]().getClass)
        if (list != null && list.size() > 0) {
          l = list.asScala.filter(_.getLayerType==MonitorConstants.LAYER_TYPE_ENGINE).toList
          //限制日志量判断
          if (ConsumerUtil.addAtomicInt(topicName)) {
            try {
              val sleepTime: Int = ConsumerUtil.getSleepTime
              Thread.sleep(sleepTime)
              if (Thread.currentThread.getName.endsWith("1")) {
                ConsumerUtil.print(topicName, sleepTime)
              }
            }
            catch {
              case e: Exception => {
                println(e.getMessage)
              }
            }
          }
        }
        return l
      }
    }

    val jumperParams = new util.HashMap[String,String]()
    jumperParams.put(StreamingConstant.PARALLEL_FACTOR,partition.toString)
    val map=jumperParams.asScala.toMap[String,String]
    val rc = new JumperReceiver(topicName, "tunaConsumer", mp, StorageLevel.MEMORY_ONLY,map)
    val msgStream = ssc.receiverStream(rc)
    return msgStream
  }

  def createStreamingContext(name:String) : StreamingContext={
    val m = new util.HashMap[String,String]()
    val properties=ConfigLoader.getSparkENVProperties()
    var ssc:StreamingContext=null

    if(properties!=null&&properties.size()>0) {

      getYccConfigProperties(properties)
      m.put(ParamConstants.SPARK_MAX_CORES, properties.getProperty(ParamConstants.SPARK_MAX_CORES))
      m.put(ParamConstants.SPARK_EXECUTOR_CORES,properties.getProperty(ParamConstants.SPARK_EXECUTOR_CORES))
      m.put(ParamConstants.SPARK_EXECUTOR_MEMORY, properties.getProperty(ParamConstants.SPARK_EXECUTOR_MEMORY))
      m.put(ParamConstants.SPARK_STREAMING_BLOCKINTERVAL, properties.getProperty(ParamConstants.SPARK_STREAMING_BLOCKINTERVAL))
      m.put(ParamConstants.SPARK_EXECUTOR_EXTRAJAVAOPTIONS,properties.getProperty(ParamConstants.SPARK_EXECUTOR_EXTRAJAVAOPTIONS))
      val spark_serializer=properties.getProperty(ParamConstants.SPARK_SERIALIZER)
      if(spark_serializer!=null) {
        m.put(ParamConstants.SPARK_SERIALIZER, spark_serializer)
      }
      val spark_streaming_backpressure_enabled=properties.getProperty(ParamConstants.SPARK_STREAMING_BACKPRESSURE_ENABLED)
      if(spark_streaming_backpressure_enabled!=null){
        m.put(ParamConstants.SPARK_STREAMING_BACKPRESSURE_ENABLED, spark_streaming_backpressure_enabled)
      }

      val spark_streaming_receiver_maxRate=properties.getProperty(ParamConstants.SPARK_STREAMING_RECEIVER_MAXRATE)
      if(spark_streaming_receiver_maxRate!=null&&spark_streaming_receiver_maxRate!=0){
        m.put(ParamConstants.SPARK_STREAMING_RECEIVER_MAXRATE,spark_streaming_receiver_maxRate)
      }

      m.put(ParamConstants.SPARK_DEFAULT_PARALLELISM,properties.getProperty(ParamConstants.SPARK_DEFAULT_PARALLELISM))
    //  m.put(ParamConstants.SPARK_CLEANER_TTL,properties.getProperty(ParamConstants.SPARK_CLEANER_TTL))
      m.put(ParamConstants.SPARK_STREAMING_CONCURRENTJOBS,properties.getProperty(ParamConstants.SPARK_STREAMING_CONCURRENTJOBS))
    //  m.put(ParamConstants.SPARK_NETWORK_TIMEOUT,properties.getProperty(ParamConstants.SPARK_NETWORK_TIMEOUT))

     // m.put(ParamConstants.SPARK_FILES,ParamConstants.cluster_global_config_path)

    //  println(ParamConstants.SPARK_FILES,ParamConstants.cluster_global_config_path)
      val masterUrl=properties.getProperty(ParamConstants.SPARK_MASTERURL)
      ssc = SparkUtils.createStreamingContext(
        masterUrl,
        name,
        ParamConstants.SPARK_JAR_PATH,
        Seconds(1),
        m)
      ssc.remember(Seconds(1))
    }else{
      LOG.warn("getYCCProperties is null!!!!!")
    }
    return ssc
  }


  def getYccConfigProperties(properties:Properties): Unit ={
    MergeStreamTest.SPARK_RECEIVER_COUNT=Integer.parseInt(properties.getProperty(ParamConstants.JUMPER_TOPIC_COUNT))
    MergeStreamTest.SPARK_RECEIVER_MULTIPLE=Integer.parseInt(properties.getProperty(ParamConstants.JUMPER_TOPIC_MULTIPLE))
    MergeStreamTest.SPARK_WINDOWDURATION=Integer.parseInt(properties.getProperty(ParamConstants.WINDOW_DURATION))
    MergeStreamTest.SPARK_SLIDEDURATION=Integer.parseInt(properties.getProperty(ParamConstants.SLIDE_DURATION))

    MergeStreamTest.CHECK_CLEAR_SIZE=60/MergeStreamTest.SPARK_WINDOWDURATION
    MergeStreamTest.CHECK_EXCEPTION_TIMES= MergeStreamTest.CHECK_CLEAR_SIZE*3

    LOG.warn("MergeStream.SPARK_SLIDEDURATION="+MergeStreamTest.SPARK_SLIDEDURATION+
      ",MergeStream.SPARK_WINDOWDURATION="+MergeStreamTest.SPARK_WINDOWDURATION+",    " +
      "   MergeStream.CHECK_CLEAR_SIZE="+MergeStreamTest.CHECK_CLEAR_SIZE+
      ",MergeStream.CHECK_EXCEPTION_TIMES="+MergeStreamTest.CHECK_EXCEPTION_TIMES+", " +
      "  MergeStream.SPARK_RECEIVER_COUNT=" + MergeStreamTest.SPARK_RECEIVER_COUNT)

    println("MergeStream.SPARK_SLIDEDURATION,MergeStream.SPARK_WINDOWDURATION,    " +
      "   MergeStream.CHECK_CLEAR_SIZE,MergeStream.CHECK_EXCEPTION_TIMES,      " +
      "  MergeStream.SPARK_RECEIVER_COUNT" +
      "",MergeStreamTest.SPARK_SLIDEDURATION,MergeStreamTest.SPARK_WINDOWDURATION,
      MergeStreamTest.CHECK_CLEAR_SIZE,MergeStreamTest.CHECK_EXCEPTION_TIMES,
      MergeStreamTest.SPARK_RECEIVER_COUNT,MergeStreamTest.SPARK_RECEIVER_MULTIPLE)
  }
}

