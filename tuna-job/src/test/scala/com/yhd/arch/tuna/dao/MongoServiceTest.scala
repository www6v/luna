package com.yhd.arch.tuna.dao

import java.util
import java.util.Date

import com.yhd.arch.tuna.dto._
import com.yhd.arch.tuna.linktree.dao.service.InitMongoDao
import com.yhd.arch.tuna.linktree.dto.{LinkTreeParam, _}
import com.yhd.arch.tuna.linktree.util.InternalConstant
import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import com.yihaodian.ymongo.client.util.SeqIdGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
/**
 * Created by root on 6/26/16.
 */
object MongoServiceTest {
  //private val mongoService=MongoService()
  def main(args:Array[String]): Unit ={
    test
  }



  def test(): Unit ={
    var k=0
    while(true) {
      k=k+1
      val nodeInfo = new NodeInfo("yihaodian/detector"+k, "method_"+k, "serviceName_1"+k, "group_1"+k, "serviceVersion_1"+k)
      val appNode = new AppNode(nodeInfo)
      val list: util.List[AppNode] = new util.ArrayList[AppNode]()
      list.add(appNode)

      val linkInfo = new LinkInfo
      linkInfo.setLinkId(System.nanoTime()+"")
      linkInfo.setLinkCounts(234343L)

      val linkTreeParam = new LinkTreeParam
      linkTreeParam.setLinkInfo(linkInfo)

      linkTreeParam.setAppNodeList(list)
      linkTreeParam.setLinkId(System.nanoTime()+"")
      linkTreeParam.setCreateTime(new Date().getTime)

      println(InitMongoDao.mongoDAO.save(linkTreeParam))
      try{
        Thread.sleep(500)
      }catch{
        case ex:Exception =>
          println("sleep exception",ex)
      }
    }
    //println(InitMongoDao.mongoDAO.save(nodeInfo))
 //   val filterMap=new util.HashMap[String,Object]()
  //  println(InitMongoDao.mongoDAO.find(classOf[LinkTreeParam],filterMap,"createTime",new Integer(1)))
  }

  def test3(): Unit ={
    val mongoDAO=LinkAnalyseDao.getBMongoDao()
    val list=new util.ArrayList[Testquery]()
    val start=System.currentTimeMillis()
    for(j<-0 to 10000) {
      var methodd = new Testquery()
      val ele: util.List[TestR] = new util.ArrayList[TestR]()
      val stringBuilder=new StringBuilder
      for (k <- 0 to 2) {
        val mapProxy = new TestR
        mapProxy.host = "192.168.16.2" + k + k+"."
        mapProxy.name = "socket_" + k + k+"."
        ele.add(mapProxy)
        stringBuilder.append("192.168.16.2" + k + k+".").append(mapProxy.name = "socket_" + k + k+".")
      }
      val key=stringBuilder.toString()
      methodd.setKey(key)
      methodd.setProxyList(ele)
      methodd.setCalledCount(1)
      methodd.gmtCreate = new Date()
      methodd.strTime = methodd.gmtCreate.getTime
     list.add(methodd)
    }
    mongoDAO.save(list)
    println("test3",System.currentTimeMillis()-start)
  }
/*
  def test {
    val mongoDAO=LinkAnalyseDao.getBMongoDao()
    var methodd=new Testquery()

   // val aclass:Class[Testquery]=methodds.getClass.asInstanceOf[Class[Testquery]]
 //   val operation=mongoDAO.(aclass)
  //  val query = mongoDAO.getQuery(aclass)
    val aclass:Class[Testquery]=methodd.getClass.asInstanceOf[Class[Testquery]]
    val query = mongoDAO.getQuery(aclass)
    mongoDAO.ensureIndexes(methodd.getClass)
    val operation=mongoDAO.(aclass)
    for(j<-0 to 5) {

      val ele: util.List[TestR] = new util.ArrayList[TestR]()
      methodd=new Testquery()
     // val aclass:Class[Testquery]=methodd.getClass.asInstanceOf[Class[Testquery]]
      for (k <- 0 to 2) {
        val mapProxy = new TestR
        mapProxy.host = "192.168.16.2" + k + k
        mapProxy.name = "socket_" + k + k
        ele.add(mapProxy)
      }
      methodd.setProxyList(ele)
      methodd.gmtCreate = new Date()
      methodd.strTime = methodd.gmtCreate.getTime
      //mongoDAO.save(methodd)



      val findProxy = new TestR
      findProxy.host = "192.168.15.11"
      findProxy.name = "name_1"
      val results = mongoDAO.getQuery(aclass).field("proxyList").hasThisElement(findProxy).asList()
      println("list", results.size())

      val strTime = methodd.gmtCreate.getTime
      println("strTime", strTime)
      query.field("proxyList").equal(ele).field("strTime").equal(strTime)

      operation.inc("strTime", 2)
      println(mongoDAO.update(query, operation,true))
    }
  }
  */
  val mongoService=MongoServiceTest()
  def apply()={
    val mongoService=new MongoServiceTest
  //  mongoService.init()
    mongoService
  }
  def getMongoDAO():MongoDAO={
    mongoService.mongoDAO
  }

  def testFindLinkAnalyse(): Unit ={

  }
}
class MongoServiceTest{
  private var appContext: ApplicationContext = null
  private var mongoDAO: MongoDAO = null
  def init(): Unit ={
    val gp: String = System.getProperty("global.config.path")
    if (HedwigUtil.isBlankString(gp)) {
      System.setProperty("global.config.path", InternalConstant.global_config_path)
      println("Can't find global config path,use default value",InternalConstant.global_config_path)
    }
    appContext = new ClassPathXmlApplicationContext("applicationContext.xml")
    mongoDAO = appContext.getBean("mongoDAO").asInstanceOf[MongoDAO]
  }

}
