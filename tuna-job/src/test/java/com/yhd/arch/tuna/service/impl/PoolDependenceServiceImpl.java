package com.yhd.arch.tuna.service.impl;

import com.google.code.morphia.Key;
import com.yhd.arch.tuna.dao.IPoolDependenceDao;
import com.yhd.arch.tuna.service.IPoolDependenceService;

/**
 * Created by root on 6/8/16.
 */
public class PoolDependenceServiceImpl implements IPoolDependenceService {

    private IPoolDependenceDao dao = null;

    public void setDao(IPoolDependenceDao dao){
        this.dao = dao;
    }
    @Override
    public <T> Key<T> save(T entity) {
        return dao.save(entity);
    }

    @Override
    public <T> Iterable<Key<T>> save(Iterable<T> entities) {
        return dao.save(entities);
    }

}
