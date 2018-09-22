package com.yhd.arch.tuna.linktree.balancer;

/**
 * Created by root on 12/16/16.
 */
public interface Balancer<T> {

	public  T select(Object object);
	public void initIndex(T t);

}
