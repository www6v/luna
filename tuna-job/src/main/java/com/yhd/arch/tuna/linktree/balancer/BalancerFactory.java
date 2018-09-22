package com.yhd.arch.tuna.linktree.balancer;

import com.yhd.arch.tuna.linktree.exception.InvalidParamException;
import com.yhd.arch.tuna.linktree.util.InternalConstant;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by root on 12/16/16.
 */
public class BalancerFactory {
	private static BalancerFactory balancerFactory=new BalancerFactory();

	public Map<String,ConsistanceHashBalancer> map=new HashMap<String,ConsistanceHashBalancer>();
	private BalancerFactory(){
		map.put(InternalConstant.BALANCER_NAME_CONSISTENTHASH,new ConsistanceHashBalancer());
	}

	public static BalancerFactory getInstance(){
		return balancerFactory;
	}

	public ConsistanceHashBalancer getConsistantHashBalancer(){
		return map.get(InternalConstant.BALANCER_NAME_CONSISTENTHASH);
	}


	public Balancer getBalancer(String key) throws Exception{
		if(key == null) {
			throw new InvalidParamException("Hash function key must not null!!!");
		} else if(this.map.containsKey(key)) {
			return (Balancer)this.map.get(key);
		} else {
			throw new Exception("Hash function key:" + key + " is not support");
		}
	}
}
