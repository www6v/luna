package com.yhd.arch.tuna.util

import java.util.Date

import com.yihaodian.monitor.dto.ClientBizLog
import com.yihaodian.monitor.intelligent.statistics.Statistics
import com.yihaodian.monitor.util.MonitorJmsSendUtil
import org.apache.commons.httpclient.params.HttpMethodParams
import org.apache.commons.httpclient.{NameValuePair, HttpClient}
import org.apache.commons.httpclient.methods.PostMethod
import java.util
import org.apache.log4j.Logger
import scala.collection.JavaConversions._

/**
 * Created by root on 4/11/16.
 */
object HttpClientUtil {
 // private val logger: Logger = Logger.getLogger(classOf[HttpClientUtil])
 def remoteExcuter(url: String, params: util.HashMap[String, String]): String = {
   var resultstr: String = null
   val httpClient: HttpClient = new HttpClient
   val postMethod: PostMethod = new PostMethod(url)
   if (params != null && params.size > 0) {
     val data = new Array[NameValuePair](params.keySet.size)
     //  val it: Iterator[Map.Entry[String, String]] = params.entrySet.iterator
     var i: Int = 0
     for(entry<-params.entrySet()) {
       //  val entry = it.next
       val key = entry.getKey
       val value = entry.getValue
       data(i) = new NameValuePair(key.toString, value.toString)
       i += 1

     }
     postMethod.setRequestBody(data)
     postMethod.getParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8")
   }
   try {
     val statusCode: Int = httpClient.executeMethod(postMethod)
     resultstr = new String(postMethod.getResponseBodyAsString.getBytes, "UTF-8")
//     if (statusCode == 200) {
//       clientBizLog.setSuccessed(1)
//     }
//     else {
//       clientBizLog.setSuccessed(-1)
//       clientBizLog.setExceptionDesc(resultstr)
//     }
   }
   catch {
     case e: Exception => {
//       clientBizLog.setSuccessed(-1)
//       clientBizLog.setExceptionDesc(e.getStackTrace.toString)
       //    logger.error("request romote service failed. url:" + url + ",params:" + params.toString, e)
     }
   } finally {
     postMethod.releaseConnection
//     clientBizLog.setRespTime(new Date)
//     MonitorJmsSendUtil.asyncSendClientBizLog(clientBizLog)
   }
   return resultstr
 }
  def remoteExcuter(url: String, params: util.HashMap[String, String], clientBizLog: ClientBizLog): String = {
    var resultstr: String = null
    val httpClient: HttpClient = new HttpClient
    val postMethod: PostMethod = new PostMethod(url)
    if (params != null && params.size > 0) {
      val data = new Array[NameValuePair](params.keySet.size)
    //  val it: Iterator[Map.Entry[String, String]] = params.entrySet.iterator
      var i: Int = 0
      for(entry<-params.entrySet()) {
        //  val entry = it.next
          val key = entry.getKey
          val value = entry.getValue
          data(i) = new NameValuePair(key.toString, value.toString)
          i += 1

      }
      postMethod.setRequestBody(data)
      postMethod.getParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8")
    }
    try {
      val statusCode: Int = httpClient.executeMethod(postMethod)
      resultstr = new String(postMethod.getResponseBodyAsString.getBytes, "UTF-8")
      if (statusCode == 200) {
        clientBizLog.setSuccessed(1)
      }
      else {
        clientBizLog.setSuccessed(-1)
        clientBizLog.setExceptionDesc(resultstr)
      }
    }
    catch {
      case e: Exception => {
        clientBizLog.setSuccessed(-1)
        clientBizLog.setExceptionDesc(e.getStackTrace.toString)
    //    logger.error("request romote service failed. url:" + url + ",params:" + params.toString, e)
      }
    } finally {
      postMethod.releaseConnection
      clientBizLog.setRespTime(new Date)
      MonitorJmsSendUtil.asyncSendClientBizLog(clientBizLog)
    }
    return resultstr
  }

  def getItilClientBizLog: ClientBizLog = {
    val clientBizLog: ClientBizLog = new ClientBizLog
    clientBizLog.setCallApp("detector/monitor")
    clientBizLog.setCallHost(Statistics.getLocalIP)
    clientBizLog.setProviderApp("ITIL")
    clientBizLog.setLayerType(1)
    clientBizLog.setReqTime(new Date)
    clientBizLog.setProviderHost("oms.yihaodian.com.cn")
    clientBizLog.setServiceName("itilService")
    clientBizLog.setServiceMethodName("sendMonitorMessage")
    clientBizLog.setRespTime(new Date(System.currentTimeMillis + 30))
    clientBizLog.setCostTime(50)
    return clientBizLog
  }

}
