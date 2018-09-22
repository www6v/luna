package com.yhd.arch.tuna.balancer;

import com.yhd.arch.tuna.linktree.balancer.ConsistanceHashBalancer;
import com.yhd.arch.tuna.linktree.dao.service.LinkDataToRedis;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by root on 12/16/16.
 */
public class BalancerTest {

	public static void main(String[] args){
		BalancerTest test=new BalancerTest();
		test.hashBalancer();
		System.out.println();
	}

	public void hashBalancer(){
		Map<Integer,AtomicLong> map=new HashMap<Integer,AtomicLong>();
		String pool="23395d57bc329da5986fb75bd6cfeacd342cc73623395d57bc329da5986fb75bd6cfeacd342cc736";
		for(int i=0;i<10000;i++){
		//	pool=pool+System.nanoTime();
			int value= LinkDataToRedis.balancer().select(pool);
			AtomicLong atomicLong=map.get(value);
			if(atomicLong!=null){
				atomicLong.incrementAndGet();
			}else{
				map.put(value,new AtomicLong(1));
			}
		}


		for(Map.Entry<Integer,AtomicLong> entry:map.entrySet()){
			System.out.print(entry.getKey()+" ,"+entry.getValue().longValue()+"          ");
		}
	}

}
