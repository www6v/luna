package com.yhd.arch.tuna.metric.task

import java.util

import com.alibaba.fastjson.JSON
import com.ycache.redis.clients.jedis.Response
import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.metric.entity.{StatisticsDataPoint, CallerTarget, SpanData}
import com.yhd.arch.tuna.metric.SpanType
import com.yhd.arch.tuna.metric.util.Constants
import com.yhd.arch.tuna.util.CommonUtils
import com.yihaodian.monitor.dto.ClientBizLog

import org.apache.spark.streaming.dstream.DStream
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.setAsJavaSet
import scala.collection.JavaConversions.asScalaSet
import scala.collection.mutable
import scala.collection.mutable.{Seq, MutableList}

//import scala.collection.mutable.HashMap

import scala.collection.mutable.Map;
import scala.collection.mutable.Set;
/**
 * Created by wangwei14 on 2016/9/19.
 */
object CallerTargetTask {

  val logger: Logger = LoggerFactory.getLogger(CallerTargetTask.getClass);

  def modelMapping(datas: DStream[(String, util.Map[String, ClientBizLog])]): DStream[Seq[(String, CallerTarget)]] = {
    datas.map(t => clientBizLogToCallerTarget(t));

    //    datas.mapPartitions( t => clientBizLogToCT(t) )  // to optimize
  }

  //  def clientBizLogToCT(trace: Iterator[(String, util.Map[String, ClientBizLog])] ):Iterator[(String, CallerTarget)]  = {
  //    null; //
  //  }

  @deprecated
  def traceAndSpanMappingPersist(datas: DStream[(String, util.Map[String, ClientBizLog])]): Unit = {
    datas.foreachRDD { rdd =>
      rdd.foreachPartition { partitionOfRecords =>
        traceAndSpanMapped(partitionOfRecords);
      }
    }
  }

  @deprecated
  private def traceAndSpanMapped(records: Iterator[(String, util.Map[String, ClientBizLog])]): Unit = {
    val start = System.currentTimeMillis()

    var spanToTraceMapping: Map[String, String] = Map();

    for (elem <- records) {
      val traceId: String = elem._1;
      val spanMapping = mapAsScalaMap(elem._2);

      for (mapping <- spanMapping) {
        val spanId: String = mapping._1

        spanToTraceMapping += (spanId -> traceId);
      }
    }

    logger.info(" spanToTraceMapping size:  " + spanToTraceMapping.size)

    val pipeline = RedisService.getRedisDao().buildPipelineUtils()
    spanToTraceMapping.foreach(elem => {
      val (spanId, traceId) = elem;
      pipeline.rpush(traceId, spanId);
      logger.info(traceId + ":" + spanId + " is persisted")
    });

    try {
      pipeline.sync()
    } catch {
      case ex: Exception => logger.error("saveToRedis pipeline sync is error", ex)
    }

    val cost = System.currentTimeMillis() - start
    logger.info("traceAndSpanMapping() saveToRedis", cost)
    logger.info("-----------")
  }


  def errorHandler(datas: DStream[(String, util.Map[String, ClientBizLog])]): Unit = {
    datas.foreachRDD { rdd =>
      rdd.foreachPartition { partitionOfRecords =>
        errorHandlerDetail(partitionOfRecords);
      }
    }
  }

  private def errorHandlerDetail(records: Iterator[(String, util.Map[String, ClientBizLog])]): Unit = {
    val pipeline = RedisService.getRedisDao().buildPipelineUtils()

    val traceAndSpanMap: Map[String, MutableList[String]] = Map();
    for (elem <- records) {
      val list: MutableList[String] = MutableList();

      val traceId: String = elem._1;
      val spanMapping = mapAsScalaMap(elem._2);


      for (mapping <- spanMapping) {
        val spanId: String = mapping._1
        list += spanId;
      }

      traceAndSpanMap.put(traceId, list);
      println("traceId:" + traceId);
      println("list contain span:" + list.size);
    }
    println("traceAndSpanMap:" + traceAndSpanMap.size);

    val result: mutable.Map[Response[String],Integer] = Map();
    for (traceAndMap <- traceAndSpanMap) {
      val traceId: String = traceAndMap._1;
      val spanList: MutableList[String] = traceAndMap._2;
      val spanIdSet = spanList.toSet

      var relateLink: Set[String] = Set();
      relateLink  ++= asScalaSet(CommonUtils.readRedis2Linkid(spanIdSet));

      spanIdSet.foreach(spanId => {
        val metricKey = traceId + ":"+ spanId;
        val errorCount = spanGetError(metricKey);
        if (errorCount>0) {
//          println("spanIdSet : " + spanIdSet.size );
//          println("CommonUtils.readRedis2Linkid : " + CommonUtils.readRedis2Linkid(spanIdSet).size() );
          println("relateLink:" + relateLink.size);
          // 相关的link加上错误

          // original
          val resp: Response[String] = pipeline.hget(metricKey, Constants.STAT_SERVER_COUNT);
          result += (resp->errorCount);

          println("result:" + result);
        }
      });
    }

    try {
      pipeline.sync()
    } catch {
      case ex: Exception => logger.error("saveToRedis pipeline sync is error", ex)
    }

    for( metric <- result ){
      val statisticsDataPoint: String = metric._1.get();
      val errorCount: Integer = metric._2;

//      val sdp: StatisticsDataPoint = JSON.parseObject( statisticsDataPoint, classOf[StatisticsDataPoint] );

      //sdp.value = sdp.value + errorCount; // error to persistent  // sdp is null

      println("statisticsDataPoint:" + statisticsDataPoint)
      //println("sdp.value:" + sdp.value);  // sdp is null
    }

    println("-------------------")
  }


