package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by root on 6/12/16.
 */
@Entity(value = "appDependence", noClassnameStored = true)
public class AppDependenceAnnotation implements Serializable{
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

    @Property("gmtCreate")
    private Date gmtCreate;

    @Property("gmtModify")
    private Date gmtModify;

    @Property("memo")
    private String memo;

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

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
