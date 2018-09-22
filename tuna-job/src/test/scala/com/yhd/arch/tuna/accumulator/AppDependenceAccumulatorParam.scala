package com.yhd.arch.tuna.accumulator

import java.util

import com.yhd.arch.tuna.dto.AppDependenceAnnotation
import org.apache.spark.AccumulatorParam

/**
  * Created by root on 7/11/16.
  */
object AppDependenceAccumulatorParam extends AccumulatorParam[util.HashMap[Long, AppDependenceAnnotation]]{
   def zero(initialValue : util.HashMap[Long, AppDependenceAnnotation]) : util.HashMap[Long, AppDependenceAnnotation] = {
     new util.HashMap[Long, AppDependenceAnnotation]()
   }

   def addInPlace(map1 : util.HashMap[Long, AppDependenceAnnotation], map2 : util.HashMap[Long, AppDependenceAnnotation]) : util.HashMap[Long, AppDependenceAnnotation] = {
     val map = new util.HashMap[Long, AppDependenceAnnotation]
     if(map1 != null){
       map.putAll(map1)
     }
     if(map2 != null){
       map.putAll(map2)
     }
     map
   }
 }

