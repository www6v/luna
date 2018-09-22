package com.yhd.arch.tuna.linktree.dto;

import com.google.code.morphia.annotations.*;

import java.io.Serializable;
import java.util.List;
/**
 * Created by root on 8/17/16.
 */
@Entity(value = "linkss",noClassnameStored = true,cap=@CappedAt(count=5,value=4000))
@Indexes(@Index(fields = {@Field("key"),@Field("gmtCreate")},options = @IndexOptions(unique = true)))
public class LinkAnalyse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Embedded
    private List<MethodAnalyse> linkList;

    private Integer curtLayer;

    private Integer calledCounts;

    @Property("gmtCreate")
    private Long gmtCreate;

    @Property("reqTime")
    private Long reqTime;

    private String key;

    public void setKey(String key){
        this.key=key;
    }

    public String getKey(){
        return key;
    }

    public void setLinkList(List<MethodAnalyse> list){
        this.linkList=list;
    }

    public List<MethodAnalyse> getLinkList(){
        return linkList;
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

//    public Date getGmtCreate() {
//        return gmtCreate;
//    }

//    public void setGmtCreate(Date time) {
//        this.gmtCreate = time;
//    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long time) {
        this.gmtCreate = time;
    }

    public Long getReqTime() {
        return reqTime;
    }

    public void setReqTime(Long time) {
        this.reqTime = time;
    }
}
