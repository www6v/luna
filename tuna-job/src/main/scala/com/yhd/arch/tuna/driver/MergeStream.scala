package com.yhd.arch.tuna.driver

import com.yhd.arch.tuna.linktree.stream.JumperStream
import com.yhd.arch.tuna.processor.GlobalLinkProcessor
import com.yhd.arch.tuna.util.StreamingUtils

/**
  * 主程序入口类
  * Created by root on 9/18/16.
  */
object MergeStream {
  def main(args: Array[String]): Unit ={
    val ssc=StreamingUtils.createStreamingContext("Tuna_job")

    val Linkprocessor=new GlobalLinkProcessor

    JumperStream.startStream(ssc,Linkprocessor)
  }
}
