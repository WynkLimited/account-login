package com.wynk.dto;


/**
 * 
 * @author imran
 *
 */
public class HttpTransactionLogDTO {

	private long requestTimeMillis;
	private long responseTimeMillis;
	private String requsetUrl;
	private int httpResponseStatus;
	
	public HttpTransactionLogDTO(long requestTimeMillis, long responseTimeMillis, String requsetUrl,
			int httpResponseStatus) {
		super();
		this.requestTimeMillis = requestTimeMillis;
		this.responseTimeMillis = responseTimeMillis;
		this.requsetUrl = requsetUrl;
		this.httpResponseStatus = httpResponseStatus;
	}

	public long getRequestTimeMillis() {
		return requestTimeMillis;
	}

	public void setRequestTimeMillis(long requestTimeMillis) {
		this.requestTimeMillis = requestTimeMillis;
	}

	public long getResponseTimeMillis() {
		return responseTimeMillis;
	}

	public void setResponseTimeMillis(long responseTimeMillis) {
		this.responseTimeMillis = responseTimeMillis;
	}

	public String getRequsetUrl() {
		return requsetUrl;
	}

	public void setRequsetUrl(String requsetUrl) {
		this.requsetUrl = requsetUrl;
	}
	
	public void setRequsetUrl(String requsetUrl, boolean trimURL) {
		if(requsetUrl !=null && requsetUrl.contains("?")){
			int index = requsetUrl.lastIndexOf("?");
			requsetUrl = requsetUrl.substring(0, index);
		}
		this.requsetUrl = requsetUrl;
	}

	public int getHttpResponseStatus() {
		return httpResponseStatus;
	}

	public void setHttpResponseStatus(int httpResponseStatus) {
		this.httpResponseStatus = httpResponseStatus;
	}

	public long getProcessingTimeMillis() {
		return responseTimeMillis - requestTimeMillis;
	}

	@Override
	public String toString() {
		return "HttpRequestResponseLogDTO [requestTimeMillis="
				+ requestTimeMillis + ", responseTimeMillis="
				+ responseTimeMillis + ", requsetUrl=" + requsetUrl
				+ ", responseStatus=" + httpResponseStatus + "]";
	}

	public HttpTransactionLogDTO() {
		super();
	}
	
	
	
	
}
