package com.yhd.arch.tuna.metric.metrics

import com.yhd.arch.tuna.metric.entity.{KeyData, SpanData, StatisticsDataPoint}

import scala.collection.mutable.{Map, MutableList}

trait Metrics[T <: KeyData] {

  def setName( name: String);

//  def addNode( t: T);
  def addNode( t: SpanData);

  def getResults(timestamp: Long ,  tags: Map[String, String]) : MutableList[StatisticsDataPoint];
}
