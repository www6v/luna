package com.yhd.arch.tuna.metric.metrics

import java.util.HashMap

import com.yhd.arch.tuna.metric.entity.{SpanData, StatisticsDataPoint}
import com.yhd.arch.tuna.metric.util.Constants
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable.{Map, MutableList}

/**
  * Created by wangwei14 on 2016/9/22.
  */
class Count1Metrics extends AbstractMetrics[SpanData] {

  private  val logger  = LoggerFactory.getLogger(classOf[Count1Metrics]);

  val   slowThreshold     = 1000; //Config.slowDuration();
  val   verySlowThreshold = 2000; //Config.verySlowDuration();

  // key:callStatuscode,
  // values:0->failNormalSize,1->failSlowSize,2->failVerySlowSize,3->successNormalSize,4->successSlowSize,5->successVerySlowSize
  var map = new HashMap[String, Array[Long]]();
  //  val map: Map[String, Array[Long]] = Map()

  override def  addNode( span: SpanData): Unit= {
    if (span != null) {
      val success = span.isCallSuccess();  logger.info( " success : "  + success );
      val code = span.getCallStatusCode(); logger.info( " code : "  + code );
      var vs = map.get(code);
      if (vs == null) {
        vs = Array( 0L, 0L, 0L, 0L, 0L, 0L );
        map.put(code, vs);
      }
      val dur = span.getDuration();  logger.info( " dur : "  + dur );
      if (dur >= verySlowThreshold) {
        if (success) {
          vs(5) = vs(5) + 1;
        } else {
          vs(2) = vs(2) + 1;
        }
      } else if (dur >= slowThreshold) {
        if (success) {
          vs(4) = vs(4) + 1;
        } else {
          vs(1) = vs(1) + 1;
        }
      } else {
        if (success) {
          vs(3) = vs(3) + 1;
        } else {
          vs(0) = vs(0) + 1;
        }
      }

//      logger.info( " map : "  + map );
//      logger.info( " vs : " + vs(0) + ":" + vs(1) + ":" + vs(2) + ":" + vs(3) + ":" + vs(4) + ":" + vs(5) );

//      println(" map : "  + map);
//      println(" vs : " + vs(0) + ":" + vs(1) + ":" + vs(2) + ":" + vs(3) + ":" + vs(4) + ":" + vs(5) );
    }
  }

//  val mapper = new ObjectMapper();

  override def getResults(timestamp: Long ,  tags: Map[String, String]) : MutableList[StatisticsDataPoint] = {
        val metricName = getName();
        val dps: MutableList[StatisticsDataPoint] = MutableList();
        for ( code <- map.keySet()) {
          getResult(code, metricName, timestamp, tags, dps);
        }

        logger.info( "dps : " + dps  );

        dps;
  }

  private def getResult( code:String,  metricName:String,  timestamp:Long, tags:Map[String, String] ,
     dps:MutableList[StatisticsDataPoint] ) {
    val vs = map.get(code);
    val failNormalSize = vs(0);
    val failSlowSize = vs(1);
    val failVerySlowSize = vs(2);
    val successNormalSize = vs(3);
    val successSlowSize = vs(4);
    val successVerySlowSize = vs(5);

    /////// fail /// 插入数据会冲突, 先注释掉
//    if (failSlowSize > 0) {
//      var failSlowTags : Map[String,String] = Map();
//      failSlowTags = failSlowTags ++  tags ;
//
//      failSlowTags.put(Constants.TAG_RESULT_CODE, code);
//      failSlowTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_SLOW);
//      dps += (new StatisticsDataPoint(metricName, timestamp, failSlowSize, failSlowTags));
//    }
//    if (failVerySlowSize > 0) {
//      var failVerySlowTags : Map[String,String] = Map();
//      failVerySlowTags = failVerySlowTags ++ tags;
//
//      failVerySlowTags.put(Constants.TAG_RESULT_CODE, code);
//      failVerySlowTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_VERY_SLOW);
//      dps += (new StatisticsDataPoint(metricName, timestamp, failVerySlowSize, failVerySlowTags));
//    }
    if (failNormalSize > 0) {
      var failNormalTags : Map[String,String] = Map();
      failNormalTags = failNormalTags ++ tags;

      failNormalTags.put(Constants.TAG_RESULT_CODE, code);
      failNormalTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_NOMAL);
      dps += (new StatisticsDataPoint(metricName, timestamp, failNormalSize, failNormalTags));
    }


    ////////  success  /// 插入数据会冲突, 先注释掉
//    if (successSlowSize > 0) {
//      var successSlowTags : Map[String,String] = Map();
//      successSlowTags = successSlowTags ++  tags ;
//
//      successSlowTags.put(Constants.TAG_RESULT_CODE, code);
//      successSlowTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_SLOW);
//
//      dps += (new StatisticsDataPoint(metricName, timestamp, successSlowSize, successSlowTags));
//    }
//    if (successVerySlowSize > 0) {
//      var successVerySlowTags : Map[String,String] = Map();
//      successVerySlowTags =  successVerySlowTags ++  tags;
//
//      successVerySlowTags.put(Constants.TAG_RESULT_CODE, code);
//      successVerySlowTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_VERY_SLOW);
//
//      dps += new StatisticsDataPoint(metricName, timestamp, successVerySlowSize, successVerySlowTags);
//    }
    if (successNormalSize > 0) {
      var successNormalTags : Map[String,String]= Map();
      successNormalTags = successNormalTags ++ tags;

      successNormalTags.put(Constants.TAG_RESULT_CODE, code);
      successNormalTags.put(Constants.TAG_RESULT_SPEED, Constants.TAG_RESULT_SPEED_VALUE_NOMAL);

      dps += new StatisticsDataPoint(metricName, timestamp, successNormalSize, successNormalTags);
    }
  }

}
