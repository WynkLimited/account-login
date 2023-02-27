package com.wynk.wcf;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.wynk.adtech.AdUtils;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.constants.Constants;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.MusicConstants;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.MusicSubscriptionState;
import com.wynk.dto.MusicSubscriptionStatus;
import com.wynk.enums.Intent;
import com.wynk.enums.Theme;
import com.wynk.enums.WCFPackGroup;
import com.wynk.enums.WCFView;
import com.wynk.music.dto.GeoLocation;
import com.wynk.server.ChannelContext;
import com.wynk.service.MusicService;
import com.wynk.service.SubscriptionIntentService;
import com.wynk.user.dto.User;
import com.wynk.utils.*;
import com.wynk.wcf.dto.*;
import com.wynk.wcf.dto.AllProductsResponse.Product;
import com.wynk.wcf.dto.UserSubscription.ProductMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.wynk.config.MusicConfig;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import static com.wynk.constants.MusicConstants.APP_BUILD_NO;
import static com.wynk.wcf.WCFApisConstants.THEME;

@Service
public class WCFApisService {

  private static final Logger logger =
	  LoggerFactory.getLogger(WCFApisService.class.getCanonicalName());
  private static String wcfBaseUrl;
  private static String wcfOfferUrl;
  private static String wcfIdentificationUrl;
  private static String wcfAllOffersUrl;
  private static String wcfAllPlansUrl;
  private static String wcfAllProductsUrl;

  @Autowired
  private MusicConfig musicConfig;

  @Autowired
  private WCFUtils wcfUtils;

  @Autowired
  private WCFApisUtils wcfApisUtils;

  @Autowired
  private MusicService musicService;

  @Autowired
  private JedisCluster wcfPayRedisCluster;

  @Autowired
  private SubscriptionIntentService subscriptionIntentService;

  private Gson gson = new Gson();

  Product fallBackProduct;

  private static Map<Integer, Product> allProducts = new ConcurrentHashMap<>();

  private static Map<Integer, Offer> allOffers = new ConcurrentHashMap<>();

  private static Map<Integer, PlanDTO> allPlans = new ConcurrentHashMap<>();
  private static final String WCF_SUBS_STATUS_ENDPOINT = "%s/wynk/s2s/v1/subscription/status/%s/music";

  private static final String ONBOARDING_EXCEPTION = "ONBOARDING_EXCEPTION";

  @PostConstruct
  void init() {
	Map<String, Object> features = new HashMap<>();
	features.put("showAds", false);
	features.put("downloads", true);
	features.put("streams", -1);
	fallBackProduct = new AllProductsResponse.Product();
	wcfBaseUrl = musicConfig.getWcfBaseUrl();
	wcfOfferUrl = musicConfig.getWcfOfferUrl();
	wcfIdentificationUrl = musicConfig.getWcfIdentificationUrl();
	wcfAllOffersUrl = musicConfig.getWcfAllOffersUrl();
	wcfAllPlansUrl = musicConfig.getWcfAllPlansUrl();
	wcfAllProductsUrl = musicConfig.getWcfAllProductsUrl();
	fallBackProduct.setCpName("WYNK_MUSIC");
	fallBackProduct.setId(WCFUtils.WCF_FALLBACK_PRODUCT_ID);
	fallBackProduct.setPackGroup("wynk_music");
	fallBackProduct.setFeatures(features);
	// Setting fallback product
	allProducts.put(fallBackProduct.getId(), fallBackProduct);
  }

  public static Map<Integer, Product> getAllProducts() {
	return allProducts;
  }

  public static Map<Integer, Offer> getAllOffers() {
	return allOffers;
  }

  public static Map<Integer, PlanDTO> getAllPlans() {
	return allPlans;
  }

  public static Product getProduct(int productId) {
	if (allProducts.containsKey(productId)) {
	  return allProducts.get(productId);
	}
	return null;
  }

  public static Offer getOffer(int offerId) {
	if (allOffers.containsKey(offerId)) {
	  return allOffers.get(offerId);
	}
	return null;
  }

