package com.yhd.arch.tuna.dao

import java.lang.Iterable
import java.util

import com.google.code.morphia.Key
import com.google.code.morphia.query.{Query}
import com.mongodb.WriteConcern
import com.yihaodian.ymongo.client.YSimpleMongoClient

import scala.collection.JavaConversions._
/**
 * Created by root on 5/27/16.
 */
class MongoDAO {
  private var mongoClient: YSimpleMongoClient = null

  def setMongoClient(mongoClient: YSimpleMongoClient) {
    this.mongoClient = mongoClient
  }

  def save[T](entity: T): Key[T] = {
    return mongoClient.save(entity)
  }


  def save[T](entities: Iterable[T]):Iterable[Key[T]]={
    return mongoClient.save(entities)
  }


  def save[T](entity:T,wc: WriteConcern): Unit ={
    mongoClient.save(entity,wc)
  }


  def find(aclass: Class[_], filters: util.HashMap[String, AnyRef]): util.List[_] = {
    return mongoClient.find(aclass, filters)
  }

  def deleteByCriteria(aclass: Class[_],filters:util.HashMap[String,AnyRef]): Unit ={
    val query= mongoClient.getQuery(aclass)
    for (k <- filters.keySet) {
      query.filter(k, filters.get(k))
    }
    mongoClient.deleteByCriteria(query)
  }

  def getQuery[T](entityClass: Class[T]):Query[T]= {
    mongoClient.getQuery(entityClass)
  }

  def ensureIndexes(clazz: Class[_]){
    mongoClient.ensureIndexes(clazz)
  }
}
