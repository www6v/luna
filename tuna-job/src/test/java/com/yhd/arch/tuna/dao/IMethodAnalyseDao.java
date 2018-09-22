package com.yhd.arch.tuna.dao;


import com.google.code.morphia.Key;
import com.yihaodian.ymongo.client.YMongoClient;

/**
 * Created by root on 6/6/16.
 */
public interface IMethodAnalyseDao {

    public <T> Key<T> save(T entity);

    public <T> Iterable<Key<T>> save(Iterable<T> entities);
}
