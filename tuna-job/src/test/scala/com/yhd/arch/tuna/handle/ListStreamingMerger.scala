package com.yhd.arch.tuna.handle

/**
 * Created by root on 3/10/16.
 */

//import org.apache.spark.Logging
import org.apache.spark.streaming.dstream.DStream

import scala.reflect.ClassTag

/**
 * Created by archer on 6/8/15.
 */
class ListStreamingMerger[T:ClassTag](val streamArray: Array[DStream[List[T]]]) extends Merger[T]  {
  override def merge(): DStream[T] = {
    var s: DStream[T] = null
    if (streamArray != null) {
      val len = streamArray.length
      if (len > 0) {
        val sam = streamArray.map(x=> x.flatMap(y => y))
        s = sam(0)
        for (i <- 1 until len) {
          s=s.union(sam(i))
        }
      }
    }
    return s
  }
}
