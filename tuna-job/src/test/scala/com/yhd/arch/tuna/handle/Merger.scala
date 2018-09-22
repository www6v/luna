package com.yhd.arch.tuna.handle

/**
 * Created by root on 3/10/16.
 */
import org.apache.spark.streaming.dstream.DStream

trait Merger[T] {

  def merge():DStream[T]
}