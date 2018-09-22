package com.yhd.arch.tuna.linktree.serialize

import java.util

import com.yhd.arch.tuna.util.ParamConstants

/**
  * Created by root on 9/20/16.
  */
object SerializerFactory {

  val factoryMaps:util.Map[String,Serializers]=new util.HashMap[String,Serializers]()
  SerializerFactory()

  def apply()={
    factoryMaps.put(ParamConstants.KRYO_SERIALIZER,new KryoSerializers)
  }

  def getSerializer(key:String):Serializers={
    if (key == null)
      throw new Exception("serializer key must not null!!!")
    if (factoryMaps.containsKey(key)) {
      return factoryMaps.get(key)
    } else {
      throw new Exception("serializer key:" + key + " is not support")
    }
  }
}
