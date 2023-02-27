package com.wynk.config;

import java.util.ArrayList;

public class ShardedRedisConfig {

	private ArrayList<RedisHostConfig> redisHosts;

	// genericObject pool has 8 as default, so keeping same value
	private int maxActive = 8;
	private int maxIdle = 8;

	public ArrayList<RedisHostConfig> getRedisHosts() {
		return redisHosts;
	}

	public void setRedisHosts(ArrayList<RedisHostConfig> redisHosts) {
		this.redisHosts = redisHosts;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActiveConnection) {
		this.maxActive = maxActiveConnection;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdleConnection) {
		this.maxIdle = maxIdleConnection;
	}
}
