package com.wynk.utils;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.handler.codec.http.cookie.Cookie;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wynk.common.PackProviderAuthRequest;
import com.wynk.common.PortalException;
import com.wynk.common.Version;
import com.wynk.constants.MusicConstants;
import com.wynk.server.ChannelContext;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.springframework.util.CollectionUtils;


/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPUtils {

	private static final HttpResponse NOT_FOUND = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);

	public static HttpResponse notFound() {
		return NOT_FOUND;
	}

	public static HttpResponse createResponseWithEtag(String responseStr) {
		HttpResponse response = null;
		if (!StringUtils.isEmpty(responseStr) && responseStr.equalsIgnoreCase(MusicConstants.NOT_MODIFIED_CODE)) {
			response = HTTPUtils.createResponse(Utils.createErrorResponse("BSY304", "Not Modified").toJSONString(),
					HttpResponseStatus.NOT_MODIFIED);
		} else {
			if (StringUtils.isEmpty(responseStr) || responseStr.equalsIgnoreCase(JsonUtils.EMPTY_JSON_STR))
				return HTTPUtils.createResponse("{\"error\":\"No info for requested API \"}",
						HttpResponseStatus.INTERNAL_SERVER_ERROR);
			response = HTTPUtils.createOKResponse(responseStr);
			if (StringUtils.isNotEmpty(ChannelContext.getEtag()))
				response.headers().set("ETag", ChannelContext.getEtag());
		}
		return response;
	}

	public static DefaultFullHttpResponse getEmptyOkReponse() {
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8").set(CONTENT_LENGTH,
				httpResponse.content().readableBytes());
		return httpResponse;
	}

	public static DefaultFullHttpResponse getCompressedEmptyOkReponse(String responseStr) {
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		return httpResponse;
	}

	public static FullHttpResponse createOKResponse(String responseStr) {

		if (StringUtils.isEmpty(responseStr))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		return response;
	}

	public static FullHttpResponse createOKResponseStatic(String responseStr, String contentType) {
		if (StringUtils.isEmpty(responseStr))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType + "; charset=UTF-8");
		return response;
	}

	public static FullHttpResponse createResponse(String responseJson, HttpResponseStatus responseStatus) {
		if (StringUtils.isEmpty(responseJson))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus,
				Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		return response;
	}

	public static FullHttpResponse createResponse(String responseStr, String contentType, HttpResponseStatus status) {
		if (responseStr == null)
			responseStr = "";

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
		return response;
	}

	public static FullHttpResponse createResponse(String responseStr, String contentType, HttpResponseStatus status,
			String fileName) {
		if (responseStr == null)
			responseStr = "";
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
		response.headers().set("Content-Disposition", "attachment; filename=" + fileName);
		return response;
	}

	public static FullHttpResponse createRedirectResponse(String redirectUrl) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().add(HttpHeaders.Names.LOCATION, redirectUrl);
		response.headers().add("Cache-Control", "no-cache, no-store, must-revalidate");
		response.headers().add("Pragma", "no-cache");
		response.headers().add("Expires", 0);
		return response;
	}

	public static String createErrorResponse(String error) {
		return HTTPUtils.createResponseJsonObj(false, error).toString();
	}

	public static JSONObject createResponseJsonObj(boolean success, String error) {
		JSONObject obj = new JSONObject();
		obj.put("success", success);
		if (error != null)
			obj.put("error", error);
		return obj;
	}

	public static String createSuccessResponse() {
		return createResponseJsonObj(true, null).toString();
	}

	public static String createResponse(long total, long num, long pos, JSONArray result, Version version) {
		if (version == Version.V1) {
			return result.toString();
		}
		JSONObject responseObj = new JSONObject();
		responseObj.put("total", total);
		responseObj.put("num", num);
		responseObj.put("pos", pos);
		responseObj.put("result", result);
		return responseObj.toString();
	}

	public static String createSmartShopResponse(long total, long num, long pos, JSONArray result) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("total", total);
		responseObj.put("num", num);
		responseObj.put("pos", pos);
		responseObj.put("result", result);
		return responseObj.toString();
	}

	public static String createSmartShopResponse(long total, long num, long pos, JSONObject result, Version version) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("total", total);
		responseObj.put("num", num);
		responseObj.put("pos", pos);
		responseObj.put("result", result);
		return responseObj.toString();
	}

	public static String createSmartShopResponse(JSONArray result) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("result", result);
		return responseObj.toString();
	}

	public static String createQriousResponse(long total, long num, long pos, JSONArray result) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("total_results", total);
		responseObj.put("request_num", num);
		responseObj.put("start_index", pos);
		responseObj.put("q", result);
		return responseObj.toString();
	}

	public static String createImageUploadResponse(String imageUrl, String eTag) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("image_url", imageUrl);
		responseObj.put("etag", eTag);
		return responseObj.toString();
	}

	// WARNING: Resetting index in this call to be able to call multiple times
	// perform and file operations
	public static Map<String, List<String>> getMultipartUrlParameters(HttpRequest request) throws PortalException {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();

			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

			List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
			for (InterfaceHttpData data : bodyHttpDatas) {
				if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
					Attribute attribute = (Attribute) data;
					String value = attribute.getValue();
					String key = attribute.getName();
					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			((FullHttpRequest) request).content().resetReaderIndex();
			return params;
		} catch (Exception e) {
			throw new PortalException("Error extracting MultiPart Url Params from url : " + request.getUri()
					+ ". Error : " + e.getMessage(), e);
		}
	}

	// WARNING: Resetting index in this call to be able to call multiple times
	// perform and file operations
	public static Reader getMultipartUrlFile(HttpRequest request) throws PortalException {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);

			List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
			for (InterfaceHttpData data : bodyHttpDatas) {
				if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
					FileUpload attribute = (FileUpload) data;
					byte[] file = attribute.get();
					if (file == null || file.length == 0) {
						return null;
					}
					ByteArrayInputStream bis = new ByteArrayInputStream(file);
					InputStreamReader isr = new InputStreamReader(bis, attribute.getCharset());
					((FullHttpRequest) request).content().resetReaderIndex();
					return isr;
				}
			}
			((FullHttpRequest) request).content().resetReaderIndex();
			return null;
		} catch (Exception e) {
			throw new PortalException("Error extracting MultiPart Url Params from url : " + request.getUri()
					+ ". Error : " + e.getMessage(), e);
		}
	}

	public static Map<String, List<String>> getUrlParameters(String url) throws PortalException {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String pair[] = param.split("=",2);
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}
					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			return params;
		} catch (UnsupportedEncodingException e) {
			throw new PortalException("Error extracting Url Params from url : " + url + ". Error : " + e.getMessage(),
					e);
		}
	}

	public static Map<String, List<String>> getAllUrlParameters(String url) throws PortalException {
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				for (String query : urlParts) {
					for (String param : query.split("&")) {
						String pair[] = param.split("=",2);
						String key = URLDecoder.decode(pair[0], "UTF-8");
						String value = "";
						if (pair.length > 1) {
							value = URLDecoder.decode(pair[1], "UTF-8");
						}
						List<String> values = params.get(key);
						if (values == null) {
							values = new ArrayList<String>();
							params.put(key, values);
						}
						values.add(value);
					}
				}
			}
			return params;
		} catch (UnsupportedEncodingException e) {
			throw new PortalException("Error extracting Url Params from url : " + url + ". Error : " + e.getMessage(),
					e);
		}
	}

	public static int getNumParameter(Map<String, List<String>> urlParameters, String paramName, int defaultValue) {
		int numresults = defaultValue;
		String stringParameter = HTTPUtils.getStringParameter(urlParameters, paramName);
		try {
			if (stringParameter != null && !stringParameter.equals("")) {
				stringParameter = stringParameter.replace(",", "");
				numresults = Integer.parseInt(stringParameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return numresults;
	}

	public static long getLongParameter(Map<String, List<String>> urlParameters, String paramName, long defaultValue) {
		String stringParameter = HTTPUtils.getStringParameter(urlParameters, paramName);
		try {
			if (StringUtils.isNotBlank(stringParameter) && !stringParameter.equals("null")) {
				defaultValue = Long.parseLong(stringParameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static double getDoubleParameter(Map<String, List<String>> urlParameters, String paramName,
			double defaultValue) {
		String stringParameter = HTTPUtils.getStringParameter(urlParameters, paramName);
		try {
			if (stringParameter != null && !stringParameter.equals("")) {
				defaultValue = Double.parseDouble(stringParameter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public static String getStringParameter(Map<String, List<String>> urlParameters, String paramName) {
		if (urlParameters == null)
			return null;
		if (urlParameters.get(paramName) != null && urlParameters.get(paramName).size() > 0) {
			String nresultsParam = urlParameters.get(paramName).get(0);
			if (nresultsParam != null) {
				return nresultsParam.trim();
			}
		}
		return null;
	}

	public static Boolean getBooleanParameter(Map<String, List<String>> urlParameters, String paramName,
			Boolean defaultValue) {
		if (urlParameters.get(paramName) != null && urlParameters.get(paramName).size() > 0) {
			String nresultsParam = urlParameters.get(paramName).get(0);
			return ObjectUtils.getBoolean(nresultsParam, defaultValue);
		}
		return defaultValue;
	}

	public static List<String> getListParameter(Map<String, List<String>> urlParameters, String paramName) {
		if (urlParameters.get(paramName) != null && urlParameters.get(paramName).size() > 0)
			return urlParameters.get(paramName);
		else
			return null;
	}

	public static HashMap<String, Integer> getSortMapFromUrlParams(Map<String, List<String>> urlParameters) {
		HashMap<String, Integer> sortMap = new HashMap<String, Integer>();
		List<String> sortParams = urlParameters.get("sort");
		if (sortParams != null && sortParams.size() > 0) {
			for (String s : sortParams) {
				s = s.trim();
				if (s.equals("")) {
					continue;
				}
				int sign = 1;
				if (s.charAt(0) == '+') {
					s = s.substring(1);
				} else if (s.charAt(0) == '-') {
					sign = -1;
					s = s.substring(1);
				}
				if (s.toLowerCase().equals("lastupdated")) {
					sortMap.put("lastUpdated", sign);
				}
				if (s.toLowerCase().equals("creationtime")) {
					sortMap.put("creationTime", sign);
				}
				if (s.toLowerCase().equals("price")) {
					sortMap.put("price", sign);
				}
				if (s.toLowerCase().equals("rank")) {
					sortMap.put("rank", sign);
				}
				if (s.toLowerCase().equals("releasedate")) {
					sortMap.put("releaseDate", sign);
				}
				if (s.toLowerCase().equals("starttime")) {
					sortMap.put("startTime", sign);
				}
				if (s.toLowerCase().equals("durationseconds")) {
					sortMap.put("durationSeconds", sign);
				}
				if (s.toLowerCase().equals("recommendation")) {
					sortMap.put("recommendation", sign);
				}
				if (s.toLowerCase().equals("publishedyear")) {
					sortMap.put("publishedYear", sign);
				}
			}
		}
		return sortMap;
	}

	public static String createMSResponse(long num, JSONObject result, Version version) {
		if (version == Version.V1) {
			return result.toString();
		}
		JSONObject responseObj = new JSONObject();
		responseObj.put("num", num);
		responseObj.put("result", result);
		return responseObj.toString();
	}

	public static PackProviderAuthRequest createApplicationAuthRequestFrom(String requestUri, String requestPayload,
			HttpRequest request) throws PortalException {
		String signatureHeader = request.headers().get(Utils.X_BSY_ATKN);
		String signature = StringUtils.EMPTY;
		String appId = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(signatureHeader)) {
			String[] tokens = signatureHeader.split(":");
			if (2 == tokens.length) {
				appId = tokens[0];
				signature = tokens[1];
			}
		}
		PackProviderAuthRequest appAuthRequest = new PackProviderAuthRequest.Builder()
				.method(request.getMethod().name()).requestUri(requestUri).requestPayload(requestPayload)
				.signature(signature).requestTimestamp(Long.parseLong(request.headers().get(Utils.X_BSY_DATE)))
				.appId(appId).build();
		return appAuthRequest;
	}

	public static JSONObject getHeaderJSON(HttpRequest request) {
		Set<String> headernames = request.headers().names();
		Iterator<String> headerNameItr = headernames.iterator();
		JSONObject headersJson = new JSONObject();
		while (headerNameItr.hasNext()) {
			String name = headerNameItr.next();
			String value = request.headers().get(name);
			headersJson.put(name, value);
		}
		return headersJson;
	}

}
