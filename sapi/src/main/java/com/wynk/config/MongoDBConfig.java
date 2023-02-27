package com.wynk.config;

public class MongoDBConfig {

    public static final String PHOTO_DB_KEY = "galleries";
    public static final String NEWS_DB_KEY = "news";
    public static final String STAR_PROFILE_DB_KEY = "profiles";
    public static final String MONITORING = "monitoring";
    public static final String PHOTO_STATS_KEY = "photostats";
    public static final String TOP_TEN_KEY = "topten";

    public String getMongoHosts() {
        return mongoHosts;
    }

    public void setMongoHosts(String mongoHosts) {
        this.mongoHosts = mongoHosts;
    }

    // mongo db properties
    private String mongoHosts;
    private String mongodbHost;
    private int mongodbPort;
    private String mongodbHostSlave;
    private int mongodbPortSlave;
    private String mongodbHostArbiter;
    private int mongodbPortArbiter;
    private String mongoDBName;
    private String mongoDBPrefix;
    private Boolean mongodbLoggingEnabled;
    private boolean readPrimary;
    
    private int mongodbThreadsAllowedToBlock;
    private int mongodbConnectionsPerHost;
    
    public boolean isReadPrimary() {
        return readPrimary;
    }
    
    public void setReadPrimary(boolean readPrimary) {
        this.readPrimary = readPrimary;
    }

    public String getMongodbHostSlave() {
        return mongodbHostSlave;
    }
    
    public void setMongodbHostSlave(String mongodbHostSlave) {
        this.mongodbHostSlave = mongodbHostSlave;
    }
    
    public int getMongodbPortSlave() {
        return mongodbPortSlave;
    }
    
    public void setMongodbPortSlave(int mongodbPortSlave) {
        this.mongodbPortSlave = mongodbPortSlave;
    }
    
    public String getMongodbHostArbiter() {
        return mongodbHostArbiter;
    }
    
    public void setMongodbHostArbiter(String mongodbHostArbiter) {
        this.mongodbHostArbiter = mongodbHostArbiter;
    }
    
    public int getMongodbPortArbiter() {
        return mongodbPortArbiter;
    }
    
    public void setMongodbPortArbiter(int mongodbPortArbiter) {
        this.mongodbPortArbiter = mongodbPortArbiter;
    }

    public String getMongodbHost() {
        return mongodbHost;
    }

    public void setMongodbHost(String mongodbHost) {
        this.mongodbHost = mongodbHost;
    }

    public int getMongodbPort() {
        return mongodbPort;
    }

    public void setMongodbPort(int mongodbPort) {
        this.mongodbPort = mongodbPort;
    }

    public String getMongoDBPrefix() {
        return mongoDBPrefix;
    }

    public void setMongoDBPrefix(String mongoDBPrefix) {
        this.mongoDBPrefix = mongoDBPrefix;
    }

    public String getMongoDBName() {
        return mongoDBName;
    }

    public void setMongoDBName(String mongoDBName) {
        this.mongoDBName = mongoDBName;
    }
    
    public Boolean getMongodbLoggingEnabled() {
        return mongodbLoggingEnabled;
    }
    
    public void setMongodbLoggingEnabled(Boolean mongodbLoggingEnabled) {
        this.mongodbLoggingEnabled = mongodbLoggingEnabled;
    }
    
    public int getMongodbThreadsAllowedToBlock() {
        return mongodbThreadsAllowedToBlock;
    }
    
    public void setMongodbThreadsAllowedToBlock(int mongodbThreadsAllowedToBlock) {
        this.mongodbThreadsAllowedToBlock = mongodbThreadsAllowedToBlock;
    }
    
    public int getMongodbConnectionsPerHost() {
        return mongodbConnectionsPerHost;
    }
    
    public void setMongodbConnectionsPerHost(int mongodbConnectionsPerHost) {
        this.mongodbConnectionsPerHost = mongodbConnectionsPerHost;
    }
}
