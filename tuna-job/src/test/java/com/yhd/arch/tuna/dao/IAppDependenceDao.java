package com.yhd.arch.tuna.dao;


import com.google.code.morphia.Key;

/**
 * Created by root on 6/6/16.
 */
public interface IAppDependenceDao {

    public <T> Key<T> save(T entity);

    public <T> Iterable<Key<T>> save(Iterable<T> entities);
}
