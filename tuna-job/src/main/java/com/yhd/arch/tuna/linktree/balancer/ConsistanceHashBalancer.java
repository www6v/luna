package com.yhd.arch.tuna.linktree.balancer;

import com.yihaodian.architecture.hedwig.common.hash.HashFunction;
import com.yihaodian.architecture.hedwig.common.hash.HashFunctionFactory;
import com.yihaodian.architecture.jumper.common.netty.component.Circle;
import org.apache.hadoop.hdfs.server.balancer.*;

import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by root on 12/15/16.
 */
public class ConsistanceHashBalancer implements Balancer<Integer>{
	private static HashFunction hf = HashFunctionFactory.getInstance()
			.getMur2Function();
	private Lock lock = new ReentrantLock();
	private static Circle<Long, Integer> indexCircle = new Circle<Long, Integer>();
	private Random random = new Random();

	@Override
	public Integer select(Object key){
		int value=0;
		String string=(String)key;
		long code = hf.hash64(string.getBytes());
		value=getIndexFromCircle(code);
		return value;
	}

	public static Integer getIndexFromCircle(long code){
		int size = indexCircle.size();
		Integer sp = null;
		if (size > 0) {
			Long tmp = code;
			while (size > 0) {
				tmp = indexCircle.lowerKey(tmp);
				sp = indexCircle.get(tmp);
				if (sp != null) {
					break;
				}
				size--;
			}
		}
		return sp;
	}

	@Override
	public void initIndex(Integer index){
		lock.lock();
		try {
			Circle<Long, Integer> circle = new Circle<Long, Integer>();

			for (int sp =1;sp<=index;sp++ ) {
				String feed ="initindex"+sp;
				long key = hf.hash64(feed);
				put2Circle(key, sp, circle);
			}
			indexCircle = circle;
		} finally {
			lock.unlock();
		}
	}

	private void put2Circle(long key,Integer sp,TreeMap<Long, Integer> circle){
		if (circle.containsKey(key)) {
			Long lower = circle.lowerKey(key);
			if (lower == null) {
				key = key / 2;
			} else {
				key = lower + (key - lower) / 2;
			}
			put2Circle(key, sp, circle);
		} else {
			circle.put(key, sp);
		}
	}
}
