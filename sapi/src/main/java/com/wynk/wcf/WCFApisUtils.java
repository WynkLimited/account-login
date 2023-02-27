package com.wynk.wcf;

import com.wynk.constants.MusicConstants;
import com.wynk.server.ChannelContext;
import com.wynk.utils.MusicDeviceUtils;
import com.wynk.utils.ObjectUtils;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.dto.*;
import com.wynk.wcf.dto.UserSubscription.ProductMeta;
import com.wynk.wcf.dto.AllProductsResponse.Product;

import java.util.*;
import java.util.function.Function;

import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.common.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WCFApisUtils {

  @Autowired
  WCFApisService wcfApisService;

  private static final Logger logger =
	  LoggerFactory.getLogger(WCFApisService.class.getCanonicalName());

  static Function<ProductMeta, Boolean> isPaid =
	  e -> {
		Offer offerObject = WCFApisService.getOffer(e.getOfferId());
		return ObjectUtils.isEmpty(offerObject)
			? false
			: offerObject.getProvisionType() == ProvisionType.PAID
				&& e.getEts() > System.currentTimeMillis();
	  };

  public Feature getFeature(FeatureType featureType, UserSubscription userSubscription) {

      Feature feature = new Feature(featureType);
      try {
          if (Objects.isNull(userSubscription) || userSubscription.getProdIds() == null) {
            return feature;
          }
          // sorting prodIds in descending expired timestamp
          Collections.sort(
              userSubscription.getProdIds(), Comparator.comparing(UserSubscription.ProductMeta::getEts));
          Collections.reverse(userSubscription.getProdIds());
          Set<Integer> featureAllowedProductIds;
          switch (featureType) {
              case HELLOTUNES:
                  featureAllowedProductIds = WCFUtils.htAllowedProductIds;
                  break;
              case SHOW_ADS:
                  featureAllowedProductIds = WCFUtils.hideAdsProductIds;
                  break;
              case DOWNLOADS:
                  featureAllowedProductIds = WCFUtils.downloadsAllowedProductIds;
                  break;
              case STREAMING:
                  featureAllowedProductIds = WCFUtils.streamingAllowedProductIds;
                  break;
              default:
                  featureAllowedProductIds = new HashSet<>();
          }

          for (ProductMeta productMeta : userSubscription.getProdIds()) {
              if (featureAllowedProductIds.contains(productMeta.getProdId())) {
                  feature.setProductId(productMeta.getProdId());
                  feature.setOfferId(productMeta.getOfferId());
                  PlanDTO plan = WCFApisService.getPlan(productMeta.getPlanId());
                  Product product = WCFApisService.getProduct(productMeta.getProdId());
                  if(product != null && ObjectUtils.notEmpty(product.getFeatures())){
                    if(product.getFeatures().containsKey(FeatureType.STREAMING.getName())){
                      feature.setStreamCount((Double)product.getFeatures().get(FeatureType.STREAMING.getName()));
                    }
                  }
                  if (plan != null) {
                      PriceDTO price = plan.getPrice();
                      if (price != null) {
                          Double amount = price.getAmount();
                          if (amount != null)
                              feature.setPrice(amount.intValue());
                      }
                      if (plan.getPeriod() != null) {
                          feature.setValidity(plan.getPeriod().getValidity());
                      }
                  }
                  feature.setValidTill(productMeta.getEts());
                  boolean isProductActiveToday = productMeta.getEts() > System.currentTimeMillis();
                  feature.setSubscribed(isProductActiveToday);
                  return feature;
              }
          }
      } catch (Exception e) {
          logger.error("getFeature exception " + e);
      }
      return feature;
  }

  public Set<Integer> getUpdatedAndNewProdIds(
	  List<ProductMeta> userDBProdIds, List<ProductMeta> userLatestProdIds) {
      Set<Integer> updatedAndNewProdIds = new HashSet<>();
      try {
          for (ProductMeta latestProd : userLatestProdIds) {
              Boolean alreadyExists = false;
              if (userDBProdIds != null) {
                  for (ProductMeta dbProd : userDBProdIds) {
                      if (alreadyExists) {
                          break;
                      }
                      if (dbProd.getProdId() == latestProd.getProdId()) {
                          alreadyExists = true;
                          if (dbProd.getEts() != latestProd.getEts()) {
                              updatedAndNewProdIds.add(latestProd.getProdId());
                          }
                      }
                  }
              }
              if (!alreadyExists) {
                  updatedAndNewProdIds.add(latestProd.getProdId());
              }
          }
      } catch (Exception e) {
          logger.error("Error in getting updated and new product Ids {}", e.getMessage());
      }
      return updatedAndNewProdIds;
  }

  public Feature getUserFeature(UserSubscription userSubscription) {

      if (userSubscription == null)
          return null;

      Feature downloadAllowed = getFeature(FeatureType.DOWNLOADS, userSubscription);
      Feature streamingAllowed = getFeature(FeatureType.STREAMING, userSubscription);

      if (downloadAllowed.getProductId() != 0 && downloadAllowed.isSubscribed()) {
          return downloadAllowed;
      } else if(streamingAllowed.getProductId() != 0 && streamingAllowed.isSubscribed()) {
          return streamingAllowed;
      } else {
          return new Feature(FeatureType.UNSUBSCRIBED);
      }
  }

  public static boolean appendWCFWebViewURL() {

        Map<String, String> musicHeaders = MusicDeviceUtils.parseMusicHeaderDID();
        HttpRequest request = ChannelContext.getRequest();
        String isWap = request.headers().get(MusicConstants.MUSIC_HEADER_IS_WAP);
        if (StringUtils.isNotBlank(isWap) && isWap.equalsIgnoreCase("true")) {
            return false;
        }
        String os = musicHeaders.get(MusicConstants.OS);
        String buildNo = musicHeaders.get(MusicConstants.APP_BUILD_NO);
        if (os != null && buildNo != null) {
            Integer buildNum = Integer.parseInt(buildNo);
            if ((os.equalsIgnoreCase("android") && buildNum >= WCFApisConstants.ANDROID_THRESHOLD)
                    || (os.equalsIgnoreCase("ios") && buildNum >= WCFApisConstants.IOS_THRESHOLD)) {
                return true;
            }
        }
        return false;
  }

  public boolean isPaidUser(UserSubscription userSubscription) {
	return ObjectUtils.notEmpty(userSubscription)
			&& ObjectUtils.notEmpty(userSubscription.getProdIds())
		? userSubscription.getProdIds().stream().anyMatch(WCFApisUtils.isPaid::apply)
		: false;
  }

  public static ProductMeta getHighestHierarchyProduct(List<ProductMeta> products) {
      if (products == null)
          return null;
      ProductMeta bestProduct = null;
      Integer highestHierarchy = null;
      for (ProductMeta productMeta : products) {
          if (productMeta.getEts() >= System.currentTimeMillis()) {
              Product prod = WCFApisService.getProduct(productMeta.getProdId());
              if (prod != null) {
                  Integer hierarchy = prod.getHierarchy();
                  if (highestHierarchy == null || hierarchy > highestHierarchy) {
                      highestHierarchy = hierarchy;
                      bestProduct = productMeta;
                  }
              }
          }
      }
      return bestProduct;
  }

  public static int getHighestHierarchyOfferId(List<ProductMeta> products) {
    try {
      Optional<Pair<Offer, Integer>> bestOfferWithHierarchyTuple =
          products.stream()
              .filter(product -> product.getEts() >= System.currentTimeMillis())
              .map(product -> WCFApisService.getOffer(product.getOfferId()))
              .filter(Objects::nonNull)
              .map(offer -> Pair.of(offer, offer.getHierarchy()))
              .max(Comparator.comparing(Pair::getSecond));
      if (bestOfferWithHierarchyTuple.isPresent()) {
        return bestOfferWithHierarchyTuple.get().getFirst().getId();
      }
    } catch (Exception e) {
      logger.error("Exception while finding HighestHierarchyOffer. Exception : {}", e.toString());
    }
    return Integer.MIN_VALUE;
  }
}
