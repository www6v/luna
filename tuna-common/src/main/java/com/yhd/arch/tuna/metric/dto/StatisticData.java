package com.yhd.arch.tuna.metric.dto;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by root on 2/8/17.
 */
public class StatisticData implements Serializable{

	private  String metric;
	private long timestamp;
	private String value;
	private Map<String,String> tags;

	private String valueType;

	public StatisticData(){

	}
	public StatisticData(String metric, long timestamp, long value, Map<String, String> tags){
		this.metric = metric;
		this.timestamp = timestamp;
		this.value  = String.valueOf(value);
		this.tags = tags;
		this.valueType="LONG";
	}

	public StatisticData(String metric, long timestamp, float value, Map<String, String> tags){
		this.metric = metric;
		this.timestamp = timestamp;
		this.value  = String.valueOf(value);
		this.tags = tags;
		this.valueType="FLOAT";
	}
	public StatisticData(String metric, long timestamp, double value, Map<String, String> tags){
		this.metric = metric;
		this.timestamp = timestamp;
		this.value  = String.valueOf(value);
		this.tags = tags;

		this.valueType="DOUBLE";
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public long longValue(){
		return Long.valueOf(value);
	}
	public double doubleValue(){
		return Double.valueOf(value);
	}

	public float floatValue(){
		return Float.valueOf(value);
	}

	@Override
	public String toString(){
		JSONObject json = new JSONObject();
		json.put("metric", metric);
		json.put("timestamp",timestamp );
		json.put("value",value );
		json.put("tags", tags );
		json.put("valueType", valueType);
		return json.toString();
	}
}
