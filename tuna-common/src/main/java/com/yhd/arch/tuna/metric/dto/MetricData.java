package com.yhd.arch.tuna.metric.dto;

/**
 * Created by root on 11/14/16.
 */
public class MetricData {
	private long totalCount;

	private long succeedCount;

	private long failedCount;

	private long totalCostTime;

	private long avgCostTime;

	private  int maxCostTime = 0;
	private int minCostTime = 0;

	private long fastCounts;
	private long commonCounts;

	private long slowCounts;

	private long linkCounts;
	public void setTotalCount(long count){
		this.totalCount=count;
	}
	public long getTotalCount(){
		return totalCount;
	}

	public void setSucceedCount(long succeedCount){
		this.succeedCount=succeedCount;
	}
	public long getSucceedCount(){
		return succeedCount;
	}
	public void setFailedCount(long failedCount){
		this.failedCount=failedCount;
	}
	public long getFailedCount(){
		return failedCount;
	}

	public void setTotalCostTime(long time){
		this.totalCostTime=time;
	}
	public long getTotalCostTime(){
		return totalCostTime;
	}

	public void setAvgCostTime(long avgCostTime){
		this.avgCostTime=avgCostTime;
	}
	public long getAvgCostTime(){
		return avgCostTime;
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


	public void setFastCounts(long fastCounts){
		this.fastCounts=fastCounts;
	}
	public long getFastCounts(){
		return fastCounts;
	}
	public void setCommonCounts(long commonCounts){
		this.commonCounts=commonCounts;
	}
	public long getCommonCounts(){
		return commonCounts;
	}

	public void setLinkCounts(long linkCounts){
		this.linkCounts=linkCounts;
	}
	public long getLinkCounts(){
		return linkCounts;
	}

	public void setSlowCounts(long slowCounts){
		this.slowCounts=slowCounts;
	}
	public long getSlowCounts(){
		return slowCounts;
	}

}
