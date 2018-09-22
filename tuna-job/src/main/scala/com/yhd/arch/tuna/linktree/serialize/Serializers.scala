package com.yhd.arch.tuna.linktree.serialize

/**
  * Created by root on 9/20/16.
  */
trait Serializers{

  def serialize[T](data:T):Array[Byte]

  def  deserialize(data:Array[Byte]):Any
}
