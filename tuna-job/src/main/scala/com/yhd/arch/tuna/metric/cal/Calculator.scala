package com.yhd.arch.tuna.metric.cal


import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.metric.entity._
import com.yhd.arch.tuna.metric.metrics.Metrics
import com.yhd.arch.tuna.metric.util.{Constants, Tags}
import com.yhd.arch.tuna.util.CommonUtils

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.LoggerFactory

import scala.collection.mutable.{MutableList, HashSet}
import scala.collection.mutable.Seq;

/**
 * Created by wangwei14 on 2016/9/20
 */
trait Calculator extends Serializable {
  private val logger = LoggerFactory.getLogger(classOf[Calculator]);

  def getValue(t: CallerTarget): SpanData;

  def run(cts: DStream[Seq[(String, CallerTarget)]], tag: Tags.TagTemplate): Unit = {

    val datas = cts.mapPartitions(tt => mappingForCallerTarget(tt,tag ));
    datas.groupByKey().foreachRDD(v1 => callFunc(v1, tag));

//    val datas = cts.mapPartitions(tt => hashCodeMappingToCallerTarget(tt,tag ));  /// 应该是这样的
//    datas.groupByKey().foreachRDD(v1 => callFunc(v1, tag)); /// 应该是这样的
//    datas.reduceByKey().foreachRDD(v1 => callFunc(v1, tag));  // groupByKey to reduceByKey
  }

  private def mappingForCallerTarget(t: Iterator[Seq[(String, CallerTarget)]], tag: Tags.TagTemplate ): Iterator[(String, CallerTarget)] = {
    val resultSet = HashSet[(String, CallerTarget)]();

    while (t.hasNext) {
      t.next().foreach(i => {
        val metricKey = i._1
        val ct = i._2
//        val callerTarget = ct.asInstanceOf[KeyData];
//        val hashCode = tag.hashCode(callerTarget);
        resultSet += ((metricKey, ct));
      })
    }

    resultSet.iterator
  }

  private def callFunc(v1: RDD[(String, Iterable[CallerTarget])], tag: Tags.TagTemplate): Unit = {
    for (v <- v1) {
      val dps : MutableList[StatisticsDataPoint] = calcGroup(v, tag);

      persistentMetric(dps);
    }

    //    val metricInDuration : MutableList[StatisticsDataPoint] = MutableList();  // to optimized
    //    metricInDuration ++= dps;  // to optimized
  }

  private def calcGroup(v: (String, Iterable[CallerTarget]), tag: Tags.TagTemplate): MutableList[StatisticsDataPoint] = {
    var span: SpanData = null;
    var ct: CallerTarget = null;
    var size = 0;

    val metrics: List[Metrics[SpanData]] = tag.getMetrics();
    var key = v._1;
    val it = v._2.toIterator;

    while (it.hasNext) {
      ct = it.next();
      size = size + 1;
      span = getValue(ct);
      for (m <- metrics) {
        m.addNode(span);
      }
    }

    val dps: MutableList[StatisticsDataPoint] = MutableList();
    if (span != null && ct != null) {
      val baseTime = span.getTimestamp();
      val tags = tag.getTags(ct);

      for (m <- metrics) {
        dps ++= (m.getResults(baseTime, tags));
      }

      logger.info("------------------");
    }

    dps
  }


  def persistentMetric(list: Seq[StatisticsDataPoint]): Unit = {
    val start = System.currentTimeMillis()
    val pipeline = RedisService.getRedisDao().buildPipelineUtils()

    /////////// hash
    for (sdp <- list) {
      val traceId = sdp.tags.getOrElse(Constants.TRACE_ID, "0");
      val spanId = sdp.tags.getOrElse(Constants.SPAN_ID, "0");
      val metricKey: String = traceId + ":" + spanId;

      //          val timestamp: Long = sdp.timestamp;  // remove
      //          val statisticsDataPoint: String = parseToJson(sdp); // remove
      //          pipeline.hmset( metricKey, Map(timestamp.toString-> statisticsDataPoint) )//


      pipeline.hdel(metricKey, sdp.metric)  // delete first
      pipeline.hset(metricKey, sdp.metric, sdp.toString());  
      pipeline.expire(metricKey, 2*60);

   //   println( "persistentMetric() sdp: " + sdp )
    //  logger.info("metricKey: " + metricKey + "sdp.metric: " + sdp.metric)
    }

    //    /////////// zset
    //    var zsetMap: Map[String, java.lang.Double] = Map();
    //    for (sdp <- list) {
    //      val timestamp: Long = sdp.timestamp;
    //      val statisticsDataPoint: String = parseToJson(sdp);
    //      zsetMap += (statisticsDataPoint -> timestamp.toDouble)
    //    }
    //    val key: String = "StatisticsDataPoint";
    //    pipeline.zadd(key, zsetMap);


    /////////// zset  /// 应该采用的
    //    for (sdp <- list) {
    //      val traceId = sdp.tags.getOrElse(Constants.TRACE_ID, "0");
    //      val spanId = sdp.tags.getOrElse(Constants.SPAN_ID, "0");
    //      val metricKey: String = traceId + ":" + spanId;
    //      //      val metricKey: String = "span123" + ":" + traceId; /// mock up
    //
    //      val timestamp: Long = sdp.timestamp;
    //
    //      //  val statisticsDataPoint: String = parseToJson(sdp);
    //      pipeline.zadd(metricKey, timestamp, sdp.toString());
    //    }

    try {
      pipeline.sync()
    } catch {
      case ex: Exception => logger.error("saveToRedis pipeline sync is error", ex)
    }
    val cost = System.currentTimeMillis() - start

   // logger.info("saveToRedis", cost)
  }

  private def parseToJson(statisticsDataPoint: StatisticsDataPoint): String = {
    //    val jSONObject = new JSONObject()
    //    jSONObject.put("statisticsDataPoint", statisticsDataPoint)
    //    jSONObject.toJSONString

    //    val mapper = new ObjectMapper();
    //    val sdp = mapper.writeValueAsString(statisticsDataPoint) ;

    //    val sdp = Json.toJson(statisticsDataPoint);
    //    sdp.toString()
    null
  }

}
