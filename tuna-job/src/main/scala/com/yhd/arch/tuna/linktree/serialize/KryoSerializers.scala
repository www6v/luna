package com.yhd.arch.tuna.linktree.serialize

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}

/**
  * Created by root on 9/20/16.
  */
class KryoSerializers extends Serializers{
  val kryo=new Kryo()
  def serialize[T](data:T): Array[Byte] ={
    val output=new Output(new ByteArrayOutputStream())
    kryo.writeClassAndObject(output,data)
    output.toBytes
  }

  def  deserialize(data:Array[Byte]): Any ={
    val input=new Input(new ByteArrayInputStream(data))
    kryo.readClassAndObject(input)
  }
}
