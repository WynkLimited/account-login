package com.wynk.server;


import com.wynk.constants.MusicConstants;
import com.wynk.common.Version;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Created by anurag on 12/9/16.
 */
public class HttpResponseService {

	private static CharSequence       DEFAULT_ERROR_RESPONSE = StringUtils.EMPTY;

	public static HttpResponse createResponseWithEtag(String responseStr) {
		HttpResponse response = null;
		if(!StringUtils.isEmpty(responseStr) && responseStr.equalsIgnoreCase(MusicConstants.NOT_MODIFIED_CODE)) {
			response =  createResponse(createErrorResponse("BSY304", "Not Modified").toJSONString(),
					HttpResponseStatus.NOT_MODIFIED);
		}
		else {
			response = createOKResponse(responseStr);
			if(StringUtils.isNotEmpty(ChannelContext.getEtag()))
				response.headers().set("ETag" , ChannelContext.getEtag());
		}
		return response;
	}

	public static JSONObject createErrorResponse(String errorcode,String errorMessage)
	{
		JSONObject response = new JSONObject();
		response.put("errorCode",errorcode);
		response.put("error",errorMessage);
		return response;
	}

	public static DefaultFullHttpResponse getEmptyOkReponse() {
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8").set(CONTENT_LENGTH,
				httpResponse.content().readableBytes());
		return httpResponse;
	}

	public static FullHttpResponse createOKResponse(String responseStr) {

		if(StringUtils.isEmpty(responseStr))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		return response;
	}

	public static FullHttpResponse createOKJsonResponse(JSONObject responseObject) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(responseObject.toJSONString(), CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		return response;
	}

	public static FullHttpResponse createOKResponseStatic(String responseStr, String contentType) {
		if(StringUtils.isEmpty(responseStr))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, contentType + "; charset=UTF-8");
		return response;
	}

	public static FullHttpResponse createImageResponse(byte[] imageFile) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(imageFile));
		response.headers().set(CONTENT_TYPE, "image/svg+xml");
		return response;
	}

	public static FullHttpResponse createResponse(String responseJson, HttpResponseStatus responseStatus) {
		if(StringUtils.isEmpty(responseJson))
			return getEmptyOkReponse();

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus,
				Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		return response;
	}

	public static FullHttpResponse createResponse(String responseStr, String contentType, HttpResponseStatus status) {
		if(responseStr == null)
			responseStr = "";

		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, contentType);
		return response;
	}

	public static FullHttpResponse createResponse(String responseStr, String contentType, HttpResponseStatus status, String fileName) {
		if(responseStr == null)
			responseStr = "";
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
				Unpooled.copiedBuffer(responseStr, CharsetUtil.UTF_8));
		response.headers().set(CONTENT_TYPE, contentType);
		response.headers().set("Content-Disposition", "attachment; filename=" + fileName);
		return response;
	}


	public static FullHttpResponse createRedirectResponse(String redirectUrl)
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().add(LOCATION , redirectUrl);
		response.headers().add("Cache-Control", "no-cache, no-store, must-revalidate");
		response.headers().add("Pragma", "no-cache");
		response.headers().add("Expires", 0);
		return response;
	}

	public static String createErrorResponse(String error) {
		return createResponseJsonObj(false, error).toString();
	}

	public static JSONObject createResponseJsonObj(boolean success, String error) {
		JSONObject obj = new JSONObject();
		obj.put("success", success);
		if(error != null)
			obj.put("error", error);
		return obj;
	}

	public static String createSuccessResponse() {
		return createResponseJsonObj(true, null).toString();
	}

	public static String createResponse(long total, long num, long pos, JSONArray result, Version version) {
		if(version == Version.V1) {
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

	public static String createImageUploadResponse(String imageUrl,String eTag) {
		JSONObject responseObj = new JSONObject();
		responseObj.put("image_url", imageUrl);
		responseObj.put("etag", eTag);
		return responseObj.toString();
	}

	public static DefaultFullHttpResponse getGenericFailureResponse() {
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.copiedBuffer(DEFAULT_ERROR_RESPONSE,
				CharsetUtil.UTF_8));
		httpResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8").set(CONTENT_LENGTH, httpResponse.content().readableBytes());
		return httpResponse;

	}
}
