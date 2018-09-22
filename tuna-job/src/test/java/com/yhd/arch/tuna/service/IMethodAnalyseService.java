package com.yhd.arch.tuna.service;

import com.google.code.morphia.Key;

/**
 * Created by root on 6/8/16.
 */
public interface IMethodAnalyseService {

    public <T> Key<T> save(T entity);

    public <T> Iterable<Key<T>> save(Iterable<T> entities);
}