  private def spanGetError(metricKey: String) : Integer = {
    6; // stub
  }

  private def spanHashError(metricKey: String) = {
    true; // stub
  }

  //  def errorLink(relateLinkMap: util.HashMap[String, String], spanIdSet: util.Set[String]) {
  //    val relateLinkSet: util.Set[String] = new util.HashSet[String]()
  //    relateLinkSet.addAll(CommonUtils.readRedis2Linkid(spanIdSet))
  //    for (relateLink <- relateLinkSet) {
  //      if (relateLinkMap.containsKey(relateLink)) {
  //        var errorCount = relateLinkMap.get(relateLink).toInt
  //        errorCount = errorCount + 1
  //        relateLinkMap.put(relateLink, errorCount + "")
  //      } else {
  //        relateLinkMap.put(relateLink, "1")
  //      }
  //    }
  //  }


  private def clientBizLogToCallerTarget(trace: (String, util.Map[String, ClientBizLog])): Seq[(String, CallerTarget)] = {
    val traceId: String = trace._1;
    val spanMapping = mapAsScalaMap(trace._2);

    var result: mutable.MutableList[(String, CallerTarget)] = MutableList();

    spanMapping.foreach(elem => {
      val (spanId, clientBizLog) = elem;
      val tuple = targetMapping(clientBizLog, traceId.toString, spanId);
      result += tuple
    });

    result
  }

  private def targetMapping(ds: ClientBizLog, traceId: String, spanId: String): (String, CallerTarget) = {
    val clientSpan: SpanData = getClientSpan(ds, traceId, spanId);
    val serverSpan: SpanData = getServerSpan(ds, traceId, spanId);

    val ct = new CallerTarget();
    ct.setCaller(clientSpan);
    ct.setTarget(serverSpan);

    //(clientSpan.getApp, ct); //  add traceId
    (traceId + ":" + spanId, ct);
  }

  private def getClientSpan(ds: ClientBizLog, traceId: String, spanId: String): SpanData = {
    val span = new SpanData();
    span.setTraceId(traceId); // link Id
    span.setSpanName(ds.getServiceName + ds.getMethodName); // ds.getServiceName
    span.setSfq(0F); // null
    span.setServiceId(ds.getServiceName + ds.getMethodName); // ds.getServiceName
    span.setSecLevel(null) // app relevant
    span.setPort(null);
    span.setParentId(ds.getUniqReqId) //
    span.setIsSample(false)
    span.setId(spanId);
    span.setHost(ds.getCallHost); /////
    span.setCompany(null)
    span.setApp(ds.getCallApp) /////
    span.setUrl(null)
    span.setStatement(null)

    span.setSpanTimestamp(ds.getReqTime.getTime); //   原来的值
    //    span.setSpanTimestamp(ds.getReqTime.getTime / 60000 * 60000); //   取整

    span.setOptype(null)
    span.setDuration(ds.getCostTime.toLong)

    if (ds.getSuccessed == -1) {
      // fail
      span.setCallStatusCode(Constants.FAIL)
    }
    if (ds.getSuccessed != -1) {
      // success
      span.setCallStatusCode(Constants.SUCCESS)
    }

    span.setSpanType(SpanType.HEDIG_CLIENT);

    span
  }

  private def getServerSpan(ds: ClientBizLog, traceId: String, spanId: String): SpanData = {
    val span = new SpanData();
    span.setTraceId(traceId); // link Id
    span.setSpanName(ds.getServiceName + ":" + ds.getServiceMethodName);
    span.setSfq(0F); // null
    span.setServiceId(ds.getServiceName + ":" + ds.getServiceMethodName);
    span.setSecLevel(null) // app relevant
    span.setPort(null);
    span.setParentId(ds.getUniqReqId) //
    span.setIsSample(false)
    span.setId(spanId);
    span.setHost(ds.getProviderHost); /////
    span.setCompany(null)
    span.setApp(ds.getProviderApp) /////
    span.setUrl(null)
    span.setStatement(null)

    span.setSpanTimestamp(ds.getReqTime.getTime); //   原来的值
    //    span.setSpanTimestamp(ds.getReqTime.getTime / 60000 * 60000)  //   取整

    span.setOptype(null)
    span.setDuration(ds.getCostTime.toLong)


    if (ds.getSuccessed == -1) {
      // fail
      span.setCallStatusCode(Constants.FAIL)
    }
    if (ds.getSuccessed != -1) {
      // success
      span.setCallStatusCode(Constants.SUCCESS)
    }

    span.setSpanType(SpanType.HEDIG_SERVER);
    span
  }
}