  public static PlanDTO getPlan(int planId) {
	if (allPlans.containsKey(planId)) {
	  return allPlans.get(planId);
	}
	return null;
  }

  public OfferProvisionResponse getOfferProvisionResponse(
	  OfferProvisionRequest offerProvisionRequest) {
	String url = String.format(wcfOfferUrl, wcfBaseUrl);
	String offerProvisionReq = offerProvisionRequest.toJson();
	Map<String, String> headerMap =
		wcfUtils.getHeaderMap(
			WCFApisConstants.METHOD_POST, String.format(wcfOfferUrl, ""), offerProvisionReq);
	long startTime = System.currentTimeMillis();
	logger.info(
		"WCF Offer Request Url :"
			+ String.format(wcfOfferUrl, "")
			+ "with request Body"
			+ offerProvisionReq
			+ "with header :"
			+ headerMap);
	String responseBody =
		HttpClient.postData(
			url, offerProvisionReq, headerMap, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS);
	logger.info("WCF Offer Response Data  : " + responseBody);
	LogstashLoggerUtils.createAccessLogLite("OfferProvisionResponse",responseBody,offerProvisionRequest.getUid());
	if (responseBody == null) {
	  throw new RuntimeException("Null Response for Wcf Offer Api");
	}
	logger.info("WCF Offer Api call time span : " + (System.currentTimeMillis() - startTime));
	return new OfferProvisionResponse().fromJson(responseBody);
  }

	public SubscriptionStatusResponse getSubscriptionStatus(String uid) {
		String url = String.format(WCF_SUBS_STATUS_ENDPOINT, wcfBaseUrl, uid);
		Map<String, String> headerMap = wcfUtils.getHeaderMap(WCFApisConstants.METHOD_GET,
				String.format(WCF_SUBS_STATUS_ENDPOINT, "", uid), null);
		long startTime = System.currentTimeMillis();
		logger.info("WCF subscriptionStatus Url : {} with header : {}", url, headerMap);
		String responseBody =
				HttpClient.getContent(
						url, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS, headerMap);
		logger.info("WCF subscriptionStatus response Data  : {}", responseBody);
		LogstashLoggerUtils.createAccessLogLite("SubscriptionStatusResponse", responseBody, uid);
		if (responseBody == null) {
			return new SubscriptionStatusResponse();
		}
		logger.info("WCF Offer Api call time span : {}", (System.currentTimeMillis() - startTime));
		return gson.fromJson(responseBody, SubscriptionStatusResponse.class);
	}

  public MsisdnIdentificationResponse getMsisdnResponse(
	  MsisdnIdentificationRequest msisdnIdentificationRequest) {
	String url = String.format(wcfIdentificationUrl, wcfBaseUrl);
	String identificationReq = msisdnIdentificationRequest.toJson();
	Map<String, String> headerMap =
		wcfUtils.getHeaderMap(
			WCFApisConstants.METHOD_POST,
			String.format(wcfIdentificationUrl, ""),
			identificationReq);
	long startTime = System.currentTimeMillis();
	logger.info(
		"Account Identification Request Url :"
			+ String.format(wcfIdentificationUrl, "")
			+ "with request Body"
			+ identificationReq
			+ "with header :"
			+ headerMap);
	String responseBody =
		HttpClient.postData(
			url, identificationReq, headerMap, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS);
	logger.info("Account Identification Response Data  : " + responseBody);
	LogstashLoggerUtils.createAccessLogLite("MsisdnIdentificationResponse",responseBody + msisdnIdentificationRequest.toString(), "NA");
		if (responseBody == null) {
	  throw new RuntimeException("Null Response for Account Identification Api");
	}
		LogstashLoggerUtils.createAccessLogLite("AccountIdentification",responseBody,"NA");
		logger.info(
		"Account Identification Api call time span : " + (System.currentTimeMillis() - startTime));
	return new MsisdnIdentificationResponse().fromJson(responseBody);
  }

