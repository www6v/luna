package com.yhd.arch.tuna.link.dto;

/**
 * Created by root on 2/13/17.
 */
public class LinkPVWDto {
	private String linkId;

	private int curtLayer;

	private long linkcounts;

	private double successRate;

	private String callerApp;

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setCallerApp(String callerApp) {
		this.callerApp = callerApp;
	}

	public String getCallerApp() {
		return callerApp;
	}

	public void setCurtLayer(int curtLayer) {
		this.curtLayer = curtLayer;
	}

	public int getCurtLayer() {
		return curtLayer;
	}

	public void setLinkcounts(long linkcounts) {
		this.linkcounts = linkcounts;
	}

	public long getLinkcounts() {
		return linkcounts;
	}

	public void setSuccessRate(double successRate) {
		this.successRate = successRate;
	}

	public double getSuccessRate() {
		return successRate;
	}
}
