package com.yhd.arch.tuna.pressure.zk

import java.util
import java.util.concurrent._
import java.util.regex.Pattern

import com.yhd.arch.tuna.linktree.util.InternalConstant
import com.yihaodian.architecture.hedwig.common.constants.InternalConstants
import com.yihaodian.architecture.hedwig.common.util.{HedwigThreadFactory, HedwigUtil, ZkUtil}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
/**
  * every 2 minutes scaning the zk nodes,find out the effective registration service node
  * snapshot hashMap
 * Created by root on 3/2/16.
 */
object ClusterManager {

  private val LOG: Logger = LoggerFactory.getLogger(ClusterManager.getClass)
  def config(): Unit ={
    val gp =System.getProperty("global.config.path")
    if(HedwigUtil.isBlankString(gp)) {
      System.setProperty("global.config.path",  InternalConstant.global_config_path)
    }

  }
  private val tp:ThreadPoolExecutor=new ThreadPoolExecutor(InternalConstants.DEFAULT_POOL_CORESIZE,
    60, InternalConstants.DEFAULT_POOL_IDLETIME, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable](800), new HedwigThreadFactory)


  def run(): Unit ={
    val scheduledTask=Executors.newScheduledThreadPool(2)
    scheduledTask.scheduleAtFixedRate(domain(),1,2*1000*60,TimeUnit.MILLISECONDS)

  }

  def domain(): Runnable ={
    new Runnable {
      override def run(): Unit = {
        println("domain")
        config()
        val list=ZkUtil.getZkClientInstance.getChildren(InternalConstants.HEDWIG_PAHT_APPDICT)
        val pathList=new util.HashMap[String,String]()
        for(ele<-list){
          val str=ele.replace("#","/").split(":")
          val appPath=str.apply(0)
          if(!appPath.contains("$")) {
            pathList.put(appPath,ele)
          }
        }
        for(entry<-pathList.entrySet()) {
          val path = entry.getKey
          if (ZkUtil.getZkClientInstance.exists(path)) {
            tp.execute(new Runnable {
              override def run(): Unit = {
                // val domain = InternalConstants.BASE_ROOT + "/" + path
                val hostServiceJob = new util.HashMap[String, util.TreeSet[String]]()
                val level = 1
                newScanZKAllHostPath(path, hostServiceJob, level)
                ClusterUtils.setClusterServer(path, hostServiceJob)
              }
            })
          }else{
          val path=entry.getValue
            ZkUtil.getZkClientInstance.delete(InternalConstants.HEDWIG_PAHT_APPDICT+"/"+path)
            println("entry.getValue "+path)
           // println("detele "+ZkUtil.getZkClientInstance.deleteRecursive(path))
          }
        }
      }
    }

   }

  def newScanZKAllHostPath(parentPath: String,hostServiceJob: util.HashMap[String,util.TreeSet[String]]
                           ,level:Int): Unit = {
    var pathList:util.List[String]=null
    try {
      pathList = ZkUtil.getZkClientInstance.getChildren(parentPath)
    }catch{
      case ex:Throwable=> {
        println("查找zk路径[" + parentPath + "]子节点失败:" )
        LOG.warn("查找zk路径[" + parentPath + "]子节点失败:")
      }
    }
    if(pathList==null)
      return
    if(parentPath.endsWith(InternalConstants.HEDWIG_PAHT_ROLL)){
      return
    }
    val childLevel=level+1
    if(childLevel>6){return}

    if(level==1&&(pathList==null||pathList.size()<=0)){
      return
    }
    try {
      for (k <- 0 to pathList.size() - 1) {
        val path: String = pathList.get(k)
        if (!path.equals(InternalConstants.HEDWIG_PAHT_CAMPS)) {
          val groupKey = new StringBuilder(parentPath)
          groupKey.append("/").append(path)
          if (!path.contains(".")) {
            newScanZKAllHostPath(groupKey.toString(), hostServiceJob, childLevel)
          } else {
            val segments = path.split("\\.")
            if (segments != null && segments.length == 4 && path.contains(":")) {
              val ip=path.split(":").apply(0)
              collectServerPath(ip, hostServiceJob, groupKey.toString())
//            } else if (segments != null && segments.length == 4 && !path.contains(":")) {
//              // 没有端口的情况，即端口是80
//              // 通过正则验证是否是IP地址，
//             // val matcher = pattern.matcher(path)
////              if (matcher.matches()) {
////                collectServerPath(path, hostServiceJob, serviceCountMap,groupKey.toString())
////                println(" matches path: "+path)
////              }
////              else {
//                newScanZKAllHostPath(groupKey.toString(), hostServiceJob, serviceCountMap,childLevel)
           //   }
            } else {
              newScanZKAllHostPath(groupKey.toString(), hostServiceJob, childLevel)
            }
          }
        }
      }
    }catch{
      case ex:Exception =>{
        ex.printStackTrace()
        LOG.error("msgError:"+ex.getCause,ex)
      }
    }
  }

  val ipRegx= "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))"
  val pattern=Pattern.compile(ipRegx)

  def collectServerPath(ip: String,hostServiceJob: util.HashMap[String,util.TreeSet[String]],path: String ): Unit ={
    var hostD=hostServiceJob.get(ip)
    if(hostD==null) {
      hostD = new util.TreeSet[String]()
      hostServiceJob.put(ip, hostD)
    }
    hostD.add(path)
  }

  def getService(host:String): String ={
    val str=host.split("/")
    val service=str.apply(4)
    service
  }

}