  public AllProductsResponse getAllProductsResponse() {
	String url = String.format(wcfAllProductsUrl, wcfBaseUrl);
	Map<String, String> headerMap =
		wcfUtils.getHeaderMap(
			WCFApisConstants.METHOD_GET, String.format(wcfAllProductsUrl, ""), null);
	long startTime = System.currentTimeMillis();
	logger.info(
		"All products Request Url :"
			+ String.format(wcfAllProductsUrl, "")
			+ "with header :"
			+ headerMap);
	String responseBody =
		HttpClient.getContent(url, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS, headerMap);
	logger.info("All products Response Data  : " + responseBody);
	LogstashLoggerUtils.createAccessLogLite("AllProductsResponse",responseBody,"NA");
		if (responseBody == null) {
	  throw new RuntimeException("Null Response for All products Api");
	}
	logger.info("All products Api call time span : " + (System.currentTimeMillis() - startTime));
	return new AllProductsResponse().fromJson(responseBody);
  }

  public AllOffersResponse getAllOffersResponse() {
	String url = String.format(wcfAllOffersUrl, wcfBaseUrl);
	Map<String, String> headerMap =
		wcfUtils.getHeaderMap(
			WCFApisConstants.METHOD_GET, String.format(wcfAllOffersUrl, ""), null);
	long startTime = System.currentTimeMillis();
	logger.info(
		"All offers Request Url :"
			+ String.format(wcfAllOffersUrl, "")
			+ "with header :"
			+ headerMap);
	String responseBody =
		HttpClient.getContent(url, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS, headerMap);
	logger.info("All offers Response Data  : " + responseBody);
	LogstashLoggerUtils.createAccessLogLite("AllOffersResponse",responseBody,"NA");
		if (responseBody == null) {
	  throw new RuntimeException("Null Response for All offers Api");
	}
	logger.info("All offers Api call time span : " + (System.currentTimeMillis() - startTime));
	return new AllOffersResponse().fromJson(responseBody);
  }

  public AllPlansResponse getAllPlansResponse() {
	String url = String.format(wcfAllPlansUrl, wcfBaseUrl);
	Map<String, String> headerMap =
		wcfUtils.getHeaderMap(
			WCFApisConstants.METHOD_GET, String.format(wcfAllPlansUrl, ""), null);
	long startTime = System.currentTimeMillis();
	logger.info(
		"All plans Request Url :"
			+ String.format(wcfAllPlansUrl, "")
			+ "with header :"
			+ headerMap);
	String responseBody =
		HttpClient.getContent(url, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS, headerMap);
	logger.info("All plans Response Data  : " + responseBody);
	LogstashLoggerUtils.createAccessLogLite("AllPlansResponse",responseBody,"NA");
		if (responseBody == null) {
	  throw new RuntimeException("Null Response for All plans Api");
	}
	logger.info("All plans Api call time span : " + (System.currentTimeMillis() - startTime));
	return new AllPlansResponse().fromJson(responseBody);
  }

