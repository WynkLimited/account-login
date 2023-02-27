package com.wynk.db;

import com.wynk.config.RedisHostConfig;
import com.wynk.config.ShardedRedisConfig;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.*;

import java.util.*;

/**
 * Created by Ankit Srivastava 
 * Date - 17/07/2016 
 * Redis service manager with Client side Sharding.
 */
public class ShardedRedisServiceManager {

	private ShardedJedisPool redisPool;

	public ShardedRedisServiceManager(ShardedRedisConfig config) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(config.getMaxActive());
		poolConfig.setMaxIdle(config.getMaxIdle());
		poolConfig.setMaxWaitMillis(1000);
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		ArrayList<RedisHostConfig> redisHosts = config.getRedisHosts();
		for (RedisHostConfig host : redisHosts) {
			JedisShardInfo si = new JedisShardInfo(host.getRedisHost(), host.getRedisPort());
			if (StringUtils.isNotBlank(host.getRedisPassword()))
				si.setPassword(host.getRedisPassword());
			shards.add(si);
		}

		redisPool = new ShardedJedisPool(poolConfig, shards);
	}

	public void shutdown() {
		redisPool.destroy();
	}

	public Set<String> hkeys(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hkeys(key);
		} catch (Exception e) {
			return null;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public List<String> hvals(String field) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hvals(field);
		} catch (Exception e) {
			return null;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public List<String> hmget(String key, String[] fields) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			List<String> values = redis.hmget(key, fields);
			return values;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public void hmset(String key, Map<String, String> fields) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			redis.hmset(key, fields);
			return;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String hget(String key, String field) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			String val = redis.hget(key, field);
			// if(field.endsWith("/s"))
			// System.out.println(System.currentTimeMillis()+" - Using REDISS
			// connection : "+redis+" to get "+val+" for
			// "+field+"."+Thread.currentThread().toString());
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Map<String, String> hgetAll(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hgetAll(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public void hrem(String key, String field) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			redis.hdel(key, field);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long ttl(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.ttl(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long hset(String key, String field, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hset(key, field, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long hdel(String key, String field) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hdel(key, field);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long hsetnx(String key, String field, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hsetnx(key, field, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	/**
	 * Returns number of entries in a given hashmap
	 *
	 * @param mapName
	 * @return
	 */
	public Long hlen(String mapName) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hlen(mapName);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long lpush(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lpush(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long rpush(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.rpush(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long lrem(String key, int count, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lrem(key, count, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public List<String> lrange(String key, long start, long end) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lrange(key, start, end);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long llen(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.llen(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String lpop(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lpop(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long sadd(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sadd(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long srem(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.srem(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long saddWithExpire(String key, String value, int expireAfter) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			long ret = redis.sadd(key, value);
			redis.expire(key, expireAfter);
			return ret;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public void hclear(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			Set<String> fields = redis.hkeys(key);
			for (Iterator<String> fieldsItr = fields.iterator(); fieldsItr.hasNext();) {
				String field = fieldsItr.next();
				redis.hdel(key, field);
			}

		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public boolean hexists(String key, String field) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hexists(key, field);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String setex(String key, String value, int keyExpiryInSeconds) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.setex(key, keyExpiryInSeconds, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String set(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.set(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public boolean sismember(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sismember(key, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String srandmember(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.srandmember(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Set<String> smembers(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.smembers(key);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String get(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			String val = redis.get(key);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String getSet(String key, String value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			String val = redis.getSet(key, value);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long expire(String key, int seconds) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.expire(key, seconds);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long zadd(String key, String value, double sortingParam) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zadd(key, sortingParam, value);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long zadd(String key, Map<String, Double> scoreMembers) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zadd(key, scoreMembers);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long zremrangeByRank(String key, long start, long end) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zremrangeByRank(key, start, end);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Set<String> zrangeByScore(String key, long min, long max) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zrangeByScore(key, min, max);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Set<String> zrange(String key, long start, long stop) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zrange(key, start, stop);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public long zrem(String key, String member) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zrem(key, member);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Double zscore(String key, String member) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zscore(key, member);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long incr(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			Long val = redis.incr(key);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long hincrBy(String hash, String key, int value) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			Long val = redis.hincrBy(hash, key, value);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long zadd(String key, double score, String member) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			Long val = redis.zadd(key, score, member);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public double zincrby(String key, double score, String member) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			double val = redis.zincrby(key, score, member);
			return val;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Set<String> zrevrange(String key, long start, long stop) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zrevrange(key, start, stop);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Set<Double> zrevrangeScoresByScore(String key, double max, double min, int offset, int count) {
		Set<Tuple> zrevrangeByScoreWithScores = zrevrangeByScoreWithScores(key, max, min, offset, count);
		LinkedHashSet<Double> scoresSet = new LinkedHashSet<Double>();
		for (Tuple tuple : zrevrangeByScoreWithScores) {
			scoresSet.add(tuple.getScore());
		}
		return scoresSet;
	}

	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public Long delete(String key) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			Long del = redis.del(key);
			return del;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String ltrim(String key, long start, long end) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.ltrim(key, start, end);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}
	
	public List<Map<String, String>> getInfo() {
		ShardedJedis redis = null;
		List<Map<String, String>> shardInfos = new ArrayList<Map<String, String>>();
		try {
			redis = redisPool.getResource();
			Collection<Jedis> shards = redis.getAllShards();
			for (Jedis shard : shards) {
				String info = shard.info();
				String lines[] = info.split("\n");
				Map<String, String> entries = new HashMap<String, String>();
				for (String line : lines) {
					if (line != null && line.length() > 0) {
						String keyValue[] = line.split(":");
						if (keyValue.length == 2) {
							entries.put(keyValue[0], keyValue[1]);
						}
					}
				}
				shardInfos.add(entries);
			}
			return shardInfos;
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}
	
	public String getShardInfoForKey(String key){
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			JedisShardInfo shardInfo = redis.getShardInfo(key);
			return shardInfo.getHost()+":"+shardInfo.getPort();
			
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public ScanResult<String> sscan(String key, String cursor) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sscan(key, cursor);
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

	public String setIfNotExistsWithExp(String key, String value, int timeInSec) {
		ShardedJedis redis = null;
		try {
			redis = redisPool.getResource();
			String val = redis.get(key);
			if (val == null) {
				return redis.setex(key, timeInSec, value);
			} else {
				return null;
			}
		} finally {
			if (redis != null) {
				redis.close();
			}
		}
	}

}
