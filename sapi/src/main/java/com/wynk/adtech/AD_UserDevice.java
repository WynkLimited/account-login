package com.wynk.adtech;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

/**
 * Created by anurag on 10/3/16.
 */
@Table(value = "devices")
public class AD_UserDevice {

	@PrimaryKey
	private String deviceid;

	private String uid;

	private String deviceappver;

	private String devicebuildnos;

	private String deviceimei;

	private boolean deviceisactive;

	private String devicekey;

	private long   devicekeylastupdatetime;

	private String deviceregistrationdate;

	private String deviceresolution;

	private String devicetype;

	private String lastactivitydate;

	private String lastbatchprocessedid;

	private String msisdn;

	private String operator;

	private String os;

	private String osversion;

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDeviceappver() {
		return deviceappver;
	}

	public void setDeviceappver(String deviceappver) {
		this.deviceappver = deviceappver;
	}

	public String getDevicebuildnos() {
		return devicebuildnos;
	}

	public void setDevicebuildnos(String devicebuildnos) {
		this.devicebuildnos = devicebuildnos;
	}

	public String getDeviceimei() {
		return deviceimei;
	}

	public void setDeviceimei(String deviceimei) {
		this.deviceimei = deviceimei;
	}

	public boolean isDeviceisactive() {
		return deviceisactive;
	}

	public void setDeviceisactive(boolean deviceisactive) {
		this.deviceisactive = deviceisactive;
	}

	public String getDevicekey() {
		return devicekey;
	}

	public void setDevicekey(String devicekey) {
		this.devicekey = devicekey;
	}

	public long getDevicekeylastupdatetime() {
		return devicekeylastupdatetime;
	}

	public void setDevicekeylastupdatetime(long devicekeylastupdatetime) {
		this.devicekeylastupdatetime = devicekeylastupdatetime;
	}

	public String getDeviceregistrationdate() {
		return deviceregistrationdate;
	}

	public void setDeviceregistrationdate(String deviceregistrationdate) {
		this.deviceregistrationdate = deviceregistrationdate;
	}

	public String getDeviceresolution() {
		return deviceresolution;
	}

	public void setDeviceresolution(String deviceresolution) {
		this.deviceresolution = deviceresolution;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getLastactivitydate() {
		return lastactivitydate;
	}

	public void setLastactivitydate(String lastactivitydate) {
		this.lastactivitydate = lastactivitydate;
	}

	public String getLastbatchprocessedid() {
		return lastbatchprocessedid;
	}

	public void setLastbatchprocessedid(String lastbatchprocessedid) {
		this.lastbatchprocessedid = lastbatchprocessedid;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsversion() {
		return osversion;
	}

	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}
}
