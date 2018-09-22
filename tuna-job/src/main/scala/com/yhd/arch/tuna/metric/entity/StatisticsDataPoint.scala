package com.yhd.arch.tuna.metric.entity

import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.builder.{ToStringBuilder, ToStringStyle}

import scala.collection.mutable.Map;
import scala.collection.JavaConversions.mapAsJavaMap
/**
  * Created by wangwei14 on 2016/9/13.
  */
class StatisticsDataPoint( var metric:String,
                           var timestamp: Long,
                           var value: String,
                           var tags: Map[String,String],
                           var valueType: String
)  extends Serializable {
  def this(metric: String, timestamp: Long, value: Long, tags: Map[String, String]) {
     this( metric,timestamp, String.valueOf(value), tags,  "Long");
  }

  def this(metric:String, timestamp:Long, value:Float, tags:Map[String, String]) {
    this( metric,timestamp, String.valueOf(value), tags,  "Float");
  }

  def this(metric:String, timestamp:Long, value:Double, tags:Map[String, String]) {
    this( metric,timestamp, String.valueOf(value), tags,  "Double");
  }

  override  def  toString() : String= {
//    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
//      .append("metric", metric)
//      .append("timestamp", timestamp)
//      .append("value", value)
//      .append("tags", tags)
//      .append("valueType", valueType)
//      .toString();


    val json = new JSONObject();
    json.put("metric", metric);
    json.put("timestamp",timestamp );
    json.put("value",value );
    json.put("tags", mapAsJavaMap(tags) );
    json.put("valueType", valueType);
    return json.toString();
  }

}