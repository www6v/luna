package com.yhd.arch.tuna.handle

import com.yihaodian.architecture.hedwig.common.hash.HashFunctionFactory

/**
  * Created by root on 9/19/16.
  */
object HashFilter {

  private val hf=HashFunctionFactory.getInstance().getMur2Function

  def main(args: Array[String]) {
    test()
  }
  def test(): Unit ={
    val uniqReqId="9@152359@backend-product-we@2773679@-1206865097"
    val v=hf.hash32(uniqReqId)
    println("v",v,Int.MaxValue)
  }
}
