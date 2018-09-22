package com.yhd.arch.tuna.metric.test

import com.yhd.arch.tuna.util.StreamingUtils

/**
 * 主程序入口类
 * Created by root on 9/18/16.
 */
object MergeStreamStub {
  def main(args: Array[String]): Unit = {
    val ssc = StreamingUtils.createStreamingContext("Tuna_job")

    //    val Linkprocessor=new GlobalLinkProcessor
    //    JumperStream.startStream(ssc,Linkprocessor)

    val Linkprocessor = new ProcessorImplStub
    TextStreamStub.startStream(ssc, Linkprocessor)
  }
}
