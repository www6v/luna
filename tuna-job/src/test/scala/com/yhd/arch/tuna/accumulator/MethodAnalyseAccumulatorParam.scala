package com.yhd.arch.tuna.accumulator

import java.util

import com.yhd.arch.tuna.dto.MethodAnalyseAnnotation
import org.apache.spark.AccumulatorParam

/**
  * Created by root on 7/11/16.
  */
object MethodAnalyseAccumulatorParam extends AccumulatorParam[util.HashMap[Long, MethodAnalyseAnnotation]]{
   def zero(initialValue : util.HashMap[Long, MethodAnalyseAnnotation]) : util.HashMap[Long, MethodAnalyseAnnotation] = {
     new util.HashMap[Long, MethodAnalyseAnnotation]()
   }

   def addInPlace(map1 : util.HashMap[Long, MethodAnalyseAnnotation], map2 : util.HashMap[Long, MethodAnalyseAnnotation]) : util.HashMap[Long, MethodAnalyseAnnotation] = {
     val map = new util.HashMap[Long, MethodAnalyseAnnotation]
     if(map1 != null){
       map.putAll(map1)
     }
     if(map2 != null){
       map.putAll(map2)
     }
     map
   }
 }

