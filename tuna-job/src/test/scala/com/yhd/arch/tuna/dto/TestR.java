package com.yhd.arch.tuna.dto;

import com.google.code.morphia.annotations.Embedded;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 8/17/16.
 */
@Embedded
public class TestR implements Serializable {
    public String host;
    public String name;
   /// public Map<String,Object> host = new HashMap<>();
}
