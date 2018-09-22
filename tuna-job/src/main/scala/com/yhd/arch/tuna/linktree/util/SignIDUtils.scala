package com.yhd.arch.tuna.linktree.util

import java.security.MessageDigest

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory

/**
  * 该工具类生成唯一Id
  * Created by root on 11/14/16.
  *
  */
object SignIDUtils {
  val logger= LoggerFactory.getLogger(SignIDUtils.getClass)

  private def string2SHA(str:String):String ={

    val byteArray=str.getBytes()
    val shaBytes = DigestUtils.sha(byteArray)
    val hexValue = new StringBuffer()
    for (i <-0 to shaBytes.length-1 ){
      val value=  shaBytes.apply(i) & 0xff
      if (value < 16)
        hexValue.append("0")
      hexValue.append(Integer.toHexString(value))
    }
    hexValue.toString()
  }

  private def string2MD5(str:String):String ={


    val byteArray=str.getBytes()

    var md5Bytes:Array[Byte]=new Array[Byte](0)
    try {
      md5Bytes=  DigestUtils.md5(byteArray)
    }catch{
      case ex:Exception =>
        println(str,ex)
    }
    val hexValue = new StringBuffer()
    for (i <-0 to md5Bytes.length-1 ){
      val value=  md5Bytes.apply(i) & 0xff
      if (value < 16)
        hexValue.append("0")
      hexValue.append(Integer.toHexString(value))
    }
    hexValue.toString()
  }

  def createSpanId(string:String): String ={
    string2MD5(string)
  }

  def createLinkId(string:String):String={
    string2SHA(string)
  }
}
