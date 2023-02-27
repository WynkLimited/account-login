package com.wynk.adtech;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

/**
 * Created by anurag on 10/4/16.
 */
@Table(value = "play_counts")
public class UserSongPlayCount {

	@PrimaryKeyColumn(name = "uid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String uid;

	@PrimaryKeyColumn(name = "day", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
	private String day;

	private int counter;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
