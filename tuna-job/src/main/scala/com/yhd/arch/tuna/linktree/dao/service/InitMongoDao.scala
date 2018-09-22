package com.yhd.arch.tuna.linktree.dao.service

import com.mongodb.BasicDBObject
import com.yhd.arch.tuna.linktree.dao.MongoDAO
import com.yhd.arch.tuna.linktree.dto.{LinkTreeParam}
import com.yhd.arch.tuna.linktree.util.InternalConstant
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
  * Created by root on 11/18/16.
  */
object InitMongoDao {
  val mongoDAO=initMongo()

  def initMongo() = {
    println("initMongo")
    val gp: String = System.getProperty("global.config.path")
    if (HedwigUtil.isBlankString(gp)) {
      System.setProperty("global.config.path", InternalConstant.global_config_path)
      println("Can't find global config path,use default value",InternalConstant.global_config_path)
    }
    val appContext: ApplicationContext = new ClassPathXmlApplicationContext("applicationContext.xml")
    val mongoDAO = appContext.getBean("mongoDAO").asInstanceOf[MongoDAO]
    mongoDAO.ensureCaps(classOf[LinkTreeParam])
    ensureIndex(mongoDAO)

    mongoDAO
  }

  //创建索引
  def ensureIndex(mongoDAO:MongoDAO): Unit ={

    mongoDAO.ensureIndexes(classOf[LinkTreeParam])
    val query = mongoDAO.getQuery(classOf[LinkTreeParam])
    val basic=new BasicDBObject()
    basic.put("linkId",new Integer(1))
    query.getCollection.createIndex(basic)

  }


}
