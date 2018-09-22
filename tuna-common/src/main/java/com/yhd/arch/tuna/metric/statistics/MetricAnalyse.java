package com.yhd.arch.tuna.metric.statistics;

import com.yhd.arch.tuna.metric.dto.SpanMetricAnalyse;

/**
 * Created by root on 11/14/16.
 */
public class MetricAnalyse extends MetricStatistics {
	/**
	 * 服务分组
	 */
	private String serviceGroup;

	private String clientMethodName;

	private String serviceMethodName;

	private String callApp;

	private String callHost;

	private String serviceName;

	private String providerApp;

	private String providerHost;

	private String serviceVersion;

	private String parentId;

	private String curtLogId;

	private long reqTime;

	private long respTime;

	private int curtLayer;

	private int succeed;

	private int costTime;

	private String spanId;


	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getCurtLogId() {
		return curtLogId;
	}

	public void setCurtLogId(String curtLogId) {
		this.curtLogId = curtLogId;
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


	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
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

	public void setSucceed(int succeed){
		this.succeed=succeed;
	}

	public int getSucceed(){
		return succeed;
	}

	public void setCostTime(int costTime){
		this.costTime=costTime;
	}

	public int getCostTime(){
		return costTime;
	}

	public void merge(MetricAnalyse analyse){
		int succeed=analyse.getSucceed();
		int cost=analyse.getCostTime();
		if(cost==0&&clientMethodName==null){
			execMerge(analyse);
		}else {
			execMerge(succeed, cost);
		}
	}

	public void metricMerge(MetricAnalyse analyse){
		int succeed=analyse.getSucceed();
		int cost=analyse.getCostTime();
		execMetricMerge(succeed,cost);
	}

	public SpanMetricAnalyse toEntity(SpanMetricAnalyse data){
		super.toEntity(data);
		data.setCallApp(callApp);
		data.setProviderApp(providerApp);
		data.setServiceGroup(serviceGroup);
		data.setServiceMethodName(serviceMethodName);
		data.setServiceName(serviceName);
		data.setCurtLayer(curtLayer);
		data.setServiceVersion(serviceVersion);
		return data;
	}

	public void initData(SpanMetricAnalyse data){
		super.initData(data);
		setCallApp(data.getCallApp());
		setProviderApp(data.getProviderApp());
	//	setClientMethodName(data.getClientMethodName());
		setCurtLayer(data.getCurtLayer());
		setServiceGroup(data.getServiceGroup());
		setServiceName(data.getServiceName());
		setServiceMethodName(data.getServiceMethodName());
		setServiceVersion(data.getServiceVersion());
		setSpanId(data.getSpanId());

	}
}
