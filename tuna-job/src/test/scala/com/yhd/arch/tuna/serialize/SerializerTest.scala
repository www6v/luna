package com.yhd.arch.tuna.serialize

import com.yhd.arch.tuna.util.ParamConstants
import java.util

import com.yhd.arch.tuna.linktree.serialize.SerializerFactory
/**
  * Created by root on 9/20/16.
  */
object SerializerTest {

  def main(args: Array[String]) {
    val serializer=SerializerFactory.getSerializer(ParamConstants.KRYO_SERIALIZER)
    val string:String="we are friend"
    val list=new util.ArrayList[String]()
    list.add(string)

    for(k<-0 to 100) {
      list.add("hello"+k)
    }
    val bytelist=serializer.serialize(list)
    println(serializer.deserialize(bytelist))

  }
}
