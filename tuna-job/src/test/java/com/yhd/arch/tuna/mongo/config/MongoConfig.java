package com.yhd.arch.tuna.mongo.config;

import com.yihaodian.configcentre.client.utils.YccGlobalPropertyConfigurer;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by root on 5/16/16.
 */
public class MongoConfig {
    private static final Logger logger = Logger.getLogger(MongoConfig.class);
    private static Properties props;
    private boolean socketKeepAlive;
    private int socketTimeout;
    private int connectionsPerHost;
    private int threadsAllowedToBlockForConnectionMultiplier;
    private int w;
    private int wtimeout;
    private boolean fsync;
    private int connectTimeout;
    private int maxWaitTime;
    private boolean autoConnectRetry;
    private boolean safe;
    private boolean j;
    private String userName;
    private String passWord;
    private String mongoUri;


    static {
        System.setProperty("global.config.path", "/var/www/webapps/config");
    }

    public MongoConfig() {
        super();
        socketKeepAlive = true;
        socketTimeout = 2000;
        connectionsPerHost = 30;
        threadsAllowedToBlockForConnectionMultiplier = 50;
        w = 0;
        wtimeout = 2000;
        fsync = false;
        connectTimeout = 2000;
        maxWaitTime = 2000;
        autoConnectRetry = true;
        safe = true;
        j = false;
        userName = "jumper";
        passWord = "jumper123";
    }

    public static String getProperty(String key){
        String value = null;
        if(props == null){
            try{
                //yihaodian_detector, tuna.mongo.properties need be replaced by constants
                props = YccGlobalPropertyConfigurer.loadConfigProperties("yihaodian_detector", "tuna.mongo.properties", false);
                System.out.println("props:" + props);
            }catch (Exception e){
                System.out.println("exception:"+e.getMessage());
                logger.error("load config error.", e);
            }
        }
        value = props.getProperty(key);
        return value;
    }

    public boolean isSocketKeepAlive() {
        return this.socketKeepAlive;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public int getConnectionsPerHost() {
        return this.connectionsPerHost;
    }

    public int getThreadsAllowedToBlockForConnectionMultiplier() {
        return this.threadsAllowedToBlockForConnectionMultiplier;
    }

    public int getW() {
        return this.w;
    }

    public int getWtimeout() {
        return this.wtimeout;
    }

    public boolean isFsync() {
        return this.fsync;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getMaxWaitTime() {
        return this.maxWaitTime;
    }

    public boolean isAutoConnectRetry() {
        return this.autoConnectRetry;
    }

    public boolean isSafe() {
        return this.safe;
    }

    public boolean isJ() {
        return this.j;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public String getMongoUri(){
        return mongoUri;
    }
}
