package com.wynk.handlers;

import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.dto.*;
import com.wynk.enums.MoEngageEventAttributes;
import com.wynk.enums.MoEngageEventNames;
import com.wynk.music.dto.GeoLocation;
import com.wynk.service.AccountService;
import com.wynk.service.MoEngageEventService;
import com.wynk.service.MyAccountService;
import com.wynk.service.api.HTApiService;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.*;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.dto.*;
import com.wynk.wcf.dto.SubscriptionStatus;
import io.netty.handler.codec.http.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.wynk.wcf.WCFApisConstants.APPID_MOBILITY;
import static com.wynk.wcf.WCFApisConstants.MUSIC;

/**
 * Created by a1vlqlyy on 02/02/17.
 */
@Controller("/music/wcf/cb.*")
public class WCFCallBackHandler implements IUrlRequestHandler,IAuthenticatedUrlRequestHandler {

    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Autowired
    private AccountService accountService;

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private WCFApisService wcfApisService;

    @Autowired
    private HTApiService htApiService;

    @Autowired
    MoEngageEventService moEngageEventService;

    public static final String X_BSY_ATKN = "x-bsy-atkn";
    public static final String X_BSY_DATE = "x-bsy-date";

    @Override
    public boolean authenticate(String requestUri, String requestPayload,  HttpRequest request) throws PortalException {

        if(requestUri.contains("/music/wcf/cb/subscribe/callback")){
            return true;
        }
        HttpMethod httpMethod = request.getMethod();

        String timestamp = request.headers().get(X_BSY_DATE);
        String atken = request.headers().get(X_BSY_ATKN);

        if(StringUtils.isBlank(timestamp) || StringUtils.isBlank(atken)){
            return false;
        }

        String[] parts = atken.split(":");
        String signature = parts[1];
        if(requestPayload == null){
            requestPayload="";
        }

        String calculatedString = wcfUtils.createSignature(httpMethod.name(),requestUri,requestPayload,timestamp,"50de5a601c133a29c8db434fa9bf2db4");

        if(StringUtils.isNotBlank(calculatedString) && calculatedString.equalsIgnoreCase(signature)){
            return true;
        }
        return false;
    }

    @Override
    public HttpResponse handleRequest(String requestUri, String requestPayload, HttpRequest request) throws PortalException {
        if (logger.isInfoEnabled()) {
            logger.info("Received request for WCF CallBack" + requestUri + " with payload " + requestPayload);
        }
        if(request.getMethod() == HttpMethod.POST){

            if(requestUri.contains("/music/wcf/cb/subscribe/tp/callback")) {
                return subscribeCallViaWCF(requestUri,requestPayload);
            } else if (requestUri.contains("/music/wcf/cb/refreshSubsData")) {
                String uid = requestUri.split("\\?")[1].split("=")[1];
                refreshSubsData(uid);
                return HTTPUtils.getCompressedEmptyOkReponse("{}");
            }
        }
        return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    }

    private void refreshSubsData(String uid) {
        try {
            User user = accountService.getUserFromUid(uid);
            String deviceId = "";
            String appVersion = "";
            int buildNo = 0;
            String os = "Android";
            if (CollectionUtils.isNotEmpty(user.getDevices())) {
                List<UserDevice> devices = user.getDevices();
                Collections.reverse(devices);
                for (UserDevice device : devices) {
                    if (device != null && device.getOs() != null) {
                        os = device.getOs();
                        buildNo = device.getBuildNumber();
                        if (device.getDeviceId() != null) {
                            deviceId = device.getDeviceId();
                        }
                        if (device.getAppVersion() != null) {
                            appVersion = device.getAppVersion();
                        }
                        break;
                    }
                }
            }
            OfferProvisionRequest offerProvisionRequest = OfferProvisionRequest.builder()
                    .appId(APPID_MOBILITY)
                    .deviceId(deviceId)
                    .appVersion(appVersion)
                    .msisdn(user.getMsisdn())
                    .service(MUSIC)
                    .buildNo(buildNo)
                    .os(os)
                    .geoLocation(new GeoLocation(user.getCountryId()))
                    .uid(uid).build();
            logger.info("Refreshing Subs : Again hitting offerProvisionRequest with body {}", offerProvisionRequest.toString());
            UserSubscription latestUserSubs = wcfApisService.getUserSubscription(offerProvisionRequest);
            if (latestUserSubs != null) {
                Map<String, Object> paramValues = new HashMap<>();
                paramValues.put(UserEntityKey.userSubscription,JsonUtils.getJsonObjectFromString(latestUserSubs.toJson()));
                accountService.updateSubscriptionForUserId(uid, paramValues,Boolean.FALSE,latestUserSubs);
            }
        } catch (Exception e) {
            logger.error("Error while refreshing subscription data for uid : {}", uid);
            LogstashLoggerUtils.createAccessLogLite("SubsRefreshError", e.getMessage(), uid);
        }
    }

