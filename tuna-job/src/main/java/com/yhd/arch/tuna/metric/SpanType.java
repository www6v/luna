package com.yhd.arch.tuna.metric;

/**
 * Created by root on 10/8/16.
 */
public enum SpanType {
    SQL("sql","sql",1),
    CACHE("cache","cache",2),
    HTTP("http","http",3),  // CALLER
    LOG("log","log",4),
    METRIC("metric","metric",5),
    OSP("osp","osp",6),  // CALLER
    HTTP_SERVER("http_server","http_server",7),  // TARGET
    OSP_SERVER("osp_server","osp_server",8), // TARGET


    REDIS("redis","redis",9),
    KAFKA("kafka","kafka",10),
    RABBITMQ("rabbitmq","rabbitmq",11),
    METHOD("method","method",12),
    THRIFTCLIENT("thriftclient","thriftclient",13),  // CALLER
    THRIFTSERVER("thriftserver","thriftserver",14),  // TARGET
    KV("kv","kv",15),
    MEMCACHED("memcached","memcached",16),
    HBASE("hbase","hbase",17),


    OSP_PROXY("osp_proxy", "osp_proxy", 19),
    ROOT_METHOD("root_method", "root_method", 20), // TARGET

    HEDIG_CLIENT("hedwig_client","hedwig_client", 21), ///
    HEDIG_SERVER("hedwig_server","hedwig_server", 22); ///

    private String spanType;
    private String desc;
    private Integer value;

    SpanType(String spanType, String desc, Integer value) {
        this.spanType = spanType;
        this.desc = desc;
        this.value = value;
    }

    public String getSpanType() {
        return spanType;
    }


    public void setSpanType(String spanType) {
        this.spanType = spanType;
    }


    public String getDesc() {
        return desc;
    }

    public Integer getValue() {
        return value;
    }


    public static SpanType getEnum(String spanType) {
        for (SpanType orderStatus : SpanType.values()) {
            if (orderStatus.getSpanType().equalsIgnoreCase(spanType)) {
                return orderStatus;
            }
        }

        return null;
    }

    public static SpanType findByValue(int value){
        switch(value){
            case 1: return SQL;
            case 2: return CACHE;
            case 3: return HTTP;
            case 4: return LOG;
            case 5: return METRIC;
            case 6: return OSP;
            case 7: return HTTP_SERVER;
            case 20: return ROOT_METHOD;

            case 21: return HEDIG_CLIENT; ///
            case 22: return HEDIG_SERVER; ///

            default: return null;
        }
    }
}

