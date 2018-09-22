package com.yhd.arch.tuna.metric.cal

import com.yhd.arch.tuna.metric.entity.{CallerTarget, SpanData}
import com.yhd.arch.tuna.metric.util.Tags.{CallerApiTargetHostApi, Target, CountAndRTTag}
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.Seq;

/**
 * Created by wangwei14 on 2016/9/20.
 */
class TargetCalculator extends Calculator with Serializable {

  def process(cts: DStream[Seq[(String, CallerTarget)]]) {

    val targets = cts; //cts.filter( (a,b) => filterFunc( (a,b) ) )

//    targets.cache();

//    run(targets, new Target() ); // rt
//    run(targets, new CallerApiTargetHostApi); // count

    run( targets, new  CountAndRTTag ); // rt + count
  }

  def filterFunc(v1: String): Boolean = {
    true
  }

  def getValue(t: CallerTarget): SpanData = {
    t.getTarget();
  }
}
