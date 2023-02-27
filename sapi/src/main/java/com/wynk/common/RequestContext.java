package com.wynk.common;


/**
 * Created with IntelliJ IDEA.
 * User: dhruva
 * Date: 10/12/14
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestContext {

    private boolean forceUpdateNDSInfo;
    private Version apiVersion;

    public boolean isForceUpdateNDSInfo() {
        return forceUpdateNDSInfo;
    }

    public void setForceUpdateNDSInfo(boolean forceUpdateNDSInfo) {
        this.forceUpdateNDSInfo = forceUpdateNDSInfo;
    }

	public Version getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(Version apiVersion) {
		this.apiVersion = apiVersion;
	}
}
