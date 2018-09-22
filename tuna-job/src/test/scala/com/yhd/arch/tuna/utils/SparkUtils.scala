package com.yhd.arch.tuna.utils

import java.util.Map

import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}

import scala.collection.JavaConversions._

/**
 * Created by root on 3/10/16.
 */
object SparkUtils {
  def createStreamingContext(master:String,name:String,jarPath:String,batchDuration:Duration,sparkProp:Map[String,String],checkpointPath:String) : StreamingContext={
    if(master==null) throw new IllegalArgumentException("master is null")
    if(name==null) throw new IllegalArgumentException("name is null")
    if(jarPath==null) throw new IllegalArgumentException("jarPath is null")
    if(batchDuration==null) throw new IllegalArgumentException("batchDuration is null ")
    if(batchDuration==null || batchDuration<(Seconds(1))) throw new IllegalArgumentException("batchDuration must bigger than 1s ")
    val conf = new SparkConf().setMaster(master).setAppName(name)
    if(sparkProp!=null){
      for((k,v) <- sparkProp){
        conf.set(k,v)
      }
    }
    conf.setJars(Array(jarPath))
    val ssc = new StreamingContext(conf, batchDuration)
//    if(checkpointPath!=null){
//      ssc.checkpoint(checkpointPath)
//    }
    return ssc
  }

  def createStreamingContext(master:String,name:String,jarPath:String,batchDuration:Duration,sparkProp:Map[String,String]): StreamingContext={
    if(master==null) throw new IllegalArgumentException("master is null")

    if(name==null) throw new IllegalArgumentException("name is null")

    if(jarPath==null) throw new IllegalArgumentException("jarPath is null")

    if(batchDuration==null) throw new IllegalArgumentException("batchDuration is null ")

    if(batchDuration==null || batchDuration<(Seconds(1))) throw new IllegalArgumentException("batchDuration must bigger than 1s ")

    val conf = new SparkConf().setMaster(master).setAppName(name)

    if(sparkProp!=null){
      for((k,v) <- sparkProp){
        conf.set(k,v)
      }
    }

    conf.registerKryoClasses(Array(classOf[ClientBizLog]))

    conf.setJars(Array(jarPath))
    val ssc = new StreamingContext(conf, batchDuration)
    ssc
  }

}
