package com.wynk.wcf.dto;

import java.util.List;
import lombok.Data;

@Data
public class PriceDTO {

  private Double amount;
  private String currency;
  private List<DiscountDTO> discount;
}
