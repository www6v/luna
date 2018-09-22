package com.yhd.arch.tuna.constants;

/**
 * Created by root on 2/13/17.
 * 存储redis数据key类型枚举
 */
public enum LinkKeyType {
	DEPENDENCY_SUFFIX_KEY("DEPENDENCY_SUFFIX_KEY","POOL下层依赖关系key"),

	CALLERDEPENDENCY_SUFFIX_KEY("CALLERDEPENDENCY_SUFFIX_KEY","POOL上层依赖关系key"),

	_LINK_KEY("linkdata_key","调用链路关系key"),

	_ERRORINFO_INDEX_KEY("errorInfo_index_key","错误链路索引key"),

	ERROR_MATCH_KEY("ERROR_MATCH_KEY","错误日志关联key"),

	_SPARNID_INDEX_KEY("spanid_index_key","spanid索引key");

	private String code;
	private String desc;

	public String getDesc() {
		return desc;
	}
	public String getCode() {
		return code;
	}
	LinkKeyType(String code,String desc){
		this.code = code;
		this.desc = desc;
	}
}
