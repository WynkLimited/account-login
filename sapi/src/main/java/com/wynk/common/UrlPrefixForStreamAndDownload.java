package com.wynk.common;

import com.wynk.constants.MusicRouterEnum;

import java.util.HashSet;

public enum UrlPrefixForStreamAndDownload {

	FREE("http://fplay.wynk.in", "http://fdl.wynk.in", "http://fhls.wynk.in/i", "fhls.wynk.in",false,MusicRouterEnum.AKAMAI,"fplay.wynk.in"),
	TFREE("http://tplay.wynk.in","http://tdl.wynk.in","http://thls.wynk.in/i", "thls.wynk.in",false,MusicRouterEnum.AKAMAI,"tplay.wynk.in"),
	PAID("http://play.wynk.in", "http://dl.wynk.in","http://hls.wynk.in/i", "hls.wynk.in",false,MusicRouterEnum.AKAMAI,"play.wynk.in"),
	STREAM_PACKAGING_CACHED_CONFIG("http://play.wynk.in", "http://dl.wynk.in","http://op-hls.wynk.in/i", "op-hls.wynk.in",false,MusicRouterEnum.AKAMAI,"play.wynk.in"),
	SECURE_AKAMAI_STREAM("http://am3.wynk.in","https://am3.wynk.in","http://am4.wynk.in","am4.wynk.in",true,MusicRouterEnum.AKAMAI,"am3.wynk.in"),
	SECURE_CLOUDFRONT_OLD_STREAM("http://cm3.wynk.in","http://cm3.wynk.in","http://cl4.wynk.in","cl4.wynk.in",true,MusicRouterEnum.CLOUDFRONT,"cm3.wynk.in"),
	SECURE_CLOUDFRONT_NEW_STREAM("http://cm3.wynk.in","http://cm3.wynk.in","http://cm4.wynk.in","cm4.wynk.in",true,MusicRouterEnum.CLOUDFRONT,"cm3.wynk.in"),
	SECURE_CLOUDFRONT_WAP_STREAM("http://wpl.wynk.in","http://wpl.wynk.in","http://wpl.wynk.in","wpl.wynk.in",true,MusicRouterEnum.CLOUDFRONT,"wpl.wynk.in"),
	UNSECURE_CLOUDFRONT_WAP_STREAM("http://wp3.wynk.in","http://wp3.wynk.in","http://wp3.wynk.in","wp3.wynk.in",false,MusicRouterEnum.CLOUDFRONT,"wp3.wynk.in");
	// Test configuration with segment length = 30 seconds
	// TEST("http://play.wynk.in", "http://dl.wynk.in","http://wynkhls-vh.akamaihd-staging.net/i", "wynkhls-vh.akamaihd-staging.net"); 
	
	public static HashSet<String> validHostNames = new HashSet<String>();

	static {
		validHostNames.add("fhls.wynk.in");
		validHostNames.add("thls.wynk.in");
		validHostNames.add("hls.wynk.in");
		validHostNames.add("op-hls.wynk.in");
		validHostNames.add("am4.wynk.in");
		validHostNames.add("cl4.wynk.in");
		validHostNames.add("cm4.wynk.in");
		validHostNames.add("wpl.wynk.in");
		validHostNames.add("wp3.wynk.in");
	}
	
	private String streamingUrl;
	private String downloadUrl;
	private String hlsUrl;
	private String hlsHostName;
	private Boolean isSecure;
	private MusicRouterEnum musicRouterEnum;
	private String mp3HostName;
	
	public String getStreamingUrl() {
		return streamingUrl;
	}
	public void setStreamingUrl(String streamingUrl) {
		this.streamingUrl = streamingUrl;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public Boolean getIsSecure(){
		return this.isSecure;
	}

	public String getHlsHostName() {
		return hlsHostName;
	}

	public void setHlsHostName(String hlsHostName) {
		this.hlsHostName = hlsHostName;
	}

	public MusicRouterEnum getMusicRouterEnum() {
		return musicRouterEnum;
	}

	public Boolean getSecure() {
		return isSecure;
	}

	public void setSecure(Boolean secure) {
		isSecure = secure;
	}

	public void setMusicRouterEnum(MusicRouterEnum musicRouterEnum) {
		this.musicRouterEnum = musicRouterEnum;
	}

	public String getMp3HostName() {
		return mp3HostName;
	}

	public void setMp3HostName(String mp3HostName) {
		this.mp3HostName = mp3HostName;
	}

	private UrlPrefixForStreamAndDownload(String streamingUrl, String downloadUrl, String hls, String hlsHostName, Boolean isSecure, MusicRouterEnum musicRouterEnum,String mp3HostName) {
		this.downloadUrl = downloadUrl;
		this.streamingUrl = streamingUrl;
		this.hlsUrl = hls;
		this.hlsHostName = hlsHostName;
		this.isSecure = isSecure;
		this.musicRouterEnum = musicRouterEnum;
		this.mp3HostName = mp3HostName;
	}
	public String getHlsUrl() {
		return hlsUrl;
	}
	public void setHlsUrl(String hlsUrl) {
		this.hlsUrl = hlsUrl;
	}
	
	public static Boolean isValidHostName(String hostname) {
		return validHostNames.contains(hostname);
	}

	public static UrlPrefixForStreamAndDownload getPrefixFromHostName(String hostName) {
		switch (hostName) {
			case "fhls.wynk.in":
				return UrlPrefixForStreamAndDownload.FREE;

			case "thls.wynk.in":
				return UrlPrefixForStreamAndDownload.TFREE;

			case "op-hls.wynk.in":
				return UrlPrefixForStreamAndDownload.STREAM_PACKAGING_CACHED_CONFIG;

			case "am4.wynk.in":
				return UrlPrefixForStreamAndDownload.SECURE_AKAMAI_STREAM;

			case "cl4.wynk.in":
				return UrlPrefixForStreamAndDownload.SECURE_CLOUDFRONT_OLD_STREAM;

			case "cm4.wynk.in":
				return UrlPrefixForStreamAndDownload.SECURE_CLOUDFRONT_NEW_STREAM;

			case "wpl.wynk.in":
				return UrlPrefixForStreamAndDownload.SECURE_CLOUDFRONT_WAP_STREAM;

			case "wp3.wynk.in":
				return UrlPrefixForStreamAndDownload.UNSECURE_CLOUDFRONT_WAP_STREAM;

			default:
				return UrlPrefixForStreamAndDownload.PAID;
		}
	}

}
