package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by root on 8/16/16.
 */
@Entity(value = "methodDependence", noClassnameStored = true)
public class MethodDependence implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Property("links")
    private String[] links;

    @Property("services")
    private String[] services;

    @Property("groups")
    private String[] groups;

    @Property("calledCounts")
    private Integer calledCounts;

    @Property("curtLayer")
    private Integer curtLayer;

    @Property("gmtCreate")
    private Date gmtCreate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public String[] getServices() {
        return services;
    }

    public void setServices(String[] services) {
        this.services = services;
    }
    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    public Integer getCalledCounts() {
        return calledCounts;
    }

    public void setCalledCounts(Integer calledCounts) {
        this.calledCounts = calledCounts;
    }

    public Integer getCurtLayer() {
        return curtLayer;
    }

    public void setCurtLayer(Integer curtLayer) {
        this.curtLayer = curtLayer;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date time) {
        this.gmtCreate = time;
    }
}