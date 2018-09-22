package com.yhd.arch.tuna.linktree.util

import java.util.Properties

import com.yhd.arch.tuna.util.ParamConstants
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil
import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer
import org.slf4j.{Logger, LoggerFactory}

/**
  * 读取初始化spark的配置文件
  *
 * Created by root on 5/25/16.
 */
object ConfigLoader {
  val LOG: Logger = LoggerFactory.getLogger(ConfigLoader.getClass)
  private var instance:ConfigLoader=ConfigLoader()
  def apply()={
    instance=new ConfigLoader
    instance.init()
    instance
  }

  def getSparkENVProperties(): Properties={
    instance.sparkConfigProp
  }

  def main(args: Array[String]) {
    println(getSparkENVProperties())
  }
}
class ConfigLoader{
  private val sparkConfigProp=new Properties()

  def init()={
 //   println("env.ini:",ConfigLoader.getClass.getClassLoader.getResource("env.ini"))

    try {
      val gp =System.getProperty("global.config.path")
      if(HedwigUtil.isBlankString(gp)) {
        System.setProperty("global.config.path",  InternalConstant.global_config_path)

        ConfigLoader.LOG.warn("Can't find global config path,use default value:"+InternalConstant.global_config_path)
      }
      val properties = YccGlobalPropertyConfigurer.loadConfigProperties(ParamConstants.CONFIG_GROUP,
                                                                        ParamConstants.CONFIG_TUNA_SPARK,
                                                                        false)
      if (properties != null && properties.size() > 0) {
        sparkConfigProp.putAll(properties)
      }

    }catch{
      case ex:Exception=>
        ConfigLoader.LOG.error("get ycc property config is fail",ex)
    }
  }



}
