package com.yhd.arch.tuna.link.dto;

import java.util.List;

/**
 * Created by root on 2/13/17.
 */
public class TreeNodeInfo {
	private NodeInfo nodeInfo;

	private List<TreeNodeInfo> childrenList;

	private String parentName = null;

	private String spanId = null;

	private String parentId = null;

	private Integer curtLayer = 0;

	public void setNodeInfo(NodeInfo node) {
		this.nodeInfo = node;
	}

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	public void setChildrenList(List<TreeNodeInfo> list) {
		this.childrenList = list;
	}

	public List<TreeNodeInfo> getChildrenList() {
		return childrenList;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setSpanId(String spanid) {
		this.spanId = spanid;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setCurtLayer(Integer curtLayer) {
		this.curtLayer = curtLayer;
	}

	public Integer getCurtLayer() {
		return curtLayer;
	}
}