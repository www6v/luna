package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by root on 6/12/16.
 */
@Entity(value = "poolDependence", noClassnameStored = true)
public class PoolDependenceAnnotation implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Property("imports")
    private String[] imports;

    @Property("name")
    private String name;

    @Property("size")
    private Integer size;

    @Property("time")
    private Date time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getImports() {
        return imports;
    }

    public void setImports(String[] imports) {
        this.imports = imports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
