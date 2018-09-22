package com.yhd.arch.tuna.util

import java.util.Date

import com.alibaba.fastjson.JSONObject


import java.util
import ConsumerUtil._
import com.yihaodian.architecture.hedwig.common.util.ZkUtil
import com.yihaodian.monitor.util.{MonitorConstants, MonitorConfigCenterUtil}
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

import com.yihaodian.util.{ZKService, ParamObject}
import com.yihaodian.architecture.zkclient.IZkDataListener

object ConsumerUtil{

	private val env: String = MonitorConfigCenterUtil.getProperty("env")
	private val concurrentInteger: ConcurrentHashMap[String, AtomicInteger] = new ConcurrentHashMap[String, AtomicInteger]
	val consumerUtil=ConsumerUtil()
	val url: String = "http://oms.yihaodian.com.cn/itil/api/sendWarning"

	/** 打印log日志和发送预警邮件 */
	def print(topicName: String, sleepTime: Int) {
		val sb: StringBuilder = new StringBuilder(new Date + ">>>> " + topicName + " is control of consumption: the limited time =" + consumerUtil.getLimitSleepTime + ",the limited size =" + getLogSize(topicName))
		val warningTopic: String = env + " yihaodian/detector消费限流预警"
		ConsumerUtil.sendWarnMessage(sb.toString, warningTopic)
	}
  def apply()={
    println("ConsumerUtil initialized")
    val v=new ConsumerUtil
    v.apply()
    v
  }

	/**
	 * 获取状态是否休眠
	 * 根据Logsize判断*/
	def getStatus(topicName: String, atomicInt: Int): Boolean = {
		if (atomicInt >= getLogSize(topicName)) {
			return true
		}
		else {
			return false
		}
	}

	def addAtomicInt(topicName: String): Boolean = {
		var logSize: Int = 0
		var atomic=concurrentInteger.get(topicName)
		if (atomic!=null) {
			logSize = atomic.incrementAndGet
		}
		else {
			logSize = 1
			atomic=new AtomicInteger(logSize)
			concurrentInteger.put(topicName, atomic)
		}
   // println("*************"+topicName+": logSize="+logSize+"**************")
		if (logSize >= getLogSize(topicName)) {
			return true
		}
		else {
			return false
		}
	}

	/** 清零操作 */
	def clearZero(topicName: String, atomicInt: AtomicInteger): AtomicInteger = {
		atomicInt synchronized {
			if (atomicInt.intValue >= getLogSize(topicName)) {
				atomicInt.set(0)
			}
		}
		return atomicInt
	}

	def getSleepTime: Int = {
    println("----------getLimitSleepTime="+consumerUtil.getLimitSleepTime+"---------"+(System.currentTimeMillis - consumerUtil.getStartTime())+"---------------")
		return consumerUtil.getLimitSleepTime - (System.currentTimeMillis - consumerUtil.getStartTime()).toInt
	}

	/** 获取topicName对应限制消费的大小 */
	def getLogSize(topicName: String): Int = {
		val paramObject: ParamObject = ZKService.getParamData
		val logSize: Int = paramObject.getLogSize(topicName)

		return logSize
	}



	/**
	 * 限流发送预警邮件信息*/
	def sendWarnMessage(content: String, warningTopic: String) {
		val itilParams= new util.HashMap[String, String]()
		val jsonObj: JSONObject = new JSONObject
		jsonObj.put("sendType", "email")
		jsonObj.put("emailReceivers", "it_architecture_soa@yhd.com")
		jsonObj.put("warningMessage", content)
		jsonObj.put("warningTopic", warningTopic)
		jsonObj.put("warningType", "1")
		jsonObj.put("time_interval", "30")
		itilParams.put("apiType", "detector")
		itilParams.put("param", jsonObj.toJSONString)
		val sendResult: String = HttpClientUtil.remoteExcuter(url, itilParams, HttpClientUtil.getItilClientBizLog)
	}
}
class ConsumerUtil {
  private var startTime: Long = 0l
  def apply()={
    println("---------------------------------------------------")
    println("class ConsumerUtil apply")
    println("---------------------------------------------------")
    try {
      ZkUtil.getZkClientInstance.subscribeDataChanges(MonitorConstants.PARAM_OBJECT, new IZkDataListener() {
        @throws(classOf[Exception])
        override def handleDataDeleted(dataPath: String) {
        }

        @throws(classOf[Exception])
        def handleDataChange(dataPath: String, data: AnyRef) {
          try {
            if (data != null) {
              val paramObject: ParamObject = data.asInstanceOf[ParamObject]
              println("zkData is changed.",paramObject.getLogSize("clientqueue_0"),paramObject.getLogSize("clientqueue_1"),
								paramObject.getLogSize("clientqueue_2"),paramObject.getLogSize("clientqueue_3"),paramObject.getLogSize("clientqueue_4"))
              ZKService.setParamData(paramObject)
            }
          }
          catch {
            case e: Exception => {
              println(dataPath + " handleDataChange error.", e)
            }
          }
        }
      })
    }catch{
      case ex:Exception=>ex.getMessage
    }
		val t=new Thread(new Runnable() {
      def run {
        while (true) {
          setStartTime(System.currentTimeMillis)
          try {
            Thread.sleep(getLimitSleepTime)
          }
          catch {
            case r: Exception => {
            }
          } finally {
            clearZero
         //   println("***********************clearZero*************************")
          }
        }
      }
    })
		t.setName("Consumer check thread")
		t.start

  }
  def setStartTime(startTime:Long): Unit ={
    this.startTime=startTime
  }
  def getStartTime():Long={
    startTime
  }

  /** 清零操作 */
  def clearZero {
    import scala.collection.JavaConversions._
    for (entry <- concurrentInteger.entrySet) {
      entry.getValue.set(0)
    }
  }
  def getLimitSleepTime: Int = {
    val paramObject: ParamObject = ZKService.getParamData
    var sleepTime: Int = paramObject.getLimitSleepTime
    if (sleepTime == 0) sleepTime = 60000
    return sleepTime
  }
}