package com.test.loadtest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by anurag on 12/22/16.
 */
public class ServerLoad {

	private static String REQUEST_FILE_NAME = "/Users/anurag/Documents/docs/finalRequestlog";

	private static int numOfThreads = 100;
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(numOfThreads);
	private static int requestCount = 10000;


	private static List<String> hosts = Arrays.asList("http://10.1.2.171:8080");

	public static void main(String[] args) {
		makeRequestFromFile();
	}

	/**
	 * grep line
	 * UID : e7UugO0SgJIPRDGxE0 ;Signature : XdYkgiGHlghff79d+gMU9RAP3Go= ;url : GET/music/v1/crcgw/srch_universalmusic_00602557150117-USUM71608607.html?sq=h&lang=en ;token : eik5m1wG ;generatedSig : XdYkgiGHlghff79d+gMU9RAP3Go= ;deviceId : 794D3A22-6FB3-491D-99AF-31967893642C/iOS/10.2/61/1.5.1
	 * bypass client verification
	 */
	public static void makeRequestFromFile() {

		System.out.println("Started executing file:" + REQUEST_FILE_NAME);
		BufferedReader br = null;
		String line = "";
		CountDownLatch latch = new CountDownLatch(requestCount);
		int requestId = 0;
		long t = System.currentTimeMillis();
		try {
			br = new BufferedReader(new FileReader(REQUEST_FILE_NAME));
			while ((line = br.readLine()) != null) {

				String[] data = line.split(";");
				if(line.contains("fingerprintmatch") || line.contains("metamatch")
						|| line.contains("account") || line.contains("datapack/") || line.contains("mymusic"))
					continue;

				if(data.length < 6)
				{
					System.out.println("data length less than 6");
					continue;
				}

				String userId = data[0];
				String uid = userId.substring(userId.indexOf("UID :"));
				uid = uid.split(":")[1].trim();

				String signature = data[1];
				String sign = signature.substring(signature.indexOf("Signature :"));
				sign = sign.split(":")[1].trim();

				String URLs = data[2];
				String url = URLs.substring(URLs.indexOf("url :")).trim();

				String requestMethod = url.substring(0,url.indexOf("/"));
				requestMethod = requestMethod.split(":")[1].trim();
				int ls = url.indexOf("{");
				if(ls == -1 )
					ls = url.indexOf("[");
				String requestURI = null;
				String payload = null;
				if(ls == -1)
					requestURI = url.substring(url.indexOf("/"));
				else {
					requestURI = url.substring(url.indexOf("/"), ls);
					payload = url.substring(ls);
				}

				String headers = "x-bsy-utkn=" + uid + ":" + sign;


				headers = headers +  ", x-bsy-did=" + data[5].split("deviceId :")[1].trim();
				headers += ", X-Forwarded-For=157.48.69.65, X-Forwarded-Port=80, X-Forwarded-Proto=http, X-Forwarded-For=10.0.11.27, Connection=close";

				Map<String,String> headerMap = getHeaderMap(headers);

				Random ran = new Random();
				int randomNum = ran.nextInt(3) ;
				String host = hosts.get(0);
				requestURI = host + requestURI.trim();

				if(requestURI.contains("userContents")) {
					requestURI = requestURI.replace("userContents", "usercontents");
				}

				if (requestId == requestCount)
					break;

				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				executorService.submit(new RequestCall( requestURI , payload, requestMethod , headerMap, requestId, latch));
				requestId ++;
				if(requestId % 100 == 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}

		}

		try {
			latch.await();
		} catch (Exception E) {
			System.out.println(E);
		}

		executorService.shutdown();

		System.out.println("Total number of request made:" + requestCount + ":"
				+ (System.currentTimeMillis() - t));

	}


	private static Map<String,String> getHeaderMap(String headers) {
		headers = headers.trim();
		Map<String,String> headerMap = new HashMap<String,String>();

		List<String> requiredHeaders = Arrays.asList( "x-bsy-utkn=","x-wap-profile=",
				"x-bsy-net=","x-bsy-snet=","x-bsy-cid=",
				"x-bsy-did=","X-Forwarded-For=", "host=","Accept-Encoding=","Content-Type=","X-Forwarded-Port",
				"X-Forwarded-Proto=","Connection=");

		for (String header : requiredHeaders) {
			if (headers.contains(header)) {
				String key = header;
				int startIdx = headers.indexOf(key);
				int endIdx = headers.indexOf(",", startIdx);
				if (endIdx < 0)
					endIdx = headers.length();
				String value = (String) headers.subSequence(startIdx, endIdx);
				value = value.replace(key, "");
				key = key.replace("=", "");
				headerMap.put(key,value);
			}
		}

		return headerMap;
	}

}


class RequestCall implements Callable {

	String url;
	String payload;
	String requestMethod;
	Map<String,String> headers;
	int requestId;
	CountDownLatch latch;


	RequestCall(String url, String payload, String requestMethod, Map<String,String> headers, int requestId, CountDownLatch latch) {
		this.url = url;
		this.payload = payload;
		this.requestId = requestId;
		this.latch = latch;
		this.headers = headers;
		this.requestMethod = requestMethod;
	}

	@Override
	public Object call() throws Exception {
		try {

			//System.out.println("Processing request:" + requestId + ":" + requestMethod + ":" + url);
			if (requestMethod.equals("POST")) {

				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
				HttpConnectionParams.setSoTimeout(httpParameters, 60000);
				DefaultHttpClient client = new DefaultHttpClient(httpParameters);
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout

				HttpResponse response;

				HttpPost post = null;
				try {

					post = new HttpPost(url);
					post.setHeader("Content-Type", "text/plain");
					for(String key : headers.keySet()) {
						post.setHeader(key, headers.get(key));
					}
					ByteArrayInputStream bis = new ByteArrayInputStream(
							payload.getBytes());
					HttpEntity entity = new InputStreamEntity(bis, bis.available());
					post.setEntity(entity);
					response = client.execute(post);
					StatusLine statusLine = response.getStatusLine();
					Thread.sleep(100);
					if (statusLine.getStatusCode() >= 400) {
						throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
					} else {
					}
				} catch (Exception e) {
					System.out.println(url);
					System.out.println(e);
				} finally {
					if (post != null) {
						post.releaseConnection();
						//System.out.println("RELEASING POST");
					}
					if (client != null) {
						client.getConnectionManager().shutdown();
						//System.out.println("RELEASING CONNECTION");
					}
				}

			}
			if (requestMethod.equals("GET")) {
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
				HttpConnectionParams.setSoTimeout(httpParameters, 60000);
				DefaultHttpClient client = new DefaultHttpClient(httpParameters);
				HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); // Timeout
				HttpResponse response;

				HttpGet post = null;
				try {
					post = new HttpGet(url);
					post.setHeader("Content-Type", "text/plain");
					for(String key : headers.keySet()) {
						post.setHeader(key, headers.get(key));
					}
					response = client.execute(post);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() >= 400) {
						throw new Exception(String.format("Got Http error code [%s]", statusLine.getStatusCode()));
					} else {
					}
				} catch (Exception e) {
					System.out.println(url);
					System.out.println(e);
				} finally {
					if (post != null) {
						post.releaseConnection();
						//System.out.println("RELEASING GET");
					}
					if (client != null) {
						client.getConnectionManager().shutdown();
						//System.out.println("RELEASING CONNECTION");
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			//System.out.println("Completed executing requestId:" + requestId );
		}
		return null;
	}



}
