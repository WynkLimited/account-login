package com.wynk.dto;

import com.wynk.music.constants.MusicContentType;
import com.wynk.common.PaymentProductId;
import com.wynk.common.PaymentRequestType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Payment extends BaseObject{

	private String ibmTransactionId;
	private String ibmStatus;
	private String userId;
	private String merchantId;
	private String transactionId;
	private String orderId;
	private String bankTxnId;
	private String txnAmount;
	private String currency;
	private String status;
	private String respCode;
	private String respMsg;
	private String txnDate;
	private String gatewayName;
	private String bankName;
	private String paymentMode;
	private String checksumHash;
	private boolean checksumVerified;
	private String ibmReason;
	private PaymentProductId productId;
	private String contentId;
	private String subscriptionId;
	private PaymentRequestType requestType;
	private int src;
	private MusicContentType contentType;
	private String subscriberId;
	private String contentTitle;

	public String toJson()
            throws Exception {
        return toJsonObject().toJSONString();
    }
    
    public Payment() {
    	
    }

    public Payment(Builder builder) {
		super();
		this.ibmTransactionId = builder.ibmTransactionId;
		this.ibmStatus = builder.ibmStatus;
		this.userId = builder.userId;
		this.merchantId = builder.merchantId;
		this.transactionId = builder.transactionId;
		this.orderId = builder.orderId;
		this.bankTxnId = builder.bankTxnId;
		this.txnAmount = builder.txnAmount;
		this.currency = builder.currency;
		this.status = builder.status;
		this.respCode = builder.respCode;
		this.respMsg = builder.respMsg;
		this.txnDate = builder.txnDate;
		this.gatewayName = builder.gatewayName;
		this.bankName = builder.bankName;
		this.paymentMode = builder.paymentMode;
		this.checksumHash = builder.checksumHash;
		this.checksumVerified = builder.checksumVerified;
		this.ibmReason = builder.ibmReason;
		this.productId = builder.productId;
		this.contentId = builder.contentId;
		this.subscriptionId = builder.subscriptionId;
		this.requestType = builder.requestType;
		this.src = builder.src;
		this.contentType = builder.contentType;
		this.subscriberId = builder.subscriberId;
		this.contentTitle = builder.contentTitle;
	}



	public JSONObject toJsonObject() {
        JSONObject jsonObj = super.toJsonObject();
        jsonObj.put("ibmTransactionId", ibmTransactionId);
        jsonObj.put("ibmStatus", ibmStatus);
        jsonObj.put("userId", userId);
        jsonObj.put("merchantId", merchantId);
        jsonObj.put("transactionId", transactionId);
        jsonObj.put("orderId", orderId);
        jsonObj.put("bankTxnId", bankTxnId);
        jsonObj.put("txnAmount", txnAmount);
        jsonObj.put("currency", currency);
        jsonObj.put("status", status);
        jsonObj.put("respCode", respCode);
        jsonObj.put("respMsg", respMsg);
        jsonObj.put("txnDate", txnDate);
        jsonObj.put("gatewayName", gatewayName);
        jsonObj.put("bankName", bankName);
        jsonObj.put("paymentMode", paymentMode);
        jsonObj.put("checksumHash", checksumHash);
        jsonObj.put("ibmReason", ibmReason);
        jsonObj.put("cid", contentId);
        jsonObj.put("subscriptionId", subscriptionId);
        jsonObj.put("checksumVerified", checksumVerified);
        jsonObj.put("src", src);
        jsonObj.put("subscriberId", subscriberId);
        
        if(null != contentTitle) {
        	jsonObj.put("contentTitle", contentTitle);
        }
        if(null != contentType) {
        	jsonObj.put("contentType", contentType.name());
        }
        if(null != productId) {
            jsonObj.put("productId", productId.getId());
        }
        if(null != requestType) {
            jsonObj.put("requestType", requestType.name());
        }
        return jsonObj;
    }

    public void fromJson(String json) throws Exception {
        JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(json);
        fromJsonObject(jsonObj);
    }

    public void fromJsonObject(JSONObject jsonObj) {

    	super.fromJsonObject(jsonObj);
    	if(jsonObj.get("ibmTransactionId") != null) {
    		setIbmTransactionId((String)jsonObj.get("ibmTransactionId"));
    	}
    	if(jsonObj.get("ibmStatus") != null) {
    		setIbmStatus((String)jsonObj.get("ibmStatus"));
    	}
    	if(jsonObj.get("userId") != null) {
    		setUserId((String)jsonObj.get("userId"));
    	}
    	if(jsonObj.get("merchantId") != null) {
    		setMerchantId((String)jsonObj.get("merchantId"));
    	}
    	if(jsonObj.get("transactionId") != null) {
    		setTransactionId((String)jsonObj.get("transactionId"));
    	}
    	if(jsonObj.get("orderId") != null) {
    		setOrderId((String)jsonObj.get("orderId"));
    	}
    	if(jsonObj.get("bankTxnId") != null) {
    		setBankTxnId((String)jsonObj.get("bankTxnId"));
    	}
    	if(jsonObj.get("txnAmount") != null) {
    		setTxnAmount((String)jsonObj.get("txnAmount"));
    	}
    	if(jsonObj.get("currency") != null) {
    		setCurrency((String)jsonObj.get("currency"));
    	}
    	if(jsonObj.get("status") != null) {
    		setStatus((String)jsonObj.get("status"));
    	}
    	if(jsonObj.get("respCode") != null) {
    		setRespCode((String)jsonObj.get("respCode"));
    	}
    	if(jsonObj.get("respMsg") != null) {
    		setRespMsg((String)jsonObj.get("respMsg"));
    	}
    	if(jsonObj.get("txnDate") != null) {
    		setTxnDate((String)jsonObj.get("txnDate"));
    	}
    	if(jsonObj.get("gatewayName") != null) {
    		setGatewayName((String)jsonObj.get("gatewayName"));
    	}
    	if(jsonObj.get("bankName") != null) {
    		setBankName((String)jsonObj.get("bankName"));
    	}
    	if(jsonObj.get("paymentMode") != null) {
    		setPaymentMode((String)jsonObj.get("paymentMode"));
    	}
    	if(jsonObj.get("checksumHash") != null) {
    		setChecksumHash((String)jsonObj.get("checksumHash"));
    	}
    	if(jsonObj.get("ibmReason") != null) {
    		setIbmReason((String)jsonObj.get("ibmReason"));
    	}
    	if(jsonObj.get("cid") != null) {
    		setContentId((String)jsonObj.get("cid"));
    	}
    	if(jsonObj.get("subscriptionId") != null) {
    		setSubscriptionId((String)jsonObj.get("subscriptionId"));
    	}
    	if(jsonObj.get("checksumVerified") != null) {
    		setChecksumVerified((Boolean)jsonObj.get("checksumVerified"));
    	}
    	if(jsonObj.get("productId") != null) {
    		Object prodIdValue = jsonObj.get("productId");
			if (prodIdValue instanceof Number) {
				int prodId;
				prodId=((Number) prodIdValue).intValue();
				setProductId(PaymentProductId.getById(prodId));
			}
    	}
    	if(jsonObj.get("requestType") != null) {
    		setRequestType(PaymentRequestType.getByName((String)jsonObj.get("requestType")));
    	}
    	if(jsonObj.get("contentType") != null) {
    		setContentType(MusicContentType.getContentTypeId((String)jsonObj.get("contentType")));
    	}
    	Object value = jsonObj.get("src");
    	if(value != null && value instanceof Number) {
    		setSrc(((Number)jsonObj.get("src")).intValue());
    	}
    	if(jsonObj.get("subscriberId") != null) {
            setSubscriptionId((String)jsonObj.get("subscriberId"));
        }
    	
    	if(jsonObj.get("contentTitle") != null) {
            setContentTitle((String)jsonObj.get("contentTitle"));
        }
    }
	
	public String getIbmTransactionId() {
		return ibmTransactionId;
	}
	public void setIbmTransactionId(String ibmnTransactionId) {
		this.ibmTransactionId = ibmnTransactionId;
	}
	public String getIbmStatus() {
		return ibmStatus;
	}
	public void setIbmStatus(String ibmStatus) {
		this.ibmStatus = ibmStatus;
	}

	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBankTxnId() {
		return bankTxnId;
	}
	public void setBankTxnId(String bankTxnId) {
		this.bankTxnId = bankTxnId;
	}
	public String getTxnAmount() {
		return txnAmount;
	}
	public void setTxnAmount(String txnAmount) {
		this.txnAmount = txnAmount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getChecksumHash() {
		return checksumHash;
	}
	public void setChecksumHash(String checksumHash) {
		this.checksumHash = checksumHash;
	}

	public String getIbmReason() {
		return ibmReason;
	}

	public void setIbmReason(String ibmReason) {
		this.ibmReason = ibmReason;
	}

	public boolean isChecksumVerified() {
		return checksumVerified;
	}

	public void setChecksumVerified(boolean checksumVerified) {
		this.checksumVerified = checksumVerified;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public PaymentProductId getProductId() {
		return productId;
	}

	public void setProductId(PaymentProductId productId) {
		this.productId = productId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public PaymentRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(PaymentRequestType requestType) {
		this.requestType = requestType;
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}

	public MusicContentType getContentType() {
		return contentType;
	}

	public void setContentType(MusicContentType contentType) {
		this.contentType = contentType;
	}

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }
	
    public String getContentTitle() {
		return contentTitle;
	}

	public void setContentTitle(String contentTitle) {
		this.contentTitle = contentTitle;
	}
    
    public static class Builder {
    	
    	private String ibmTransactionId;
    	private String ibmStatus;
    	private String userId;
    	private String merchantId;
    	private String transactionId;
    	private String orderId;
    	private String bankTxnId;
    	private String txnAmount;
    	private String currency;
    	private String status;
    	private String respCode;
    	private String respMsg;
    	private String txnDate;
    	private String gatewayName;
    	private String bankName;
    	private String paymentMode;
    	private String checksumHash;
    	private boolean checksumVerified;
    	private String ibmReason;
    	private PaymentProductId productId;
    	private String contentId;
    	private String subscriptionId;
    	private PaymentRequestType requestType;
    	private int src;
    	private MusicContentType contentType;
    	private String subscriberId;
    	private String contentTitle;
    	
    	
    	public Builder() {
    		
    	}
    	
    	public Builder userId(String userId) {
    		this.userId = userId;
    		return this;
    	}
    	public Builder paymentProductId(PaymentProductId paymentProductId) {
    		this.productId = paymentProductId;
    		return this;
    	}
    	public Builder orderId(String orderId) {
    		this.orderId = orderId;
    		return this;
    	}
    	public Builder contentId(String orderId) {
    		this.contentId = orderId;
    		return this;
    	}
    	public Builder contentType(MusicContentType orderId) {
    		this.contentType = orderId;
    		return this;
    	}
    	public Builder bankName(String bankName) {
    		this.bankName = bankName;
    		return this;
    	}
    	
    	public Builder contentTitle(String contentTitle) {
    		this.contentTitle = contentTitle;
    		return this;
    	}
    	
    	public Payment build() {
    		Payment payment = new Payment(this);
    		return payment;
    	}
    }
}
