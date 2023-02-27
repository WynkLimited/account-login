package com.wynk.service.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.WCFApisService;
import com.wynk.wcf.dto.OfferProvisionRequest;
import com.wynk.wcf.dto.UserSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WCFProvisionsCommand extends HystrixCommand<UserSubscription> {
  private static final Logger logger = LoggerFactory.getLogger(WCFProvisionsCommand.class.getCanonicalName());
  WCFApisService wcfService;
  String service;
  String uid;
  OfferProvisionRequest wcfProvisionOfferRequestApiObject;
  boolean nullIfFallback;

  public WCFProvisionsCommand(Setter setter, WCFApisService wcfService, String service, OfferProvisionRequest wcfProvisionOfferRequestApiObject, String uid, boolean nullIfFallback) {
    super(setter);
    this.service = service;
    this.uid = uid;
    this.wcfService = wcfService;
    this.wcfProvisionOfferRequestApiObject = wcfProvisionOfferRequestApiObject;
    this.nullIfFallback = nullIfFallback;
  }

  @Override
  protected UserSubscription run() throws Exception {
    return wcfService.getUserSubscription(wcfProvisionOfferRequestApiObject);
  }

  @Override
  protected UserSubscription getFallback() {
    String msisdn = wcfProvisionOfferRequestApiObject.getMsisdn();
    logger.info("Fallback triggered in WCF Packs call for msisdn {}", msisdn, getExecutionException());
    LogstashLoggerUtils.createStandardLog(HystrixUtils.HYSTRIX_FALLBACK_LOGS, uid, msisdn, "", HystrixUtils.getCommandProperties(this));
    return nullIfFallback ? null : WCFUtils.FALLBACK_PROVISION;
  }

}