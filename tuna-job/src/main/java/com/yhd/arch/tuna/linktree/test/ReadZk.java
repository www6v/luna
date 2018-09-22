package com.yhd.arch.tuna.linktree.test;

import com.yhd.arch.tuna.linktree.util.InternalConstant;
import com.yihaodian.architecture.hedwig.common.exception.HedwigException;
import com.yihaodian.architecture.hedwig.common.util.HedwigUtil;
import com.yihaodian.architecture.hedwig.common.util.ZkUtil;
import com.yihaodian.architecture.jumper.common.inner.config.impl.TopicZoneReplication;
import com.yihaodian.architecture.zkclient.ZkClient;

/**
 * Created by root on 12/6/16.
 */
public class ReadZk {
	public static void init(){
		String gp = System.getProperty("global.config.path");
		if (HedwigUtil.isBlankString(gp)) {
			System.out.println("global.config.path "+ InternalConstant.global_config_path);
			System.setProperty("global.config.path", InternalConstant.global_config_path);
		}
	}
	public static void main(String[] args){
		init();
		readZkNode();
	}
	public static void readZkNode(){
		try {
			ZkClient zkClient = ZkUtil.getZkClientInstance();
			String path = "/JumperMQ/Topic/serverqueue/ZONE_JQ_STG#hedwig#hedwig";
			String paths="/FlagsCenter/yihaodian#front-oms/gray_hedwig";
			if (zkClient.exists(paths)){
				byte[] rawData = zkClient.readRawData(paths, true);
				String str=new String(rawData);
				System.out.println("path:"+paths+" >"+str);

			}
		} catch (HedwigException e) {
			e.printStackTrace();
		}

	}
}
