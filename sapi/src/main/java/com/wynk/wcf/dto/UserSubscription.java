package com.wynk.wcf.dto;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class UserSubscription {

  private List<ProductMeta> prodIds;
  private Long offerTS;

  public UserSubscription() {}

  public UserSubscription(List<ProductMeta> prodIds, Long offerTS) {
    this.prodIds = prodIds;
    this.offerTS = offerTS;
  }

  @Getter
  @Setter
  public static class ProductMeta {
    private int prodId;
    private int offerId;
    private int planId;
    private long ets;

    public ProductMeta() {}

    public ProductMeta(int prodId, int offerId, int planId,  long ets) {
      this.prodId = prodId;
      this.offerId = offerId;
      this.planId = planId;
      this.ets = ets;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || getClass() != obj.getClass() || prodIds == null) return false;
    UserSubscription productMeta = (UserSubscription) obj;
    if (CollectionUtils.isEmpty(productMeta.getProdIds())) return false;

    Iterator<ProductMeta> it1 = prodIds.iterator();
    Iterator<ProductMeta> it2 = productMeta.getProdIds().iterator();
    while (it1.hasNext() && it2.hasNext()) {
      ProductMeta it1Obj = it1.next();
      ProductMeta it2Obj = it2.next();
      if (!(Objects.equals(it1Obj.getProdId(), it2Obj.getProdId())
          && Objects.equals(it1Obj.getEts(), it2Obj.getEts())
          && Objects.equals(it1Obj.getOfferId(), it2Obj.getOfferId()))) {
        return false;
      }
    }
    return true;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public UserSubscription fromJson(String json) {
    return new Gson().fromJson(json, UserSubscription.class);
  }
}
