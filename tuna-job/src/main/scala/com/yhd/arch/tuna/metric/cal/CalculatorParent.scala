package com.yhd.arch.tuna.metric.cal

import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.metric.entity.{SpanData, StatisticsDataPoint, KeyData, CallerTarget}
import com.yhd.arch.tuna.metric.metrics.Metrics
import com.yhd.arch.tuna.metric.util.{Constants, Tags}
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory

import scala.collection.mutable.Seq


import scala.collection.mutable.{MutableList, HashSet}

/**
 * Created by root on 11/10/16.
 */
trait CalculatorParent {
  def getValue(t: CallerTarget): SpanData;

  private val logger = LoggerFactory.getLogger(classOf[CalculatorParent]);

  private def hashCodeMappingToCallerTarget(t: Iterator[Seq[(String, CallerTarget)]], tag: Tags.TagTemplate ): Iterator[(Long, CallerTarget)] = {
    val resultSet = HashSet[(Long, CallerTarget)]();

    while (t.hasNext) {
      t.next().foreach(i => {
        val ct = i._2
        val callerTarget = ct.asInstanceOf[KeyData];
        val hashCode = tag.hashCode(callerTarget);
        resultSet += ((hashCode, ct));
      })
    }

    resultSet.iterator
  }

  private def callFunc(v1: RDD[(Long, Iterable[CallerTarget])], tag: Tags.TagTemplate): Unit = {
    for (v <- v1) {
      val dps : MutableList[StatisticsDataPoint] = calcGroup(v, tag);

      persistentMetric(dps);
    }

    //    val metricInDuration : MutableList[StatisticsDataPoint] = MutableList();  // to optimized
    //    metricInDuration ++= dps;  // to optimized
  }

  private def calcGroup(v: (Long, Iterable[CallerTarget]), tag: Tags.TagTemplate): MutableList[StatisticsDataPoint] = {
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
      pipeline.hset(metricKey, sdp.metric, sdp.toString());  //  mset

      println( "persistentMetric() sdp: " + sdp )
      logger.info("metricKey: " + metricKey + "sdp.metric: " + sdp.metric)
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

    logger.info("saveToRedis", cost)
  }

}
