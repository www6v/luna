package com.yhd.arch.tuna.mongo.util;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.yhd.arch.tuna.mongo.config.MongoConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 5/16/16.
 */
public class MongoUtil {

    private static final Logger logger = Logger.getLogger(MongoUtil.class);
    private static final MongoUtil mongoUtil = new MongoUtil();
    private static MongoConfig config;
    private static MongoClient mongoClient;

    private MongoUtil(){
        config = new MongoConfig();
        if(mongoClient == null){
            try{
                List<ServerAddress> addressList = buildMongoUri();
                mongoClient = getMongoClient(addressList);
            }catch (Exception e){
                logger.error("new mongoClient failed.", e);
            }
        }

    }

    public static MongoClient getMongoClient(){
        return mongoClient;
    }

    public static MongoUtil getMongoUtil(){
        return mongoUtil;
    }

    private static MongoClientOptions getMongoClientOptions(MongoConfig config){
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(config.getConnectionsPerHost());
        builder.socketKeepAlive(config.isSocketKeepAlive());
        builder.socketTimeout(config.getSocketTimeout());
        builder.threadsAllowedToBlockForConnectionMultiplier(config.getThreadsAllowedToBlockForConnectionMultiplier());
        builder.connectTimeout(config.getConnectTimeout());
        MongoClientOptions options = builder.build();
        return options;
    }

    private static MongoClient getMongoClient(List<ServerAddress> addressList){
        MongoClientOptions mongoClientOptions = getMongoClientOptions(config);
        char[] pwd_char = config.getPassWord().toCharArray();
        MongoCredential credential = MongoCredential.createCredential(config.getUserName(), "admin", pwd_char);
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        credentialList.add(credential);
        MongoClient mongoClient = new MongoClient(addressList, credentialList, mongoClientOptions);
        return mongoClient;
    }

    private static List<ServerAddress> buildMongoUri(){
        String mongoUri = config.getMongoUri();
        if(mongoUri == null || StringUtils.isEmpty(mongoUri)){
            mongoUri = MongoConfig.getProperty("mongoUri");
        }
        System.out.println("mongoUri:"+mongoUri);
        List<ServerAddress> addressList = new ArrayList<ServerAddress>();
        if(mongoUri != null && !StringUtils.isEmpty(mongoUri)){
            String[] hostPortArr = mongoUri.split(",");
            for(String hostPort : hostPortArr){
                String[] pair = hostPort.split(":");
                try{
                    addressList.add(new ServerAddress(pair[0].trim(), Integer.parseInt(pair[1].trim())));
                }catch (Exception e){
                    logger.error("the mongoUri is bad format, please check.", e);
                }
            }
        }
        return addressList;
    }
}
