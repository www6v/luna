package com.yhd.arch.tuna.link.dto;

/**
 * Created by root on 2/13/17.
 */
public class NodeInfo {
	private String serviceGroups;

	private String methodName;

	private String poolId;

	private String serviceName;

	private String version;

	public void setServiceGroups(String groupNames) {
		this.serviceGroups = groupNames;
	}

	public String getServiceGroups() {
		return serviceGroups;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public String getPoolId() {
		return poolId;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
