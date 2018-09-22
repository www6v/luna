package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by root on 6/8/16.
 */
@Entity(value = "methodAnalyse", noClassnameStored = true)
public class MethodAnalyseAnnotation implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Property("appCode")
    private String appCode;

    @Property("serviceName")
    private String serviceName;

    @Property("clientAppCode")
    private String clientAppCode;

    @Property("clientMethodName")
    private String clientMethodName;

    @Property("serviceMethodName")
    private String serviceMethodName;

    @Property("serviceGroup")
    private String serviceGroup;

    @Property("serviceVersion")
    private String serviceVersion;

    @Property("providerHost")
    private String providerHost;

    @Property("calledCounts")
    private Long calledCounts;

    @Property("failedCounts")
    private Long failedCounts;

    @Property("totalCostTime")
    private Long totalCostTime;

    @Property("avgCost")
    private Integer avgCost;

    @Property("startTime")
    private Date startTime;

    @Property("endTime")
    private Date endTime;

    @Property("successedCounts")
    private Long successedCounts;

    @Property("gmtCreate")
    private Date gmtCreate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getClientAppCode() {
        return clientAppCode;
    }

    public void setClientAppCode(String clientAppCode) {
        this.clientAppCode = clientAppCode;
    }

    public String getClientMethodName() {
        return clientMethodName;
    }

    public void setClientMethodName(String clientMethodName) {
        this.clientMethodName = clientMethodName;
    }

    public String getServiceMethodName() {
        return serviceMethodName;
    }

    public void setServiceMethodName(String serviceMethodName) {
        this.serviceMethodName = serviceMethodName;
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

    public String getProviderHost() {
        return providerHost;
    }

    public void setProviderHost(String providerHost) {
        this.providerHost = providerHost;
    }

    public Long getCalledCounts() {
        return calledCounts;
    }

    public void setCalledCounts(Long calledCounts) {
        this.calledCounts = calledCounts;
    }

    public Long getFailedCounts() {
        return failedCounts;
    }

    public void setFailedCounts(Long failedCounts) {
        this.failedCounts = failedCounts;
    }

    public Long getTotalCostTime() {
        return totalCostTime;
    }

    public void setTotalCostTime(Long totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    public Integer getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(Integer avgCost) {
        this.avgCost = avgCost;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getSuccessedCounts() {
        return successedCounts;
    }

    public void setSuccessedCounts(Long successedCounts) {
        this.successedCounts = successedCounts;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
