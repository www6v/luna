package com.yhd.arch.tuna.metric.test

import java.util.Date

import com.alibaba.fastjson.JSON
import com.yhd.arch.tuna.linktree.util.Config
import com.yihaodian.monitor.dto.ClientBizLog
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.slf4j.{Logger, LoggerFactory}

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
  * Created by wangwei14 on 9/5/16.
  */
object TextStreamStub {

  val hdfsFile = "hdfs://192.168.7.199:8020/home/wangwei14/tunaMockData/test.txt";

  private val topic_count=Config.getTopicCount()

  private val topic_multiple=Config.getTopicMultiple()
  val LOG: Logger = LoggerFactory.getLogger(TextStreamStub.getClass)

  def startStream(ssc:StreamingContext,processor: ProcessorStub) {
//    var k=0
//    println("topic_count",topic_count,"topic_multiple",topic_multiple)
//    val list = new Array[DStream[ClientBizLog]](topic_count*topic_multiple)
//    for(i<- 0 until topic_count){
//      for(j<-0 until topic_multiple){
//        var topic = ParamConstants.DEFAULT_TOPIC + "_" + i
//        println("topic",topic)
//        list(k)=StreamingUtils.createSOALogStream(ssc, 2, topic).flatMap(x=>x)
//        k=k+1
//      }
//    }
//    val mss = ssc.union(list)

//    val winStream=mss.window(Seconds(Config.getWindowsduration()),Seconds(Config.getSlideduration()))

    mockData(ssc);

    val  mockStream : DStream[String] = ssc.textFileStream(hdfsFile);

    processor.process(mockStream);

    ssc.start()
    ssc.awaitTermination()
  }

  def mockData(ssc:StreamingContext): Unit = {
    val clientBizLog:String = mockClientBizLog();
    createHDFSFile(clientBizLog);

//    ssc.textFileStream("hdfs://home/wangwei14/tunaMockData/test.txt")

//    val file = ssc.textFile("hdfs://xxx");
//    val writer = new PrintWriter(new File("/home/wangwei14/tunaMockData/test.txt"))

//    writer.write(clientBizLog)
//    writer.close()
  }

  def createHDFSFile(t:String): Unit = {
    val conf:Configuration=new Configuration();
    val hdfs:FileSystem=FileSystem.get(conf);
    val buff = t.getBytes();
    val dfs:Path=new Path(hdfsFile);
    val outputStream:FSDataOutputStream=hdfs.create(dfs);

    outputStream.write(buff,0,buff.length);
  }

  def mockClientBizLog() : String = {
    val cbl = new ClientBizLog();
    cbl.setCallApp("callApp1");  //
    cbl.setCallHost("10.10.10.10"); //
    cbl.setCallZone("callZone1")
    cbl.setCommId("CommId")
    cbl.setCostTime(10)  //
    cbl.setCurtLayer(1)
    cbl.setErrorType("")
    cbl.setExceptionClassname("")
    cbl.setExceptionDesc("")
    cbl.setExtInfo("")
    cbl.setGmtCreate(new Date())
    cbl.setId(111)
    cbl.setInParam("InParam")
    cbl.setInParamLength(10L)
    cbl.setLayerType(1)
    cbl.setMemo("")
    cbl.setMethodName("methodName1")  //
    cbl.setOutParam("outParam")
    cbl.setOutParamLength(10L)
    cbl.setOutParamObject(null)
    cbl.setPartition("")
    cbl.setProviderApp("providerApp")  //
    cbl.setProviderHost("10.10.10.10") //
    cbl.setProviderZone("providerZone")
    cbl.setReqId("reqId")
    cbl.setReqTime(new Date());  //
    cbl.setRespTime(new Date()); //
    cbl.setServiceGroup("serviceGroup")
    cbl.setServiceMethodName("serviceMethodName")  //
    cbl.setServiceName("serviceName") //
    cbl.setServicePath("servicePath")
    cbl.setServiceVersion("1")
    cbl.setSuccessed(1)
    cbl.setUniqReqId("123456")  //

    JSON.toJSONString(cbl, true)
  }
}