  public String forwardInitWebViewRequest(String payload, WCFView view) {
	String requestUri = null;
	if (view != null && view.equals(WCFView.SMALL))
	  requestUri = WCFApisConstants.PLANS_MANAGE_ENDPOINT;
	else requestUri = WCFApisConstants.PLANS_PURCHASE_ENDPOINT;
	String url = wcfBaseUrl.concat(requestUri);
	String responseBody = null;
	long startTime = 0;
	try {
	  Map<String, String> headerMap =
		  wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, requestUri, payload);
	  startTime = System.currentTimeMillis();
	  logger.info(
		  "WCF Init WebView Request Url : "
			  + requestUri
			  + " with body "
			  + payload
			  + " with header : "
			  + headerMap);
	  responseBody =
		  HttpClient.postData(url, payload, headerMap, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS);
		LogstashLoggerUtils.createAccessLogLite("PlanPurchaseResponse",responseBody,"NA");
		logger.info(
		  "WCF Init WebView Request Response "
			  + responseBody
			  + " for uid "
			  + ChannelContext.getUid());
		LogstashLoggerUtils.createAccessLogLite("initWebViewWCFResponse", responseBody, ChannelContext.getUid());
	} catch (Exception ex) {
	  LogstashLoggerUtils.createFatalExceptionLogWithMessage(
		  ex,
		  ExceptionTypeEnum.THIRD_PARTY.WCF.name(),
		  "",
		  "WCFApiService.forwardInitWebViewRequest",
		  "Exception while forwarding Init WebView Request for uid" + ChannelContext.getUid());
	  logger.error(
		  "Exception occured in WCF Init WebView Call for uid {} And Payload {} and exception is "
			  + ex,
		  ChannelContext.getUid(),
		  payload);
	}
	long apiTimeSpan = System.currentTimeMillis() - startTime;
	logger.info("WCF Init WebView Call time span : " + apiTimeSpan);
	return responseBody;
  }

  public String verifyPaymentReceipt(String payload) {
	String requestUri = WCFApisConstants.VERIFY_RECEIPT_ENDPOINT;
	String wcfPayBaseUrl = musicConfig.getWcfPayBaseUrl();
	String url = wcfPayBaseUrl.concat(requestUri);
	String responseBody = null;
	long startTime = 0;
	try {
	  Map<String, String> headerMap =
		  wcfUtils.getHeaderMap(WCFApisConstants.METHOD_POST, requestUri, payload);
	  startTime = System.currentTimeMillis();
	  logger.info(
		  "WCF payment receipt verification request url : "
			  + requestUri
			  + " with body "
			  + payload
			  + " with header : "
			  + headerMap.toString());
	  responseBody =
		  HttpClient.postData(url, payload, headerMap, WCFApisConstants.CONNECTION_TIMEOUT_MILLIS);
	  logger.info(
		  "WCF payment receipt verification response "
			  + responseBody
			  + " for uid "
			  + ChannelContext.getUid());
		LogstashLoggerUtils.createAccessLogLite("verifyPaymentReceiptWCFResponse", responseBody, ChannelContext.getUid());
		LogstashLoggerUtils.createAccessLogLite("verifyPaymentReceipt",payload,ChannelContext.getUid());
	} catch (Exception e) {
	  LogstashLoggerUtils.createFatalExceptionLogWithMessage(
		  e,
		  ExceptionTypeEnum.THIRD_PARTY.WCF.name(),
		  "",
		  "WCFApiService.verifyPaymentReceipt",
		  "Exception while forwarding payment receipt verification Request for uid"
			  + ChannelContext.getUid());
	  logger.error(
		  "Exception occured in WCF payment receipt verification call for uid {} and payload {} and exception is "
			  + e,
		  ChannelContext.getUid(),
		  payload);
	}
	long apiTimeSpan = System.currentTimeMillis() - startTime;
	logger.info("WCF payment receipt verification call time span : " + apiTimeSpan);
	return responseBody;
  }

  public UserSubscription getUserSubscription(OfferProvisionRequest offerProvisionRequest) {
	OfferProvisionResponse offerProvisionResponse =
		getOfferProvisionResponse(offerProvisionRequest);
	  logger.info("Wcf received from WCF in getOfferProvisionResponse with response {}", offerProvisionResponse);
	List<ProductMeta> productMetaList = new ArrayList();
	List<OfferStatus> offers = offerProvisionResponse.getData().getOfferStatus();
	offers = offers.stream().filter(e -> e.getStatus() == Status.ACTIVE).collect(Collectors.toList());
	for (SubscriptionStatus subscriptionStatus :
		offerProvisionResponse.getData().getSubscriptionStatus()) {
		for(OfferStatus offer : offers){
			int productId = subscriptionStatus.getProductId();
			int offerId = offer.getOfferId();
			int planId = offer.getPlanIds().get(0);
			long validity = subscriptionStatus.getValidity();
			if(getOffer(offerId).getProducts().containsKey(String.valueOf(productId))){
				ProductMeta userSubscription = new ProductMeta();
				userSubscription.setProdId(productId);
				userSubscription.setOfferId(offerId);
				userSubscription.setPlanId(planId);
				userSubscription.setEts(validity);
				productMetaList.add(userSubscription);
				break;
			}
		}
	}
	return UserSubscription.builder().offerTS(System.currentTimeMillis()).prodIds(productMetaList).build();
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60)
  public void loadProducts() {
	try {
	  Set<Integer> htAllowedProductIds = new HashSet<>();
	  Set<Integer> downloadsAllowedProductIds = new HashSet<>();
	  Set<Integer> streamingAllowedProductIds = new HashSet<>();
	  Set<Integer> hideAdsProductIds = new HashSet<>();
	  Set<Integer> htTrialProducts = new HashSet<>();
	  Map<Integer, Map<String, Object>> htMeta = new HashMap<>();
	  List<Product> allProductsResponse = getAllProductsResponse().getData().getAllProducts();
	  if (CollectionUtils.isNotEmpty(allProductsResponse)) {
		Map<Integer, Product> latestProductsData = new ConcurrentHashMap<>();
		allProductsResponse.stream()
			.filter(Objects::nonNull)
			.forEach(
				e -> {
				  latestProductsData.put(e.getId(), e);
				  if (ObjectUtils.notEmpty(e.getFeatures())) {

					if (e.getFeatures().containsKey(FeatureType.HELLOTUNES.getName()) &&
							(Boolean) e.getFeatures().get(FeatureType.HELLOTUNES.getName())) {
					  htAllowedProductIds.add(e.getId());
					}

					if (e.getFeatures().containsKey(FeatureType.DOWNLOADS.getName()) &&
							(Boolean) e.getFeatures().get(FeatureType.DOWNLOADS.getName())) {
					  downloadsAllowedProductIds.add(e.getId());
					}

					if (e.getFeatures().containsKey(FeatureType.STREAMING.getName())) {
					  streamingAllowedProductIds.add(e.getId());
					}

					if (e.getFeatures().containsKey(FeatureType.SHOW_ADS.getName()) &&
							!(Boolean) e.getFeatures().get(FeatureType.SHOW_ADS.getName())) {
					  hideAdsProductIds.add(e.getId());
					}

					if (e.getFeatures().containsKey(FeatureType.HELLOTUNES_TRIAL.getName()) &&
								(Boolean) e.getFeatures().get(FeatureType.HELLOTUNES_TRIAL.getName())) {
						htTrialProducts.add(e.getId());
					}

				  }
				  // for HT only packs CPName will be hellotunes
				  if (e.getCpName().equalsIgnoreCase(FeatureType.HELLOTUNES.toString())) {
							htAllowedProductIds.add(e.getId());
							htMeta.put(e.getId(), e.getFeatures());
				  }
				});
		allProducts = latestProductsData;
	  }
	  wcfUtils.setDownloadsProductIdsInRedis(downloadsAllowedProductIds);
	  wcfUtils.setStreamingProductIdsInRedis(streamingAllowedProductIds);
	  wcfUtils.setHideAdsProductIdsInRedis(hideAdsProductIds);
	  wcfUtils.setHtTrialProductIdsInRedis(htTrialProducts);
	  wcfUtils.setHtAllowedProductIdsMetaInRedis(htMeta);
	  htAllowedProductIds.removeAll(htTrialProducts);
	  wcfUtils.setHtProductIdsInRedis(htAllowedProductIds);
	} catch (Exception ex) {
	  logger.error("Exception occurred while loading products : {}", ex.getMessage());
	}
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60)
  public void loadOffers() {
	try {
	  List<Offer> allOffersResponse = getAllOffersResponse().getData().getAllOffers();
	  if (CollectionUtils.isNotEmpty(allOffersResponse)) {
		Map<Integer, Offer> latestOffersData = new ConcurrentHashMap<>();
		Map<Integer, Map<String, Object>> offersMeta = new HashMap<>();
		allOffersResponse.stream()
			.filter(Objects::nonNull)
			.forEach(
					e -> {
						latestOffersData.put(e.getId(), e);
						Map<String, Object> offerMeta = new HashMap<>();
						offerMeta.put("description", e.getDescription());
						offerMeta.put("hierarchy", e.getHierarchy());
						offersMeta.put(e.getId(), offerMeta);
					});
		allOffers = latestOffersData;
		wcfUtils.setOffersMetaInRedis(offersMeta);
		wcfUtils.setCompleteOffersMetaInRedis(latestOffersData);
	  }
	} catch (Exception ex) {
	  logger.error("Exception occurred while loading offers : {}", ex.getMessage());
	}
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60)
  public void loadAllPlans() {
	try {
	  List<PlanDTO> allPlansDataResponse = getAllPlansResponse().getData().getPlans();
	  if (CollectionUtils.isNotEmpty(allPlansDataResponse)) {
		Map<Integer, PlanDTO> latestPlansData = new ConcurrentHashMap<>();
		allPlansDataResponse.stream()
			.filter(Objects::nonNull)
			.forEach(e -> latestPlansData.put(e.getId(), e));
		allPlans = latestPlansData;
	  }
	} catch (Exception ex) {
	  logger.error("Exception occurred while loading plans : {}", ex.getMessage());
	}
  }

  public HttpResponse setSidAndPlanIdInRedis(String payload) {
    String uri = WCFApisConstants.WCF_STATIC_WEBVIEW_ENDPOINT;
    JSONObject paymentData;
    try {
      paymentData = gson.fromJson(payload, JSONObject.class);
      String digestString = uri.split("#")[1] + paymentData.get(JsonKeyNames.UID) + new BigDecimal(paymentData.get(WCFApisConstants.TIMESTAMP).toString()).toPlainString();
      String requestSignature = (String) paymentData.get(WCFApisConstants.HASH);
      String computedSignature = EncryptUtils.calculateRFC2104HMAC(digestString, musicConfig.getStaticWebViewSecretKey());
//      computedSignature = URLEncoder.encode(computedSignature, "UTF-8");
      if(requestSignature != null && !requestSignature.equals(computedSignature)) {
        logger.error("[POST Request from WCF static WebView] Unauthenticated Request : " + payload);
        return HTTPUtils.createResponse("Unauthenticated Request", HttpResponseStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      logger.error("[POST Request from WCF static WebView] error while authenticating the request : " + payload);
      return HTTPUtils.createResponse("Error while authenticating the request.", HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
    boolean success = true;
    String errorMessage = null;
    String key = paymentData.get(JsonKeyNames.UID) + "_" + paymentData.get(MusicConstants.OS).toString().toLowerCase() + "_" + paymentData.get(WCFApisConstants.DEVICE_ID);
    try {
      wcfPayRedisCluster.setex(key, 15*60, payload);
    } catch (Exception e) {
      logger.error("[POST Request from WCF static WebView] Error while setting paymentData in redis for key : {} and value : {}", key, payload);
      success = false;
      errorMessage = e.toString();
      String sqsUrl = musicConfig.getWcfPaySqsUrl();
      AmazonSQS sqs = AmazonSQSClientBuilder.standard()
              .withRegion(musicConfig.getWcfPaySqsRegion().equalsIgnoreCase("ap-south-1") ? Regions.AP_SOUTH_1 : Regions.US_EAST_1)
              .build();
      JSONObject sqsMessage = new JSONObject();
      sqsMessage.put("key", key);
      sqsMessage.put("value" , payload);
      SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
              .withQueueUrl(sqsUrl)
              .withMessageBody(sqsMessage.toString());
      sqs.sendMessage(sendMessageStandardQueue);
    }
    if (success) {
      return HTTPUtils.createOKResponse("{\"status\":\"ok\"}");
    } else {
      return HTTPUtils.createResponse(errorMessage, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public MusicSubscriptionStatus getMusicSubscriptionStatusFromCache(String service, User user
		  ,String msisdn, Boolean isAirtelUser, Boolean subStatusCall, String requestUri, Boolean isConfigOrAccount, Boolean fromAccountCall){

		MusicSubscriptionStatus musicSubscriptionStatus = new MusicSubscriptionStatus();
		try{

			MusicSubscriptionState subscriptionState = null;
			UserSubscription userSubscription = null;
			String uid = null;
			if (user != null) {
				userSubscription = user.getUserSubscription();
				uid = user.getUid();
			}
			musicSubscriptionStatus.setUserSubscription(userSubscription);

			//setting highestHierarchyProductId in response
			ProductMeta bestProduct = null;
			if (userSubscription != null) {
				bestProduct = WCFApisUtils.getHighestHierarchyProduct(userSubscription.getProdIds());
				if (bestProduct != null) {
					musicSubscriptionStatus.setProductId(bestProduct.getProdId());
				}
			}

			int bestOffer = Integer.MIN_VALUE;
			try {
				if (fromAccountCall) {
					if (userSubscription != null) {
						bestOffer = WCFApisUtils.getHighestHierarchyOfferId(userSubscription.getProdIds());
					}
					//if userSubscription is null or
					// there exist no offer in case of expiry
					if (bestOffer == Integer.MIN_VALUE) {
						//providing the lowest package for now
						//need to ask product abt lowest package
						if (user.getOperator() != null && user.getOperator().equalsIgnoreCase(Constants.AIRTEL)) {
							bestOffer = MusicConstants.WYNK_AIRTEL_USER_BASE_PACK;
						} else {
							bestOffer = MusicConstants.WYNK_NON_AIRTEL_USER_BASE_PACK;
						}
					}
					musicSubscriptionStatus.setTopOfferId(bestOffer);
				}
			} catch (Exception e) {
				LogstashLoggerUtils.createAccessLogLite(ONBOARDING_EXCEPTION, e.toString(), user.getUid());
			}

			Boolean hasDownloadFeature = wcfApisUtils.getFeature(FeatureType.DOWNLOADS, userSubscription).isSubscribed();
			if ((subStatusCall || isConfigOrAccount) && (!hasDownloadFeature || org.apache.commons.lang3.StringUtils.isBlank(msisdn))) {

				// client allows download for SUBSCRIBED_PRE_REMINDER and SUBSCRIBED_IN_REMINDER status only
				// except for IOS build less than 651 which rely on SUBSCRIBED_GRACE_EXCEEDED to block users from downloading
				Map<String, String> deviceInfo = MusicDeviceUtils.parseMusicHeaderDID();
				if (deviceInfo != null && deviceInfo.get(APP_BUILD_NO) != null && deviceInfo.get(MusicConstants.OS) != null) {
					Integer buildNo = Integer.parseInt(deviceInfo.get(APP_BUILD_NO));
					String os = deviceInfo.get(MusicConstants.OS);
					if (os.toLowerCase().equals("ios") && buildNo <= 651) {
						subscriptionState = MusicSubscriptionState.SUBSCRIBED_GRACE_EXCEEDED;
					} else {
						subscriptionState = MusicSubscriptionState.NEVER_SUBSCRIBED;
					}
				} else {
					subscriptionState = MusicSubscriptionState.NEVER_SUBSCRIBED;
				}

				// only for musicSubscriptionStatusCall
				if (subStatusCall) {
					if (WCFApisUtils.appendWCFWebViewURL()) {
						Theme theme = null;
						if (requestUri != null) {
							Map<String, List<String>> queryParams = HTTPUtils.getUrlParameters(requestUri);
							if (queryParams.containsKey(THEME) && queryParams.get(THEME).size() > 0) {
								theme = Theme.getTheme(queryParams.get(THEME).get(0));
							}
						}
						JSONObject webViewData = musicService.forwardInitWCFWebViewRequest(WCFPackGroup.WYNK_MUSIC, theme, null, WCFView.LARGE, Intent.DOWNLOAD.name());
						musicSubscriptionStatus.setRedirectUrl((String) webViewData.get(WCFApisConstants.REDIRECT_URL));
						musicSubscriptionStatus.setSid((String) webViewData.get(WCFApisConstants.SID));
					}
					if (MusicBuildUtils.isSubscriptionIntentSupported()) {
						JSONObject subscriptionIntentObject = new JSONObject();
						subscriptionIntentObject.put(JsonKeyNames.DOWNLOAD_INTENT_PAYLOAD, subscriptionIntentService.getDownloadIntent());
						musicSubscriptionStatus.setSubscription_intent(subscriptionIntentObject);
					}
				}
			} else {
				if (bestProduct != null) {
					subscriptionState = SubscriptionUtils.getSubscriptionState(bestProduct.getEts());
				} else {
					subscriptionState = SubscriptionUtils.getSubscriptionState(0);
				}
			}

			musicSubscriptionStatus.setState(subscriptionState);
			PlanDTO planInfo = WCFApisService.getPlan(bestProduct.getPlanId());

			if (MusicSubscriptionState.NEVER_SUBSCRIBED.equals(subscriptionState)) {
				musicSubscriptionStatus.setOfferPackAvailed(false);
				musicSubscriptionStatus.setUnsubscribed(true);
				musicSubscriptionStatus.setAutoRenewalOn(false);
			} else if (bestProduct != null) {
				populateMusicSubscriptionStatus(musicSubscriptionStatus, bestProduct);
			}

			musicSubscriptionStatus.setSubscriptionType(AdUtils.getSubType(wcfApisUtils.getFeature(FeatureType.SHOW_ADS,userSubscription)));

			if(bestProduct == null){
				logger.error("Products don't exist for the uid {} ", uid);
			} else {
				// as of now client is not using price value
				if (planInfo != null) {
					PriceDTO price = planInfo.getPrice();
					if (price != null) {
						Double amount = price.getAmount();
						if (amount != null)
							musicSubscriptionStatus.setPrice(amount.intValue());
					}
					if (planInfo.getPeriod() != null) {
						musicSubscriptionStatus.setPackValidityInDays(planInfo.getPeriod().getValidity());
					}
				}
			}
			musicSubscriptionStatus.setOfferPackAvailed(true);

			if(org.apache.commons.lang3.StringUtils.isNotBlank(musicSubscriptionStatus.getMessage())){
				Map<String,Object> paramMap = new HashMap();
				Long expiryDateInMillis = musicSubscriptionStatus.getExpireTimestamp() - 1L;
				String validDate = DateFormatUtils.format(expiryDateInMillis, "dd MMM yyyy");
				paramMap.put("{validDate}",validDate);
				musicSubscriptionStatus.setMessage(wcfUtils.replaceMessageParameter(musicSubscriptionStatus.getMessage(),paramMap));
			}
		} catch (Exception e) {
			logger.error("Exception : " + ExceptionUtils.getStackTrace(e));
		}
		return musicSubscriptionStatus;
  }

  private MusicSubscriptionStatus populateMusicSubscriptionStatus(MusicSubscriptionStatus musicSubscriptionStatus, ProductMeta bestProduct) {
		long expireTimeStamp = bestProduct.getEts();
		musicSubscriptionStatus.setExpireTimestamp(expireTimeStamp);
		int daysToExpire = (int) Math.floor((expireTimeStamp - System.currentTimeMillis()) / MusicConstants.DAY);
		if (daysToExpire >= 0)
			musicSubscriptionStatus.setDaysToExpire(daysToExpire);
		else
			musicSubscriptionStatus.setDaysToExpire(0);
		musicSubscriptionStatus.setAutoRenewalOn(false);
		musicSubscriptionStatus.setUnsubscribed(false);
		return musicSubscriptionStatus;
  }

  public JSONObject refreshProducts(){
		logger.info("refreshing : Products, Offers and Plans");
		JSONObject jsonObj = new JSONObject();
		loadProducts();
		loadOffers();
		loadAllPlans();
		jsonObj.put(WCFApisConstants.ALL_PRODUCTS,getAllProducts());
		jsonObj.put(WCFApisConstants.ALL_OFFERS,getAllOffers());
		jsonObj.put(WCFApisConstants.ALL_PLANS, getAllPlans());
		return jsonObj;
  }

	public JSONObject getInMemoryProductIds(){
		logger.info("returning in-memory productIds");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("htAllowedProducts", WCFUtils.htAllowedProductIds);
		jsonObj.put("htTrialProductIds", WCFUtils.htTrialProductIds);
		jsonObj.put("downloadsAllowedProductIds", WCFUtils.downloadsAllowedProductIds);
		jsonObj.put("hideAdsProductIds", WCFUtils.hideAdsProductIds);
		jsonObj.put("streamingAllowedProductIds", WCFUtils.streamingAllowedProductIds);
		return jsonObj;
	}
}
