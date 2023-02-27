package com.wynk.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PaymentProductId {
	AIRTEL_STORE(1,"AIRTEL_STORE"), TWANG_RENT(2,"TWANG_RENT"), TWANG_SUBSCRIBE(3,"TWANG_SUBSCRIBE"), TWANG_PURCHASE(4, "TWANG_PURCHASE"), 
	IPAYY_SUBSCRIBE(5, "IPAYY_SUBSCRIBE"), WCF_PD(6, "WCF_PD");
	
	private final int id;
	private final String name;
	private static final Map<Integer, PaymentProductId> idToPaymentProductIdMap;
	
	static {
		Map<Integer, PaymentProductId> temp = new HashMap<>();
		for(PaymentProductId paymentProductId: PaymentProductId.values()) {
			temp.put(paymentProductId.id, paymentProductId);
		}
		idToPaymentProductIdMap = Collections.unmodifiableMap(temp);
	}
	
	private PaymentProductId(int id, String name) {
		this.id= id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public static PaymentProductId getById(int id) {
		if(idToPaymentProductIdMap.get(id) != null) {
			return idToPaymentProductIdMap.get(id);
		}
		return PaymentProductId.TWANG_PURCHASE;
	}
}
