package com.wynk.adtech;

import org.json.simple.JSONObject;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Set;

/**
 * Created by anurag on 10/3/16.
 */
@Table(value = "users")
public class AD_User {

	@PrimaryKey
	private String uid;

	private String fname;
	private String email;
	private Set<String> device_ids;
	private String circle;
	private String usertype;
	private String operator;
	private String user_subscription_type;
	private String dob;
	private String gender;
	private String lang_selcted;
	private String lastActivityDate;
	private String lname;
	private String msisdn;
	private String songQuality;
	private int productId;
	private String max_mrp;
	private Set<String> other_apps_installed;
	private String registration_date;
	private long rentalscount;
	private String song_streamed_30_days;
	private String songquality;
	private long source;
	private long streamedcount;
	private long daily_avg_song_streamed;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getDevice_ids() {
		return device_ids;
	}

	public void setDevice_ids(Set<String> device_ids) {
		this.device_ids = device_ids;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getUser_subscription_type() {
		return user_subscription_type;
	}

	public void setUser_subscription_type(String user_subscription_type) {
		this.user_subscription_type = user_subscription_type;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLang_selcted() {
		return lang_selcted;
	}

	public void setLang_selcted(String lang_selcted) {
		this.lang_selcted = lang_selcted;
	}

	public String getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(String lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getSongQuality() {
		return songQuality;
	}

	public void setSongQuality(String songQuality) {
		this.songQuality = songQuality;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getMax_mrp() {
		return max_mrp;
	}

	public void setMax_mrp(String max_mrp) {
		this.max_mrp = max_mrp;
	}

	public Set<String> getOther_apps_installed() {
		return other_apps_installed;
	}

	public void setOther_apps_installed(Set<String> other_apps_installed) {
		this.other_apps_installed = other_apps_installed;
	}

	public String getRegistration_date() {
		return registration_date;
	}

	public void setRegistration_date(String registration_date) {
		this.registration_date = registration_date;
	}

	public long getRentalscount() {
		return rentalscount;
	}

	public void setRentalscount(long rentalscount) {
		this.rentalscount = rentalscount;
	}

	public String getSong_streamed_30_days() {
		return song_streamed_30_days;
	}

	public void setSong_streamed_30_days(String song_streamed_30_days) {
		this.song_streamed_30_days = song_streamed_30_days;
	}

	public String getSongquality() {
		return songquality;
	}

	public void setSongquality(String songquality) {
		this.songquality = songquality;
	}

	public long getSource() {
		return source;
	}

	public void setSource(long source) {
		this.source = source;
	}

	public long getStreamedcount() {
		return streamedcount;
	}

	public void setStreamedcount(long streamedcount) {
		this.streamedcount = streamedcount;
	}

	public long getDaily_avg_song_streamed() {
		return daily_avg_song_streamed;
	}

	public void setDaily_avg_song_streamed(long daily_avg_song_streamed) {
		this.daily_avg_song_streamed = daily_avg_song_streamed;
	}

	public void fromCassandraJSON(JSONObject jsonObject) {

	}
}