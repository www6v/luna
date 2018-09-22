package com.yhd.arch.tuna.metric.metrics

import com.yhd.arch.tuna.dao.impl.RedisService
import com.yhd.arch.tuna.metric.entity.{SpanData, StatisticsDataPoint}
import com.yhd.arch.tuna.metric.util.Constants
import org.slf4j.LoggerFactory

import scala.collection.mutable.{HashMap, Map, MutableList}
import scala.collection.JavaConversions.mapAsJavaMap;

/**
 * Created by root on 10/12/16.
 */
class AvgMaxMinMetrics extends AbstractMetrics[SpanData] {

  private  val logger  = LoggerFactory.getLogger(classOf[Count1Metrics]);

  var totalTime: Double = _;
  var size: Integer = 0;
  var minData: Long = _;
  var maxData: Long = _;


  override def addNode(span: SpanData): Unit = {
    if (span != null) {
      var dur = span.getDuration();
      totalTime += dur;
      size +=  1;

      if (minData == 0 || minData > dur) {
        minData = dur;
      }
      if (maxData == 0 || maxData < dur) {
        maxData = dur;
      }
    }

//    logger.info( "totalTime : " + totalTime  );
//    logger.info( "minData : " + minData  );
//    logger.info( "maxData : " + maxData  );
  }


  override def getResults(timestamp: Long ,  tags: Map[String, String]) : MutableList[StatisticsDataPoint] = {
    val name = getName();
    val dps: MutableList[StatisticsDataPoint] = MutableList();

    // for avg
    var avgTags : Map[String,String] = Map();
    avgTags = avgTags ++  tags ;
    avgTags.put(Constants.TAG_RESPONSE_PERCENT, Constants.TAG_RESPONSE_PERCENT_AVG);
    dps += (new StatisticsDataPoint(name, timestamp, totalTime / size, avgTags));

    // for min  // 应该有最小值
//    var minTags : Map[String,String] = Map();
//    minTags = minTags ++  tags ;
//    minTags.put(Constants.TAG_RESPONSE_PERCENT, Constants.TAG_RESPONSE_MIN);
//    dps += (new StatisticsDataPoint(name, timestamp,  minData, minTags));

    // for max  // 应该有最大值
//    var maxTags : Map[String,String] = Map();
//    maxTags = maxTags ++  tags ;
//    maxTags.put(Constants.TAG_RESPONSE_PERCENT, Constants.TAG_RESPONSE_MAX);
//    dps += (new StatisticsDataPoint(name, timestamp,  maxData, maxTags));

//    logger.info( "dps : " + dps  );
    dps;
  }
}
