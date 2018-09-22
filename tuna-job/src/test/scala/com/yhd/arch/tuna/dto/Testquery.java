package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.*;

import java.util.List;
import java.util.Date;
/**
 * Created by root on 8/17/16.
 */
@Entity(value = "methodd", noClassnameStored = true)
@Indexes(@Index(fields = {@Field("strTime")},options = @IndexOptions(unique = true)))
public class Testquery {
    @Id
    private String id;

    @Embedded
    private List<TestR> proxyList;

    private String key;

    private Integer calledCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCalledCount(Integer calledCount){
        this.calledCount=calledCount;
    }
    public Integer getCalledCount(){
        return calledCount;
    }

    public void setKey(String key){
        this.key=key;
    }

    public String getKey(){
        return key;
    }

    public void setProxyList(List<TestR> list){
        this.proxyList=list;
    }

    public List<TestR> getProxyList(){
        return proxyList;
    }

    public Date gmtCreate;

    public Long strTime;
}
