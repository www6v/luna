package com.yhd.arch.tuna.dao

import java.util.Date

import com.yhd.arch.tuna.linktree.dto.LinkAnalyse
import com.yhd.arch.tuna.pressure.dao.BasicMongoDAO

/**
  * Created by root on 8/18/16.
  */
object LinkAnalyseDao extends BasicMongoDAO{
  val linkAnalyseDao=LinkAnalyseDao()

  def apply() = {
   val analyse : LinkAnalyseDao = new LinkAnalyseDao()
    analyse.ensureIndex()
    analyse
  }
}

class LinkAnalyseDao{

  //创建索引
  def ensureIndex(): Unit ={
    val linkAnalye=new LinkAnalyse()
    val mongoDao=BasicMongoDAO.getBMongoDao()
    if(mongoDao!=null) {
      mongoDao.ensureIndexes(linkAnalye.getClass)
    }else{
      println(new Date,"ensureIndex is failure")
    }
  }

}
