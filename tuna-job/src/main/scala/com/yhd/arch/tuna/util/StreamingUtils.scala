package com.yhd.arch.tuna.util

import java.util
import java.util.Date

import com.yhd.arch.streaming.{JumperReceiver, MessageProcessor, StreamingConstant}
import com.yhd.arch.tuna.linktree.util.Config
import com.yihaodian.architecture.jumper.common.message.Message
import com.yihaodian.monitor.dto.ClientBizLog
import com.yihaodian.monitor.util.MonitorConstants
import org.apache.commons.lang.StringUtils
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}
import com.yhd.arch.tuna.metric.util.Tags

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
/**
  * 工具类：
  * 1.生成streamingontext
  * 2.消费jumper消息作为输入流
  *
  * Created by root on 9/14/16.
  */
object StreamingUtils {
  val LOG: Logger = LoggerFactory.getLogger(StreamingUtils.getClass)
  def createStreamingContext(defaultName:String): StreamingContext ={
    val conf = new SparkConf()
    val map=Config.getSparkConfMap(ParamConstants.SPARK_PREFIX)

    if(StringUtils.isEmpty(map.get(ParamConstants.SPARK_APP_NAME))){
     map.put(ParamConstants.SPARK_APP_NAME,defaultName)
    }
    for(entry<-map.entrySet()){
      conf.set(entry.getKey,entry.getValue)
    }

    conf.registerKryoClasses(Array(classOf[ClientBizLog]))
 //   conf.registerKryoClasses(Array(classOf[Tags.TagTemplate]))
    val batchDuration=Config.getBatchDuration()

    if(batchDuration==0L) throw new IllegalArgumentException("batchDuration is null ")

    LOG.warn("batchDuration "+Seconds(batchDuration)+" "+batchDuration)
    val streamingContext=new StreamingContext(conf,Seconds(batchDuration))

    streamingContext
  }

  def createSOALogStream(ssc:StreamingContext,partition:Int,topicName:String,consumerName:String): DStream[List[ClientBizLog]]={

    val mp = new MessageProcessor[List[ClientBizLog]] {
      override def process(msg: Message): List[ClientBizLog] = {
        var bizCollect: List[ClientBizLog] = null

        val list: util.ArrayList[ClientBizLog] = msg.transferContentToBean(new util.ArrayList[ClientBizLog]().getClass)
        if (list != null && list.size() > 0) {
          val nowData:Long=new Date().getTime-ParamConstants.DEFAULT_USETIME
          bizCollect = list.asScala.filter(_.getLayerType==MonitorConstants.LAYER_TYPE_ENGINE).toList
          bizCollect=bizCollect.filter(p=>(p.getReqTime.getTime>=nowData&&p.getUniqReqId!=null))
    //      bizCollect= bizCollect.filter(_.getReqTime.getTime>=nowData)
          //限制日志量判断
//          if (ConsumerUtil.addAtomicInt(topicName)) {
//            try {
//              val sleepTime: Int = ConsumerUtil.getSleepTime
//              Thread.sleep(sleepTime)
//              if (Thread.currentThread.getName.endsWith("1")) {
//                ConsumerUtil.print(topicName, sleepTime)
//              }
//            }
//            catch {
//              case e: Exception => {
//                println(new Date(),e.getMessage)
//              }
//            }
//          }
        }
        return bizCollect
      }
    }

    val jumperParams = new util.HashMap[String,String]()
    jumperParams.put(StreamingConstant.PARALLEL_FACTOR,partition.toString)
    val map=jumperParams.asScala.toMap[String,String]
    val rc = new JumperReceiver(topicName,consumerName , mp, StorageLevel.MEMORY_ONLY,map)
    val msgStream = ssc.receiverStream(rc)
    return msgStream
  }
}
