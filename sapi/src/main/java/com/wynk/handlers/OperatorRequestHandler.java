package com.wynk.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.service.AccountService;
import com.wynk.service.UserAuthorizationService;
import com.wynk.service.api.NdsUserInfoApiService;
import com.wynk.utils.*;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Controller("/v1/operator/.*")
public class OperatorRequestHandler implements IUrlRequestHandler, IAuthenticatedUrlRequestHandler {

	private Logger logger = LoggerFactory.getLogger(OperatorRequestHandler.class.getCanonicalName());

	private final String jsonContentType = "application/json";

	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	@Autowired
	private NdsUserInfoApiService ndsUserInfoApiService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MusicConfig musicConfig;

	@Autowired
	private UserAuthorizationService userAuthorizationService;

	private ConcurrentMap<String, Boolean> htCptidMap = new ConcurrentLinkedHashMap.Builder<String, Boolean>()
			.maximumWeightedCapacity(2000).build();

	private Set<String> asRequestUrisSet = new HashSet<>();

	@PostConstruct
	public void init() {
		asRequestUrisSet.add("/v1/operator/in/account");
		asRequestUrisSet.add("/v1/operator/in/account/subscribedPacks");
		asRequestUrisSet.add("/v1/operator/datapack/usage");
		asRequestUrisSet.add("/v1/operator/datapack/recommend");
		asRequestUrisSet.add("/v1/operator/datapack/provision");

		// TODO - ADD /HT and /secallback here
	}

	@Override
	public boolean authenticate(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
		// todo: implement logic to check URLs to be excluded.

		if (accountService.checkForMusicUserTokenPresence(request)) {
			return userAuthorizationService.authenticate(request, requestUri, requestPayload);
		}

		if (request.getMethod() != HttpMethod.GET)
			return true;

		int index = requestUri.indexOf("?");
		String requestUriWithoutParams = requestUri;
		if (index > 0) {
			requestUriWithoutParams = requestUri.substring(0, index);
		}

		String msisdn = UserDeviceUtils.getMsisdn(request);

		// todo: add IDC IP check.
		if (StringUtils.isBlank(msisdn)) {
			Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
			msisdn = Utils.getURLParam(urlParameters, "msisdn");
		}
		return !StringUtils.isEmpty(msisdn);
	}

	@Override
	public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request)
			throws PortalException {
		if (logger.isDebugEnabled()) {
			logger.debug("Received request " + requestUri + " with payload " + requestPayload);
		}
		logger.info("OperatorRequestHandler - Received request : " + requestUri);
		Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(requestUri);
		long startTime = System.currentTimeMillis();
		boolean overwriteCircleWithNDS = ObjectUtils
				.getBoolean(Utils.getURLParam(urlParameters, "overwriteCircleWithNDS"), true);

		String msisdnInHeader = UserDeviceUtils.getMsisdn(request);
		String msisdnParam = Utils.getURLParam(urlParameters, "msisdn");

		String msisdn = msisdnInHeader;
		boolean useHeaderMsisdn = ObjectUtils.getBoolean(Utils.getURLParam(urlParameters, "uhm"), true); // use
																											// header
																											// msisdn
		if (!useHeaderMsisdn) {
			msisdn = msisdnParam;
		}

		if (StringUtils.isEmpty(msisdn)) {
			msisdn = msisdnParam;
		}
		if (StringUtils.isEmpty(msisdn) && !requestUri.contains("/ht/")) {
			msisdn = accountService.getMsisdnByUID();
		}
		String circle = Utils.getCircleShortName(msisdn);
		if (!StringUtils.isEmpty(circle)) {
			circle = circle.toLowerCase();
		}
		String tenDigMsisdn = Utils.getTenDigitMsisdn(msisdn);

		int index = requestUri.indexOf("?");
		String requestUriWithoutParams = requestUri;
		if (index > 0) {
			requestUriWithoutParams = requestUri.substring(0, index);
		}
		// for other calls redirect to India server
		if (!asRequestUrisSet.contains(requestUriWithoutParams) && (request.getMethod() == HttpMethod.GET)
				&& !requestUri.startsWith("/v1/operator/userinfo")) {

			HttpResponse response = HTTPUtils.createRedirectResponse("http://125.21.246.72" + requestUri);
			return response;
		}

		if (requestUri.matches("/v1/operator/datapack/usage.*")) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("errorMessage", "No datapacks for music");
			return HTTPUtils.createResponse(jsonObject.toJSONString(), jsonContentType, HttpResponseStatus.OK);
		} else if (requestUri.matches("/v1/operator/datapack/recommend.*")) {
			return HTTPUtils.createResponse("[]", jsonContentType, HttpResponseStatus.OK);
		}
		return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
	}

}
