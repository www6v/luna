package com.yhd.arch.tuna.sql.test
import org.bson.Document
import com.mongodb.spark._
import com.mongodb.spark.config._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Column
import org.apache.spark.{SparkConf, SparkContext}
import java.util.Date

import com.mongodb.client.MongoCollection
import com.mongodb.spark.sql.helpers.StructFields
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DataTypes, StructField}

import scala.collection.mutable.ArrayBuffer
/**
  * Created by root on 1/5/17.
  */
object SparkSqlTest {

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
        conf.setAppName("SparkMongoTest")
        conf.setMaster("spark://liumiao02:7077")
        conf.set("spark.cores.max", "8")
        conf.set("spark.executor.cores","4")
        conf.set("spark.executor.memory", "1g")
        conf.set("spark.mongodb.input.uri","mongodb://jumper:jumper123@10.161.144.67:27017/admin.queryLinkInfoTreeParam")
        conf.set("spark.mongodb.output.uri","mongodb://jumper:jumper123@10.161.144.67:27017/admin.queryLinkInfoTreeParam")
        conf.setExecutorEnv("spark.executor.instances","10")
        conf.setJars(List("/home/chenyuyao1/tuna-sql.jar"))

        //println(conf.get("spark.debug.maxToStringFields"))
        val context = new SparkContext(conf)
        val sqlSession = SparkSession.builder().appName("MongoTest").getOrCreate()
        sqlSession.conf.set("spark.debug.maxToStringFields","50")
        sqlSession.conf.set("spark.sql.shuffle.partitions","20")

        import java.util.Date
        val df=MongoSpark.load(sqlSession)
        df.printSchema()
        val nowDate=new Date().getTime-60000
        val filter_df=df.filter(df("createTime") > nowDate)


        val now=new Date().getTime

        val appList_df= filter_df.select(df("linkId"),df("createTime"),explode(df("appNodeList"))).toDF("linkId","createTime","appNodeList")

        appList_df.createOrReplaceTempView("table")
        val sqlDF=sqlSession.sql("select linkId,last(createTime) as createTime,last(appNodeList) as appNodeList from table group by linkId")
        sqlDF.groupBy("linkId").count().show()

        val singleton_df=sqlDF


        val linkInfo_df=filter_df.groupBy(df("linkId")).agg(
            sum(df("linkInfo.linkCounts")) as "linkCounts",
              sum(df("linkInfo.errorCounts")) as "errorCounts",
              max(df("linkInfo.linkCurtLayer")) as "linkCurtLayer"
          )

        linkInfo_df.show()

        val result=singleton_df.joinWith(linkInfo_df,singleton_df("linkId")===linkInfo_df("linkId"))

        val results=singleton_df.join(linkInfo_df,"linkId")
        results.show()

        val writeConfig = WriteConfig(Map("collection" -> "mergeLinkParam", "writeConcern.w" -> "majority"), Some(WriteConfig(context)))

        MongoSpark.builder()
        MongoSpark.save(results.rdd,writeConfig)
        val mongoConnector = MongoConnector(writeConfig.asOptions)
      //  mongoConnector.withCollectionDo(writeConfig,{ collection: MongoCollection =>collection.})
    }
}
