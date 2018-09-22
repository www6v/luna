package com.yhd.arch.tuna.metric.dto;

/**
 * Created by root on 2/13/17.
 */
public class MetricViewInfo {
	private long totalTimes = 0L;

	private String successRate = "";

	private String failRate = "";

	private String responseTime = "";

	public void setTotalTimes(long totalTimes) {
		this.totalTimes = totalTimes;
	}

	public long getTotalTimes() {
		return totalTimes;
	}

	public void setSuccessRate(String successRate) {
		this.successRate = successRate;
	}

	public String getSuccessRate() {
		return successRate;
	}

	public void setFailRate(String failRate) {
		this.failRate = failRate;
	}

	public String getFailRate() {
		return failRate;
	}

	public void setResponseTime(String averageTime) {
		this.responseTime = averageTime;
	}

	public String getResponseTime() {
		return responseTime;
	}
}
