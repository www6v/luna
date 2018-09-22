package com.yhd.arch.tuna.accumulator

import java.util

import com.yhd.arch.tuna.pressure.dto.AppHostStatistics
import org.apache.spark.AccumulatorParam
/**
 * Created by root on 5/11/16.
 */
object AppAccumulatorParam extends AccumulatorParam[util.ArrayList[AppHostStatistics]]{
  def zero(initialValue:util.ArrayList[AppHostStatistics]): util.ArrayList[AppHostStatistics] ={
    new util.ArrayList[AppHostStatistics]()
  }
  def addInPlace(t1:util.ArrayList[AppHostStatistics],t2:util.ArrayList[AppHostStatistics]):util.ArrayList[AppHostStatistics]={
    t1.addAll(t2)
    t1
  }
}
