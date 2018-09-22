package com.yhd.arch.tuna.dao.impl;

import com.google.code.morphia.Key;
import com.yhd.arch.tuna.dao.IMethodAnalyseDao;
import com.yihaodian.ymongo.client.YSimpleMongoClient;

/**
 * Created by root on 6/8/16.
 */
public class MethodAnalyseDaoImpl implements IMethodAnalyseDao{
    private YSimpleMongoClient mongoClient = null;

    public void setMongoClient(YSimpleMongoClient mongoClient){
        this.mongoClient = mongoClient;
    }

    @Override
    public <T> Key<T> save(T entity) {
        return mongoClient.save(entity);
    }

    @Override
    public <T> Iterable<Key<T>> save(Iterable<T> entities) {
        return mongoClient.save(entities);
    }

}
