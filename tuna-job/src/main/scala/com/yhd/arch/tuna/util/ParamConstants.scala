package com.yhd.arch.tuna.util

/**
 * Created by root on 3/14/16.
 */
object ParamConstants {
  val CONFIG_GROUP                                      ="yihaodian_tuna"

  val CONFIG_TUNA_SPARK                                 ="tuna_spark.properties"

  val CONFIG_TUNA_MONGO                                 ="tuna_mongo.properties"

  val TUNA_MESSURE_PATH                                 ="/MessureParam"

  //#####################废弃字段#############################################

  val SPARK_EXECUTOR_MEMORY                             ="spark.executor.memory"

  val SPARK_MAX_CORES                                   ="spark.cores.max"

  val SPARK_EXECUTOR_CORES                              ="spark.executor.cores"

  val SPARK_STREAMING_BLOCKINTERVAL                     ="spark.streaming.blockInterval"

  val SPARK_MASTERURL                                   ="spark.master"

  val SPARK_CHECKPOINTPATH                              ="spark.checkpoint.path"

  val SPARK_FILES                                       ="spark.files"

  val SPARK_RPC_ASKTIMEOUT                              ="spark.rpc.askTimeout"

  val SPARK_NETWORK_TIMEOUT                             ="spark.network.timeout"

  val SPARK_SERIALIZER                                  ="spark.serializer"

  val SPARK_STREAMING_BACKPRESSURE_ENABLED              ="spark.streaming.backpressure.enabled"

  val SPARK_STREAMING_RECEIVER_MAXRATE                  ="spark.streaming.receiver.maxRate"

  val SPARK_EXECUTOR_EXTRAJAVAOPTIONS                   ="spark.executor.extraJavaOptions"

  val SPARK_EVENTLOG_ENABLED                            ="spark.eventLog.enabled"

  val SPARK_EVENTLOG_DIR                                ="spark.eventLog.dir"

  val SPARK_DRIVER_MEMORY                               ="spark.driver.memory"

  val SPARK_SUBMIT_DEPLOYMODE                           ="spark.submit.deployMode"

  val SPARK_STREAMING_CONCURRENTJOBS                    ="spark.streaming.concurrentJobs"

  val SPARK_CLEANER_TTL                                 ="spark.cleaner.ttl"

  val  SPARK_JAR_PATH                                   ="/data/M00/tuna/tuna-0.1-SNAPSHOT.jar"

  val SPARK_WINDOWDURATION                              ="pressure.windowDuration"

  val SPARK_SLIDEDURATION                               ="pressure.slideDuration"

  val SPARK_RECEIVER_COUNT                              ="spark.receiver.count"

  val SPARK_DEFAULT_PARALLELISM                         ="spark.default.parallelism"

  val SPARK_RECEIVER_MULTIPLE                           ="spark.receiver.multiple"

  //##################################################################

  val SPARK_HEALTH_ROLL_PATH                            ="/spark_roll"

  val SPARK_HEALTH_PATH                                 ="/spark_health"

  val SPARK_HEALTH_ROOT                                 ="/detector"

  val SPARK_HEALTH_PARAM_PATH                           ="/spark_param"

  val DEFAULT_TOPIC                                     ="clientqueue"

  val SPARK_PREFIX                                      ="spark."

  val JUMPER_CONSUMER_NAME                                 ="jumper.consumer.name"

  val JUMPER_TOPIC_COUNT                                ="jumper.topic.count"

  val JUMPER_TOPIC_MULTIPLE                             ="jumper.topic.multiple"

  val WINDOW_DURATION                                   ="window.duration"

  val SLIDE_DURATION                                    ="silde.duration"

  val BATCH_DURATION                                    ="batch.duration"

  val SPARK_APP_NAME                                    ="spark.app.name"

  val COST_TIME_MULTIPLE                                =2

  val KRYO_SERIALIZER                                   ="kryo_serializer"

  val DEFAULT_USETIME                                   =3*60*1000

  val KEY_EXPIRE                                        =60*60*24

  val ONE_HOUR_EXPIRE                                   =60*60

  val METRIC_EXPIRE                                     =30

  val TUNA_CURSOR_PATH                                  ="/detector/tuna/CursorParam"

  val LINK_INDEX_SIZE                                   =5000

  val CONTAINER_SIZE                                    =20

  val ONE_MIN                                           =60*1000

  val ONE_HOUR                                          =60*60*1000

}
