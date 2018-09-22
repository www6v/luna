package com.yhd.arch.tuna.metric.util

import com.yhd.arch.tuna.metric.entity.{CallerTarget, KeyData, SpanData}
import com.yhd.arch.tuna.metric.metrics.{AvgMaxMinMetrics, Count1Metrics, Metrics}

import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map};

/**
 * Created by wangwei14 on 2016/9/21.
 */
object Tags {

  /// 总调用次数 + 平均耗时
  class CountAndRTTag extends TagTemplate {

    private val category: String = "countAndRT";

    ///

    def getCategory: String = {
      return category
    }

    override def toString(): String = {
      this.category
    }

    override def hashCode(kd: KeyData): Long = {
      val ct: CallerTarget = kd.asInstanceOf[CallerTarget];
      val caller: SpanData = ct.getCaller();
      val target: SpanData = ct.getTarget();

      /// 要在方法上的聚集
      if (caller == null) {
        //        return HashCodeUtil.getHashCode(target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(target.getApp(), target.getServiceId());
      } else {
        //        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
        //          target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
          target.getApp(), target.getServiceId());
      }
    }


    override def getTags(kd: KeyData): Map[String, String] = {
      val ct = kd.asInstanceOf[CallerTarget];
      val tags = new mutable.HashMap[String, String];

      setCommonTage(kd, tags)

      tags += ((Constants.TAG_TARGET_APP, ct.getTarget().getApp()));
      tags += ((Constants.TAG_TARGET_API, ct.getTarget().getServiceId()));
      //      tags += ((Constants.TAG_TARGET_HOST, ct.getTarget().getHost()));  // remove

      if (ct.getCaller() != null) {
        tags += ((Constants.TAG_APP, ct.getCaller().getApp()));
        tags += ((Constants.NAME, ct.getCaller().getServiceId()));
      }

      return tags;
    }

    override def getMetrics[E <: KeyData](): List[Metrics[SpanData]] = {
      val count = new Count1Metrics()
      count.setName(Constants.STAT_SERVER_COUNT);
//      List(count);

      val avg = new AvgMaxMinMetrics();
      avg.setName(Constants.STAT_SERVER_RESPONSE_TIME_APP);
      List(count, avg);
    }
  }


  /// 总调用次数
  class CallerApiTargetHostApi extends TagTemplate {

    private val category: String = "count";

    ///

    def getCategory: String = {
      return category
    }

    override def toString(): String = {
      this.category
    }

    override def hashCode(kd: KeyData): Long = {
      // not correct
      //    val ct = kd.asInstanceOf[CallerTarget];
      //    return HashCodeUtil.getHashCode(ct.getTarget().getApp());

      val ct: CallerTarget = kd.asInstanceOf[CallerTarget];
      val caller: SpanData = ct.getCaller();
      val target: SpanData = ct.getTarget();

      /// 要在方法上的聚集
      if (caller == null) {
        //        return HashCodeUtil.getHashCode(target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(target.getApp(), target.getServiceId());
      } else {
        //        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
        //          target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
                                        target.getApp(), target.getServiceId());
      }
    }


    override def getTags(kd: KeyData): Map[String, String] = {
      val ct = kd.asInstanceOf[CallerTarget];
      val tags = new mutable.HashMap[String, String];

      setCommonTage(kd, tags)

      tags += ((Constants.TAG_TARGET_APP, ct.getTarget().getApp()));
      tags += ((Constants.TAG_TARGET_API, ct.getTarget().getServiceId()));
      //      tags += ((Constants.TAG_TARGET_HOST, ct.getTarget().getHost()));  // remove

      if (ct.getCaller() != null) {
        tags += ((Constants.TAG_APP, ct.getCaller().getApp()));
        tags += ((Constants.NAME, ct.getCaller().getServiceId()));
      }

      return tags;
    }

    override def getMetrics[E <: KeyData](): List[Metrics[SpanData]] = {
      val count = new Count1Metrics()
      count.setName(Constants.STAT_SERVER_COUNT);
      List(count);
    }
  }


  /// 平均耗时
  class Target extends TagTemplate {
    private val category: String = "avgRT";

    ///

    def getCategory: String = {
      return category
    }

    override def toString(): String = {
      this.category
    }

    override def getTags(kd: KeyData): Map[String, String] = {
//      val ct = kd.asInstanceOf[CallerTarget];
//      val tags = new HashMap[String, String];
//
//      setCommonTage(kd, tags)
//
//      tags.put(Constants.TAG_TARGET_APP, ct.getTarget().getApp());
//      tags


      val ct = kd.asInstanceOf[CallerTarget];
      val tags = new mutable.HashMap[String, String];

      setCommonTage(kd, tags)

      tags += ((Constants.TAG_TARGET_APP, ct.getTarget().getApp()));
      tags += ((Constants.TAG_TARGET_API, ct.getTarget().getServiceId()));
      //      tags += ((Constants.TAG_TARGET_HOST, ct.getTarget().getHost()));  // remove

      if (ct.getCaller() != null) {
        tags += ((Constants.TAG_APP, ct.getCaller().getApp()));
        tags += ((Constants.NAME, ct.getCaller().getServiceId()));
      }

      return tags;
    }

    @Override
    override def hashCode(kd: KeyData): Long = {
//      val ct: CallerTarget = kd.asInstanceOf[CallerTarget];
//      return HashCodeUtil.getHashCode(ct.getTarget().getApp());

      val ct: CallerTarget = kd.asInstanceOf[CallerTarget];
      val caller: SpanData = ct.getCaller();
      val target: SpanData = ct.getTarget();

      /// 要在方法上的聚集
      if (caller == null) {
        //        return HashCodeUtil.getHashCode(target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(target.getApp(), target.getServiceId());
      } else {
        //        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
        //          target.getApp(), target.getHost(), target.getServiceId());
        return HashCodeUtil.getHashCode(caller.getApp(), caller.getServiceId(),
          target.getApp(), target.getServiceId());
      }
    }


    override def getMetrics[E <: KeyData](): List[Metrics[SpanData]] = {
      val avg = new AvgMaxMinMetrics();
      avg.setName(Constants.STAT_SERVER_RESPONSE_TIME_APP);
      List(avg);
    }
  }


  abstract class TagTemplate extends Serializable {
    //    private var category: String = _;
    //
    //    private def this(category: String) {
    //      this()
    //      this.category = category
    //    }

    private val serialVersionUID = 1L;

    def getTags(kd: KeyData): Map[String, String];

    def hashCode(kd: KeyData): Long;

    //    def getMetrics[E <: KeyData]() : List[Metrics[E]]
    def getMetrics[E <: KeyData](): List[Metrics[SpanData]]

    def setCommonTage(kd: KeyData, tags: mutable.HashMap[String, String]): tags.type = {
      tags += ((Constants.TRACE_ID, kd.getTraceId()));
      tags += ((Constants.SPAN_ID, kd.getSpanId()));
    }


  }

}


