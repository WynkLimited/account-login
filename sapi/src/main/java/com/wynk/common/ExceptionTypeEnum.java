package com.wynk.common;

public enum ExceptionTypeEnum {
	
	BATCH,
	CODE;
	public enum INFRA { REDIS, MONGO, HORNETQ, SOLR, PYTHON, KAFKA }
	public enum THIRD_PARTY { IBM, GCM, AWS, PAYTM, NDS, DIWALI_LIBRARY, IPAYY, APN, MAXMIND, PUSH_SERVICE, TWILIO, LAPU,WCF,FOLLOW, SOLACE}
}
