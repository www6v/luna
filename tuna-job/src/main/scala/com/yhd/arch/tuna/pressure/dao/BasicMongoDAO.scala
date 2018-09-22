package com.yhd.arch.tuna.pressure.dao

import com.yhd.arch.tuna.dao.BMongoDao
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import org.apache.spark.SparkFiles
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
  * 初始化mongo读写操作类
  *
  * Created by root on 8/18/16.
  */
object BasicMongoDAO {
  val basicMongoDAO=BasicMongoDAO()
  def getBMongoDao():BMongoDao={
    basicMongoDAO.mongoDAO
  }
  def apply()={
   val basic : BasicMongoDAO = new BasicMongoDAO()
    basic.init()
    basic
  }
}
class BasicMongoDAO{
  private var appContext: ApplicationContext = null
  private var mongoDAO: BMongoDao = null

  def init(): Unit ={
    val gp: String = System.getProperty("global.config.path")

    if (HedwigUtil.isBlankString(gp)) {

      val sparkfiles=SparkFiles.get("env.ini").replaceAll("env.ini","")

      System.setProperty("global.config.path", sparkfiles)

      println("Can't find global config path,use default value",sparkfiles)
    }

    appContext = new ClassPathXmlApplicationContext("applicationContext.xml")

    mongoDAO = appContext.getBean("mongoDAO").asInstanceOf[BMongoDao]
  }

  def getBMongoDao():BMongoDao={
    BasicMongoDAO.getBMongoDao
  }
}
