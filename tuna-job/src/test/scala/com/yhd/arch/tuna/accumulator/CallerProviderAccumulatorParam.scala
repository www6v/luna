package com.yhd.arch.tuna.accumulator

import java.util

import org.apache.spark.AccumulatorParam

/**
 * Created by root on 7/11/16.
 */
object CallerProviderAccumulatorParam extends AccumulatorParam[util.HashMap[String, util.List[String]]]{
  def zero(initialValue : util.HashMap[String, util.List[String]]) : util.HashMap[String, util.List[String]] = {
    new util.HashMap[String, util.List[String]]()
  }

  def addInPlace(map1 : util.HashMap[String, util.List[String]], map2 : util.HashMap[String, util.List[String]]) : util.HashMap[String, util.List[String]] = {
    val map = new util.HashMap[String, util.List[String]]
    if(map1 != null){
      map.putAll(map1)
    }
    if(map2 != null){
      map.putAll(map2)
    }
    map
  }
}

