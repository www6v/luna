package com.yhd.arch.tuna.linktree.dto;

import com.google.code.morphia.annotations.Embedded;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by root on 8/17/16.
 */
public class MethodAnalyse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 服务分组
     */
    protected String serviceGroup;

    protected String clientMethodName;

    protected String serviceMethodName;

    protected String callApp;

    protected String callHost;

    protected String serviceName;

    protected String providerApp;

    protected String providerHost;

    protected long reqTime;

    protected long respTime;

    protected int curtLayer;

    protected int succeed;

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

}
