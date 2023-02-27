package com.wynk.newcode.user.core.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.ConsistencyLevel;
import org.springframework.cassandra.core.ConsistencyLevelResolver;
import org.springframework.cassandra.core.QueryOptions;
import org.springframework.cassandra.core.RetryPolicy;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.cassandra.core.WriteOptions;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;

@Repository
public class CassandraDao<T> {

	private static final Logger logger = LoggerFactory.getLogger(CassandraDao.class);

	@Autowired
	private CassandraOperations cassandraTemplate;

	@Autowired
	private CassandraConverter converter;

	private ConsistencyLevel consistencyLevel;

	@Value("${cassandra.consistency.level}")
	public void setConsistencyLevel(String _consistencyLevel) {

		consistencyLevel = ConsistencyLevel.valueOf(_consistencyLevel);
		logger.info("CASSANDRA RUNNING ON ConsistencyLevel {}", _consistencyLevel);
	}

	private List<T> select(String query, final Class<T> clazz, QueryOptions queryOptions) {
		List<T> records = cassandraTemplate.query(query, new RowMapper<T>() {

			@Override
			public T mapRow(Row row, int rowNum) throws DriverException {
				return converter.read(clazz, row);
			}

		}, queryOptions);
		return records;

	}

	private T selectOne(String query, final Class<T> clazz, QueryOptions queryOptions) {
		T record = null;
		List<T> records = cassandraTemplate.query(query, new RowMapper<T>() {

			@Override
			public T mapRow(Row row, int rowNum) throws DriverException {
				return converter.read(clazz, row);
			}

		}, queryOptions);
		if (CollectionUtils.isNotEmpty(records)) {
			record = records.get(0);
		}
		return record;

	}

	public List<T> select(String query, final Class<T> clazz) {
		QueryOptions queryOptions = new QueryOptions(consistencyLevel, RetryPolicy.DEFAULT);
		return select(query, clazz, queryOptions);
	}

	public T selectOne(String query, final Class<T> clazz) {
		QueryOptions queryOptions = new QueryOptions(consistencyLevel, RetryPolicy.DEFAULT);
		return selectOne(query, clazz, queryOptions);
	}

	public void execute(Update update) {
		try {
			update.setConsistencyLevel(ConsistencyLevelResolver.resolve(consistencyLevel));
			cassandraTemplate.execute(update);
		} catch (Throwable th) {
			logger.error("Error while updating records, update: {}", update.getQueryString(), th);
			throw th;
		}
	}

	public T insert(T object) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setConsistencyLevel(consistencyLevel);
		return cassandraTemplate.insert(object, writeOptions);

	}

	/**
	 * The TTL value for a column is a number of seconds. After the number of
	 * seconds since the column's creation exceeds the TTL value, TTL data is
	 * considered expired and is included in results. Expired data is marked
	 * with a tombstone after on the next read on the read path, but it remains
	 * for a maximum of gc_grace_seconds. After this amount of time, the
	 * tombstoned data is automatically removed during the normal compaction and
	 * repair processes.
	 * 
	 * @param object
	 * @param ttl
	 * @return
	 */
	public T insert(T object, int ttl) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setConsistencyLevel(consistencyLevel);
		writeOptions.setTtl(ttl);
		return cassandraTemplate.insert(object, writeOptions);

	}

	public List<T> insert(List<T> list) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setConsistencyLevel(consistencyLevel);
		return cassandraTemplate.insert(list, writeOptions);

	}
	public List<T> update(List<T> list) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setConsistencyLevel(consistencyLevel);
		return cassandraTemplate.update(list, writeOptions);

	}
	public List<T> insert(List<T> list, int ttl) {
		WriteOptions writeOptions = new WriteOptions();
		writeOptions.setConsistencyLevel(consistencyLevel);
		writeOptions.setTtl(ttl);
		return cassandraTemplate.insert(list, writeOptions);

	}

	public void execute(String query) {
		try {
			QueryOptions queryOptions = new QueryOptions(consistencyLevel, RetryPolicy.DEFAULT);
			cassandraTemplate.execute(query, queryOptions);
		} catch (Throwable th) {
			logger.error("Error while executing query {} ", query, th);
			throw th;
		}
	}

	public void delete(T entity) {

		try {
			Table tabel = entity.getClass().getAnnotation(Table.class);
			Delete delete = QueryBuilder.delete().from(tabel.value());
			delete.setConsistencyLevel(com.datastax.driver.core.ConsistencyLevel.QUORUM);
			Map<String, String> map = getTableKeyMap(entity.getClass());

			for (Map.Entry<String, String> entry : map.entrySet()) {
				delete.where(QueryBuilder.eq(entry.getKey(), getPropertyValue(entity, entry.getValue())));
			}
			cassandraTemplate.execute(delete);
		} catch (Throwable th) {
			logger.error("Error while deleting {} ", entity, th);
			throw th;
		}
	}

	public void delete(List<T> entities) {
		if (CollectionUtils.isNotEmpty(entities)) {
			try {
				Batch batch = null;
				for (T entity : entities) {
					Table tabel = entity.getClass().getAnnotation(Table.class);
					Delete delete = QueryBuilder.delete().from(tabel.value());
					delete.setConsistencyLevel(com.datastax.driver.core.ConsistencyLevel.QUORUM);
					Map<String, String> map = getTableKeyMap(entity.getClass());

					for (Map.Entry<String, String> entry : map.entrySet()) {
						delete.where(QueryBuilder.eq(entry.getKey(), getPropertyValue(entity, entry.getValue())));
					}
					if (batch == null) {
						batch = QueryBuilder.batch(delete);
					} else {
						batch.add(delete);
					}
				}
				cassandraTemplate.execute(batch);
			} catch (Throwable th) {
				logger.error("Error while deleting {} ", entities, th);
				throw th;
			}
		}
	}

	public static <T> Map<String, String> getTableKeyMap(Class<T> typedClass) {

		Map<String, String> map = new HashMap<String, String>();

		if (typedClass != null) {
			Field[] fields = typedClass.getDeclaredFields();

			for (Field field : fields) {

				try {
					String dbName = null;

					field.setAccessible(true);
					PrimaryKeyColumn pkc = field.getAnnotation(PrimaryKeyColumn.class);

					if (pkc != null) {
						dbName = (pkc.name() != null) ? pkc.name() : field.getName();
						map.put(dbName, field.getName());
					}
					PrimaryKey pk = field.getAnnotation(PrimaryKey.class);

					if (pk != null) {
						dbName = (pk.value() != null) ? pk.value() : field.getName();
						map.put(dbName, field.getName());
					}

				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

		}
		return map;
	}

	public static <T> Object getPropertyValue(T obj, String name) {
		String str = null;

		if (name != null) {
			try {
				Class<T> objClass = (Class<T>) obj.getClass();
				Field field = objClass.getDeclaredField(name);
				field.setAccessible(true);
				Object val = field.get(obj) != null ? field.get(obj) : "";

				return val;

			}

			catch (Exception iEx) {
			}

		}
		return str;
	}

}
