package com.yhd.arch.tuna.dao;

import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.yihaodian.ymongo.client.YSimpleMongoClient;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 8/22/16.
 */
public class BMongoDao {

	private YSimpleMongoClient mongoClient = null;

	public void setMongoClient(YSimpleMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public <T> Key<T> save(T entity) {
		return mongoClient.save(entity);
	}

	public <T> Iterable<Key<T>> save(Iterable<T> entities) {
		return mongoClient.save(entities);
	}

	public <T> List<T> find(Class<T> entityClass, Map<String, Object> filters) {
		return mongoClient.find(entityClass,filters);
	}

	public <T> Query<T> getQuery(Class<T> entityClass) {
		return mongoClient.getQuery(entityClass);
	}

	public <T> void ensureIndexes(final Class<T> clazz) {
		mongoClient.ensureIndexes(clazz);
	}

}
