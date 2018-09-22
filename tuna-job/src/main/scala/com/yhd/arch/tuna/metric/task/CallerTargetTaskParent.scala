package com.yhd.arch.tuna.metric.task

import java.util.concurrent.atomic.AtomicInteger

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhd.arch.tuna.metric.entity.{CallerTarget, SpanData}
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.log4j.Logger
import org.apache.spark.streaming.dstream.DStream

import scala.util.Random

/**
 * Created by wangwei14 on 10/10/16.
 */
@deprecated
abstract class CallerTargetTaskParent {

  val radmon = new Random();
  val stamp = new AtomicInteger(0);
  val mapper = new ObjectMapper();
  private val logger: Logger = Logger.getLogger(classOf[CallerTargetTaskParent])


  def  call( datas:DStream[ClientBizLog] ) : DStream[(String, CallerTarget)] = {
    logger.info( "in call. " )

    val traceId = radmon.nextInt(); // for mock
    val spanId = radmon.nextInt(); // for mock
    stamp.incrementAndGet(); // for mock

    val spanDatasClent  = datas.map( t => bizLogToSpanClent(t, traceId.toString,spanId.toString) );  // adaptor
    val spanDatasServer  = datas.map( c => bizLogToSpanServer(c,traceId.toString,spanId.toString) );  // adaptor

    val traceDatas = spanDatasClent.union(spanDatasServer);

    val ctCollectionsSeq =  traceDatas.flatMap( span => allCallerTargetConllectionInDuration(span) );
    //    val ctCollectionsSeq =  spanDatasClent.flatMap( span => allCallerTargetConllectionInDuration(span) );
    val c = ctCollectionsSeq.reduceByKey( (v1,v2) => red(v1,v2)  );
    c.map(  m => ctMap(m) );
  }


  // 1.1
   def bizLogToSpanClent(ds:ClientBizLog,traceId:String, spanId: String) : SpanData = {
    logger.info("in bizLogToSpanClent");

    logger.info( "ClientBizLog : " + mapper.writeValueAsString(ds) + " stamp:" + stamp.toString);

    val span: SpanData = getClientSpan(ds, traceId, spanId)

    logger.info("client SpanData is : " + span + " stamp:" + stamp.toString)

    span
  }



  // 1.2
   def bizLogToSpanServer(ds:ClientBizLog,traceId:String, spanId: String) : SpanData = {
    logger.info("in bizLogToSpanServer");

    val span: SpanData = getServerSpan(ds, traceId, spanId)

    logger.info("server SpanData is : " + span + " stamp:" + stamp.toString)

    span
  }



  // 2
   def allCallerTargetConllectionInDuration( span:SpanData ): Seq[(String, CallerTarget)] = {
    //    val rets =  mutable.LinkedList[(String, CallerTarget)]();
    val calcTime = System.currentTimeMillis();
    val baseTime = calcTime / 60000 * 60000 - 60000;


    var rets: List[(String, CallerTarget)] = List()


    //    val jsons = JSON.parseArray(t);
    //    if(jsons != null){
    //      for(i <- 0 until jsons.size()){
    //        val span = jsons.getObject(i, classOf[SpanData]);
    var valid = span.isValid(baseTime);
    valid =true; // mock
    //logger.info("valid is" + valid);  // mock
    if(valid){

      //      val ct = new CallerTarget(span); // for test
      //      logger.info( "ct.getTarget() : " + ct.getTarget() );

      //      rets.+:( (span.getId() + span.getTraceId() , new CallerTarget(span) ) );
      rets =  ( span.getId()+span.getTraceId() , new CallerTarget(span) )::rets;
    }
    //      }
    //    }

    //logger.info("span.getId() + span.getTraceId() is : " + span.getId() + span.getTraceId());
    //logger.info( "rets.size : " + rets.size );

    rets;
  }

  // 3
   def red( v1: CallerTarget,  v2: CallerTarget) : CallerTarget  ={
    logger.info(" in red ")

    logger.info("first CallerTarget  is : " + v1 + " stamp:" + stamp.toString)
    logger.info("second CallerTarget  is : " + v2 + " stamp:" + stamp.toString)

    val span = v2.getEffectData();
    v1.addData(span);

    logger.info("CallerTarget  is : " + v1 + " stamp:" + stamp.toString)

    return v1;
  }

  // 4
   def ctMap(  t: (String, CallerTarget) ): (String, CallerTarget) = {
    logger.info( "in  ctMap" )
    val ct = t._2;
    logger.info( "ct: " + ct )
    logger.info( "ct.getEffectData() : " + ct.getEffectData()  );
    logger.info( "ct.getEffectData().getApp() : " + ct.getEffectData().getApp()  );

    (ct.getEffectData().getApp(), ct);
  }

  def getClientSpan(ds: ClientBizLog, traceId: String, spanId: String): SpanData;
  def getServerSpan(ds: ClientBizLog, traceId: String, spanId: String): SpanData;


  //  @deprecated
  //  def toPair( t:String ) : Seq[(String, CallerTarget)]  = {
  //    val rets =  mutable.LinkedList[(String, CallerTarget)]();
  //    val calcTime = System.currentTimeMillis();
  //    val baseTime = calcTime / 60000 * 60000 - 60000;
  //
  //    val jsons = JSON.parseArray(t);
  //    if(jsons != null){
  //      for(i <- 0 until jsons.size()){
  //          val span = jsons.getObject(i, classOf[SpanData]);
  //          val valid = span.isValid(baseTime);
  //          if(valid){
  //            rets.+:( (span.getId() + span.getTraceId() , new CallerTarget(span) ) );
  //          }
  //      }
  //    }
  //
  //    rets;
  //  }
}
