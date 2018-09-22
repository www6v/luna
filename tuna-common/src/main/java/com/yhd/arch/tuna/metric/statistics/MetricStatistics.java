package com.yhd.arch.tuna.metric.statistics;

import com.yhd.arch.tuna.metric.dto.MetricData;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by root on 11/14/16.
 */
public abstract class MetricStatistics implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 链路调用次数**/
	protected AtomicLong linkCounts = new AtomicLong(0);
	/** 成功调用次数**/
	protected AtomicLong successedCounts = new AtomicLong(0);
	/** 失败调用次数**/
	protected AtomicLong failedCounts = new AtomicLong(0);
	/** 因服务端导致失败调用次数**/
	protected AtomicLong serverFailedCounts = new AtomicLong(0);
	/** 因客户端导致失败调用次数**/
	protected AtomicLong clientFailedCounts = new AtomicLong(0);
	/** 因框架导致失败调用次数**/
	protected AtomicLong frameWorkFailedCounts = new AtomicLong(0);

	/** 总耗时**/
	protected AtomicLong totalCostTime = new AtomicLong(0);
	/** 平均耗时**/
	protected int avgCost = 0;
	/** 响应速度快的次数[0,40]*/
	protected AtomicLong fastCounts = new AtomicLong(0);
	/** 响应速度一般的次数 [40,80) */
	protected AtomicLong commonCounts = new AtomicLong(0);
	/**响应速度慢的次数[80,6000000) */
	protected AtomicLong slowCounts = new AtomicLong(0);
	protected int maxCostTime = 0;
	protected int minCostTime = Integer.MAX_VALUE;

	public abstract void merge(MetricAnalyse analyse);

	public void execMerge(int succeed,int cost){
		if (succeed == -1) {
			failedCounts.getAndIncrement();

		} else {
			successedCounts.getAndIncrement();
		}
		linkCounts.getAndIncrement();
		totalCostTime.addAndGet(cost);
		if (costTimeIn(cost, 0, 40)) {
			fastCounts.getAndIncrement();
		} else if (costTimeIn(cost, 40, 80)) {
			commonCounts.getAndIncrement();
		} else {
			slowCounts.getAndIncrement();
		}
		if (cost > maxCostTime) {
			maxCostTime = cost;
		}
		if (cost < minCostTime) {
			minCostTime = cost;
		}
	}

	public void execMerge(MetricAnalyse analyse){
		successedCounts.addAndGet(analyse.getSuccessedCounts().get());
		failedCounts.addAndGet(analyse.getFailedCounts().get());
		linkCounts.addAndGet(analyse.getLinkCounts().get());
		commonCounts.addAndGet(analyse.getCommonCounts().get());
		fastCounts.addAndGet(analyse.getFastCounts().get());
		slowCounts.addAndGet(analyse.getSlowCounts().get());
		maxCostTime+=analyse.getMaxCostTime();
		minCostTime+=analyse.getMinCostTime();
		totalCostTime.addAndGet(analyse.getTotalCostTime().get());
	}

	public void execMetricMerge(int succeed,int cost){
		if (succeed == -1) {
			failedCounts.getAndIncrement();
		} else {
			successedCounts.getAndIncrement();
		}
		totalCostTime.addAndGet(cost);
		if (costTimeIn(cost, 0, 40)) {
			fastCounts.getAndIncrement();
		} else if (costTimeIn(cost, 40, 80)) {
			commonCounts.getAndIncrement();
		} else {
			slowCounts.getAndIncrement();
		}
		if (cost > maxCostTime) {
			maxCostTime = cost;
		}
		if (cost < minCostTime) {
			minCostTime = cost;
		}
	}


	public MetricData toEntity(MetricData analyse) {
	//	String mainPoolId = clientAppCode;
	//	int reciprocalRatio = StatisticsHelper.getReciprocalRatio(mainPoolId);
//		analyse.setAppCode(appCode);
	//	analyse.addAndGetClientFailedCounts(clientFailedCounts.longValue());
	//	analyse.setEndTime(end);
	//	analyse.setExtInfo(fetchJSONExtInfo());
	//	analyse.addAndGetFrameworkFailedCounts(frameWorkFailedCounts.longValue());
		//analyse.setIntervalTime((int) (end.getTime() - begin.getTime()));
	//	analyse.setResultType(0);
	//	analyse.addAndGetServerFailedCounts(serverFailedCounts.longValue());
	//	analyse.setStartTime(begin);
	//	analyse.setStatisticsHost(getLocalIP());
	//	analyse.addAndGetThirdpartyFailedCounts(thirdPartyFailedCounts.longValue());

		analyse.setTotalCount((successedCounts.longValue())  + failedCounts.longValue());
		analyse.setLinkCounts(linkCounts.longValue());
		analyse.setCommonCounts((commonCounts.longValue()) );
		analyse.setFailedCount(failedCounts.longValue());
		analyse.setFastCounts((fastCounts.longValue()) );
		analyse.setSlowCounts((slowCounts.longValue()) );
		analyse.setMaxCostTime(maxCostTime);
		analyse.setMinCostTime(minCostTime);
		analyse.setSucceedCount((successedCounts.longValue()) );

		analyse.setTotalCostTime((totalCostTime.get()) );
		analyse.setAvgCostTime(getAvgCost());
		return analyse;
	}

	public int getAvgCost(){
		long curt = successedCounts.get()+failedCounts.get();
		if (curt != 0) {
			this.avgCost = (int) (totalCostTime.get() / curt);
			return avgCost;
		}
		return 0;
	}

	public void initData(MetricData analyse){
		successedCounts.addAndGet(analyse.getSucceedCount());
		failedCounts.addAndGet(analyse.getFailedCount());
		linkCounts.addAndGet(analyse.getLinkCounts());
		commonCounts.addAndGet(analyse.getCommonCounts());
		fastCounts.addAndGet(analyse.getFastCounts());
		slowCounts.addAndGet(analyse.getSlowCounts());
		maxCostTime+=analyse.getMaxCostTime();
		minCostTime+=analyse.getMinCostTime();
		totalCostTime.addAndGet(analyse.getTotalCostTime());
	}

	private boolean costTimeIn(int value, int min, int max) {
		return (value >= min && value < max);
	}
	public void setAvgCost(int avgCost) {
		this.avgCost = avgCost;
	}

	public AtomicLong getFailedCounts() {
		return failedCounts;
	}

	public AtomicLong getLinkCounts(){return linkCounts;}

	public void setLinkCounts(AtomicLong linkCounts){this.linkCounts=linkCounts;}

	public void setFailedCounts(AtomicLong failedCounts) {
		this.failedCounts = failedCounts;
	}

	public AtomicLong getServerFailedCounts() {
		return serverFailedCounts;
	}

	public void setServerFailedCounts(AtomicLong serverFailedCounts) {
		this.serverFailedCounts = serverFailedCounts;
	}

	public AtomicLong getClientFailedCounts() {
		return clientFailedCounts;
	}

	public void setClientFailedCounts(AtomicLong clientFailedCounts) {
		this.clientFailedCounts = clientFailedCounts;
	}

	public AtomicLong getFrameWorkFailedCounts() {
		return frameWorkFailedCounts;
	}

	public AtomicLong getSuccessedCounts() {
		return successedCounts;
	}

	public void setSuccessedCounts(AtomicLong successedCounts) {
		this.successedCounts = successedCounts;
	}

	public void setFrameWorkFailedCounts(AtomicLong frameWorkFailedCounts) {
		this.frameWorkFailedCounts = frameWorkFailedCounts;
	}

	public AtomicLong getTotalCostTime() {
		return totalCostTime;
	}

	public void setTotalCostTime(AtomicLong totalCostTime) {
		this.totalCostTime = totalCostTime;
	}

	public AtomicLong getFastCounts() {
		return fastCounts;
	}

	public void setFastCounts(AtomicLong fastCounts) {
		this.fastCounts = fastCounts;
	}

	public AtomicLong getCommonCounts() {
		return commonCounts;
	}

	public void setCommonCounts(AtomicLong commonCounts) {
		this.commonCounts = commonCounts;
	}

	public AtomicLong getSlowCounts() {
		return slowCounts;
	}

	public void setSlowCounts(AtomicLong slowCounts) {
		this.slowCounts = slowCounts;
	}

	public int getMaxCostTime() {
		return maxCostTime;
	}

	public void setMaxCostTime(int maxCostTime) {
		this.maxCostTime = maxCostTime;
	}

	public int getMinCostTime() {
		return minCostTime;
	}

	public void setMinCostTime(int minCostTime) {
		this.minCostTime = minCostTime;
	}
}
