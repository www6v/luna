package com.yhd.arch.tuna.stream

import java.math.BigDecimal
import java.security.MessageDigest
import java.util

import com.yhd.arch.tuna.linktree.util.SignIDUtils
import com.yhd.arch.tuna.util.ParamConstants

/**
 * Created by root on 4/19/16.
 */
object Test {
    def main(args:Array[String]): Unit ={
        getShaId

    }

    def getShaId(): Unit ={
        val str="djfijhsdjfshdfsdhf237sdsd4e2837ds"
        println(SignIDUtils.createSpanId(str))
        val map =new util.HashMap[String,String]()
        for(k<-0 to 2) {
            map.put("ds_"+k, "sds_"+k)
        }
        println(map.keySet().toString)
    }
    def stringTolong(): Unit ={
        val layer=System.nanoTime()
        val string="11473839250360529000"

        val longKey=string.toDouble
        println("longKey",layer)
    }
    def print(): Unit ={
        for(i<- 0 until 5){
            for(j<-0 until 5){
                println( ParamConstants.DEFAULT_TOPIC + "_" + i)
            }
        }
        val big=new BigDecimal(1*1.0/3)
        println(big.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue())
    }
    def map(): Unit ={
        val map:util.Map[Long,Integer]=new util.TreeMap[Long,Integer]()
        val map2:util.Map[Long,Integer]=new util.TreeMap[Long,Integer]()
        var layer=System.nanoTime()
        var string=1+""+layer
        map.put(string.toLong,1)

       layer=System.nanoTime()
        string=2+""+System.nanoTime()
        map2.put(string.toLong,5)
        map.putAll(map2)
        val iterator=map.values().iterator()
        println(iterator.next())

       // println(map.size(),map)
    }
//    def add(v:HostStatistics): Unit ={
//        val hostServiceMap=v.getHostServiceMd4fsap()
//
//        val costMap=v.getCostMap()
//        val successMap=v.getSucceedMap()
//        //  val serviceCountMap=v.getServiceCountMap()
//        // val groupCount=v.getGroupCount
//        //    println("HostServiceMap:"+hostServiceMap+
//        //      "  succeddMap"+successMap)
//        //  getGroupCount().addAndGet(groupCount.intValue())
//
//        for(entry<-hostServiceMap.entrySet()){
//            val key=entry.getKey
//            val value=entry.getValue
//            val atomic= getHostServiceMap().get(key)
//            if(atomic==null){
//                getHostServiceMap().put(key,value)
//                getCostMap().put(key,costMap.get(key))
//                getSucceedMap().put(key,successMap.get(key))
//                //      getServiceCountMap.put(key,serviceCountMap.get(key))
//            }else{
//                atomic.addAndGet(value.intValue())
//                getCostMap.get(key).addAndGet(costMap.get(key).intValue())
//                getSucceedMap().get(key).addAndGet(successMap.get(key).intValue())
//                //      getServiceCountMap.get(key).addAndGet(serviceCountMap.get(key).intValue())
//            }
//        }
//    }
//
//    def checkCount(groupSize:Integer,serviceMap:util.HashMap[String,AtomicInteger]): Integer ={
//        var checkCount=0
//        for(entry<-getHostServiceMap().entrySet()){
//            val serviceName=entry.getKey
//            val hAtomic=entry.getValue
//            val sAtomic=serviceMap.get(serviceName)
//            // val hAtomic=getHostServiceMap.get(serviceName)
//            val countRate = hAtomic.intValue() * 1.0 / sAtomic.intValue()
//            val weight = weightMap.get(serviceName)
//            //  val groupSize = appAnalyst.getHostGroupSize(groupName)
//            val weightRate = weight * 1.0 / (groupSize + weight - 1)
//            val minRate = (weight - 1) * 1.0 / (groupSize + weight - 1 - 1)
//            println(serviceName+" >>>>> serviceCount=="+sAtomic+" hostServiceCount="+hAtomic
//              +" countRate= " + countRate + "  weightRate= " + weightRate + "  minRate=" + minRate)
//
//            if(countRate<=minRate){
//                println("change weight is not succeedful")
//                checkCount=1
//                //    HostWeightUtil.increaseWeight(serPathMap.get(serName), weight)
//            }
//        }
//        checkCount
//    }
//    /**计算host下所有service 的成功率*/
//    def makeSucceedRate(): Unit ={
//        for(entry<-succeedMap.entrySet()) {
//            val serviceName = entry.getKey
//            val succeed = entry.getValue.intValue()
//            val hostServiceCount = hostServiceMap.get(serviceName).intValue()
//            val succeedRate = succeed * 1.0 / hostServiceCount
//            println("serviceName:"+serviceName+" >>succeedRate="+succeedRate)
//            succeedRateMap.put(serviceName, succeedRate)
//        }
//    }
//    //  def makeSucceedRate(): Unit ={
//    //    for (entry <- serviceCountMap.entrySet()) {
//    //      val key = entry.getKey
//    //      val serviceCount = entry.getValue.intValue()
//    //      val succeed = succeedMap.get(key).intValue()
//    //      var rate = 0.0
//    //      if (serviceCount == 0) {
//    //        rate = 0
//    //      } else {
//    //        rate = (succeed * 100 * 1.0) / serviceCount
//    //      }
//    //      succeedRateMap.put(key,rate)
//    //    }
//    //  }
//
//    def checkSucceedRate(clientHostAnalyst: ClientHostAnalyst,sparkPoolIdParam: SparkPoolIdParam): Boolean ={
//        println("*************************************")
//        println("HostStatistics.checkSucceedRate")
//        println("*************************************")
//
//        val loop=new Breaks
//        var b=false
//        loop.breakable {
//            for (entry <- clientHostAnalyst.getSucceedRateMap().entrySet()) {
//                val key=entry.getKey
//                val originRate=entry.getValue
//                val rate=succeedRateMap.get(key)
//                // val rate=succeedCountMap.get(key).intValue()*100*1.0/serviceCountMap.get(key).intValue()
//                // succeedRateMap.put(key,rate)
//                println("checkSucceedRate======> rate="+rate+" history.rate="+originRate)
//                val d=originRate-rate
//                if(d<sparkPoolIdParam.getSucceedRate){
//                    b=true
//                    loop.break()
//                }
//            }
//        }
//        b
//    }
//
//    def increaseWeight(): Integer ={
//
//        var weight=0
//        for(en<-serPathMap.entrySet()){
//            weight=HostWeightUtil.increaseWeight(en.getValue)
//            val key=en.getKey
//            weightMap.put(key,weight)
//        }
//        weight
//    }
//
//    def recoverWeight(): Unit ={
//        for(ser<-serPathMap.entrySet()){
//            HostWeightUtil.recoverWeight(ser.getValue)
//            // HostWeightUtil.recoverWeight(ser.getValue,OriginweighMap.get(ser.getKey))
//        }
//    }
//    def calculateRate(): Unit ={
//
//    }
//
//    def  initWeight(treeSet:util.TreeSet[String]): Unit ={
//        for(value<-treeSet) {
//            val serviceName = getServiceName(value)
//            serPathMap.put(serviceName, value)
//            val weight=HostWeightUtil.getWeight(value)
//            weightMap.put(serviceName,weight)
//            originWeighMap.put(serviceName,weight)
//        }
//    }
//
//
//    private def getServiceName(serpath:String): String={
//        serpath.split("/").apply(4)
//    }
//
//    def getWeightMap():util.HashMap[String,Integer]={
//        weightMap
//    }
//    def getSucceedRateMap(): util.HashMap[String,Double]={
//        succeedRateMap
//    }
}
