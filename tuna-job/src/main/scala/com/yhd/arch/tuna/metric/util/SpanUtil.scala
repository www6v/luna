package com.yhd.arch.tuna.metric.util

/**
 * Created by root on 10/8/16.
 */

import java.util.{HashSet, Set}

import com.yhd.arch.tuna.metric.SpanType

object SpanUtil {
  val TARGET: Set[SpanType] = new HashSet[SpanType]
  val CALLER: Set[SpanType] = new HashSet[SpanType]
  val OTHERS: Set[SpanType] = new HashSet[SpanType]
  try {
    TARGET.add(SpanType.HTTP_SERVER)
    TARGET.add(SpanType.THRIFTSERVER)
    TARGET.add(SpanType.ROOT_METHOD)


    CALLER.add(SpanType.HTTP)
    CALLER.add(SpanType.THRIFTCLIENT)

    OTHERS.add(SpanType.SQL)
    OTHERS.add(SpanType.REDIS)
    OTHERS.add(SpanType.CACHE)
    OTHERS.add(SpanType.KAFKA)
    OTHERS.add(SpanType.RABBITMQ)
    OTHERS.add(SpanType.MEMCACHED)
    OTHERS.add(SpanType.HBASE)
    OTHERS.add(SpanType.LOG)
    OTHERS.add(SpanType.METRIC)
    OTHERS.add(SpanType.KV)


    TARGET.add(SpanType.HEDIG_SERVER) ///
    CALLER.add(SpanType.HEDIG_CLIENT) ///
  }
}


