package com.yhd.arch.tuna.service.impl;

import com.google.code.morphia.Key;
import com.yhd.arch.tuna.dao.IAppDependenceDao;
import com.yhd.arch.tuna.service.IAppDependenceService;

/**
 * Created by root on 6/8/16.
 */
public class AppDependenceServiceImpl implements IAppDependenceService {

    private IAppDependenceDao dao = null;

    public void setDao(IAppDependenceDao dao){
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