    public HttpResponse subscribeCallViaWCF(String requestUri, String requestPayload) {
        try{
            WCFCallbackResponse wcfCallbackResponse = new WCFCallbackResponse().fromJson(requestPayload);
            if(StringUtils.isBlank(wcfCallbackResponse.getEvent()) || StringUtils.isBlank(wcfCallbackResponse.getUid())){
                logger.error("Event or uid is Missing from payload {}",requestPayload);
                throw new PortalException("Event or uid is Missing");
            }
            List<String> validEvents = Arrays.asList("SUBSCRIBE", "PURCHASE", "RENEW", "UNSUBSCRIBE", "CANCELLED");
            if (!validEvents.contains(wcfCallbackResponse.getEvent().toUpperCase())) {
                return HTTPUtils.createResponse("Invalid event type sent in WCF callback payload", HttpResponseStatus.BAD_REQUEST);
            }
            User user = accountService.getUserFromUid(wcfCallbackResponse.getUid());
            if(ObjectUtils.isEmpty(user)){
                logger.error("[subscribeCallViaWCF] user does not Exists {}",requestPayload);
                return HTTPUtils.createResponse("User not found", HttpResponseStatus.NOT_FOUND);
            }
            if (wcfCallbackResponse.getEvent().toUpperCase().matches("SUBSCRIBE|PURCHASE")) {
                Executors.newSingleThreadExecutor().submit(() ->
                        sendEventToMoEngage(user, MoEngageEventNames.PackPurchased.name(),
                                wcfCallbackResponse.getPlanId().toString()));
            }
            if (wcfCallbackResponse.getEvent().equalsIgnoreCase("RENEW")) {
                Executors.newSingleThreadExecutor().submit(() ->
                        sendEventToMoEngage(user, MoEngageEventNames.PackRenewed.name(),
                                wcfCallbackResponse.getPlanId().toString()));
            }
            if (wcfCallbackResponse.getEvent().toUpperCase().matches("CANCELLED")) {
                refreshSubsData(user.getUid());
                logger.info("Synced db and cache for cancelled with WCF");
            }

            Map<String, Object> paramValues = wcfUtils.getFupResetParameterMap(wcfCallbackResponse.getValidTillDate());
            UserSubscription userSubscription = null;
            try {
                SubscriptionStatusResponse subsStatusResponse = wcfApisService.getSubscriptionStatus(wcfCallbackResponse.getUid());
                List<SubscriptionStatus> subscriptionStatusList = subsStatusResponse.getData();
                if (subscriptionStatusList.isEmpty())
                    throw new Exception("Empty response from WCF");
                userSubscription = wcfUtils.getSubsObjFromSubsStatus(subscriptionStatusList);
            } catch (Exception e) {
                userSubscription = wcfUtils.getUpdatedSubscriptionObject(user,wcfCallbackResponse);
            }
            paramValues.put(UserEntityKey.userSubscription,JsonUtils.getJsonObjectFromString(userSubscription.toJson()));
            accountService.updateSubscriptionForUserId(wcfCallbackResponse.getUid(), paramValues,Boolean.TRUE,userSubscription);

            try {
                htApiService.autoActivateHellotunes(user.getUid());
            } catch (Exception e) {
                logger.error("Error while auto activating hellotunes for the uid : {}", user.getUid());
            }

            return HTTPUtils.getCompressedEmptyOkReponse("{}");
        }catch (Exception ex){
            logger.error("Exception occurred in WCF Subscription Callback for uri {} and ex is {}",requestUri,ex);
            return HTTPUtils.createResponse(ex.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

  private void pushEventsToMoEngage(String uid, List<MoEngageEvent> eventsList) {
    MoEngageEventRequest moEngageEventRequest =
        new MoEngageEventRequest(uid, eventsList);
    try {
      moEngageEventService.sendEvent(moEngageEventRequest);
    } catch (Exception e) {
      logger.error("Error sending HT Event to MoEngage for user " + uid);
    }
  }

  private MoEngageEvent createEvent(String eventName, Map<String, String> attributes) {
    return new MoEngageEvent(eventName, attributes);
  }

  private Map<String, String> createAttributes(User user, String purchasedPack, String eventName) {
    Map<String, String> attributes = new HashMap<>();
    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
    attributes.put(MoEngageEventAttributes.CurrentPlanID.name(), purchasedPack);
    attributes.put(MoEngageEventAttributes.Msisdn.name(), user.getMsisdn());
    attributes.put(MoEngageEventAttributes.Timestamp.name(), simpleDateFormat.format(date));
    if (eventName.equalsIgnoreCase(MoEngageEventNames.PackPurchased.name())) {
      UserSubscription.ProductMeta userLatestSubscription =
          user.getUserSubscription().getProdIds().get(indexOfLatestOffer(user));
      attributes.put(
          MoEngageEventAttributes.OldPlanID.name(),
          String.valueOf(userLatestSubscription.getPlanId()));
      attributes.put(
          MoEngageEventAttributes.OldOfferID.name(),
          String.valueOf(userLatestSubscription.getOfferId()));
    }
    return attributes;
  }

  private void sendEventToMoEngage(User user, String eventName, String purchasedPack) {
      Map<String, String> attributes = createAttributes(user, purchasedPack, eventName);
      MoEngageEvent moEvent = createEvent(eventName, attributes);
      List<MoEngageEvent> eventsList = new ArrayList<>();
      eventsList.add(moEvent);
      pushEventsToMoEngage(user.getUid(), eventsList);
  }

  private int indexOfLatestOffer(User user) {
    List<Integer> listOfHierarchy =
        user.getUserSubscription().getProdIds().stream()
            .map(
                i -> Objects.requireNonNull(WCFApisService.getOffer(i.getOfferId())).getHierarchy())
            .collect(Collectors.toList());
    int max = Collections.max(listOfHierarchy);
    return listOfHierarchy.indexOf(max);
  }
}
