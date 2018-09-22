package com.yhd.arch.tuna.metric.dto;

import com.yhd.arch.tuna.metric.dto.MetricData;

/**
 * Created by root on 11/15/16.
 */
public class SpanMetricAnalyse extends MetricData {
	private String linkId;

	private String spanId;

	private String serviceGroup;

	private String clientMethodName;

	private String serviceMethodName;

	private String callApp;

	private String callHost;

	private String serviceName;

	private String providerApp;

	private String providerHost;

	private long reqTime;

	private long respTime;

	private int curtLayer;

	private long gmtCreate;

	private String serviceVersion;

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String LinkId) {
		this.linkId = LinkId;
	}


	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getServiceMethodName() {
		return serviceMethodName;
	}

	public void setServiceMethodName(String serviceMethodName) {
		this.serviceMethodName = serviceMethodName;
	}

	public String getCallApp() {
		return callApp;
	}

	public void setCallApp(String callApp) {
		this.callApp = callApp;
	}

	public String getCallHost() {
		return callHost;
	}

	public void setCallHost(String callHost) {
		this.callHost = callHost;
	}

	public String getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = serviceGroup;
	}


	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getClientMethodName() {
		return  clientMethodName;
	}

	public void setClientMethodName(String methodName) {
		this.clientMethodName = methodName;
	}

	public String getProviderApp() {
		return  providerApp;
	}

	public void setProviderApp(String providerApp) {
		this.providerApp = providerApp;
	}

	public String getProviderHost() {
		return providerHost;
	}

	public void setProviderHost(String providerHost) {
		this.providerHost = providerHost;
	}

	public void setReqTime(Long time){
		this.reqTime=time;
	}

	public Long getReqTime(){
		return reqTime;
	}

	public void setGmtCreate(Long time){
		this.gmtCreate=time;
	}

	public Long getGmtCreate(){
		return gmtCreate;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setRespTime(Long time){
		this.respTime=time;
	}

	public Long getRespTime(){
		return respTime;
	}

	public void setCurtLayer(int curtLayer){
		this.curtLayer=curtLayer;
	}

	public int getCurtLayer(){
		return curtLayer;
	}

}
