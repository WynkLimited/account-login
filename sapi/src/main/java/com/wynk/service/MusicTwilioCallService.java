package com.wynk.service;

import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.PortalException;
import com.wynk.config.MusicConfig;
import com.wynk.dto.OtpResult;
import com.wynk.server.HttpResponseService;
import com.wynk.utils.EncryptUtils;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.Utils;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MusicTwilioCallService {

    private ExecutorService executorService = null;

    @Autowired
    private AccountService      accountService;

    @Autowired
    private MusicConfig         musicConfig;

    @Autowired
    private OtpService otpService;

    private static final Logger logger          = LoggerFactory
            .getLogger(MusicTwilioCallService.class.getCanonicalName());

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(musicConfig.getTwilioThreadPoolSize());
        logger.info("Twilio executer service initiated");
    }

    public HttpResponse generateOtpCall(String requestPayload, boolean isFourDigitPin, boolean isEncrypted) throws PortalException {
        if(StringUtils.isEmpty(requestPayload))
            return HttpResponseService
                    .createResponse(AccountService.createErrorResponse("BSY007", "Empty request").toJSONString(),
                            HttpResponseStatus.NO_CONTENT);

        JSONObject requestJson = accountService.getJsonObjectFromPayload(requestPayload);

        if(requestJson != null) {
            String msisdn = (String) accountService.getJsonObjectFromPayload(requestPayload).get("msisdn");

            if(StringUtils.isEmpty(msisdn)) {
                return HttpResponseService.createResponse(
                        AccountService.createErrorResponse("BSY001", "unable to determine msisdn").toJSONString(),
                        HttpResponseStatus.NO_CONTENT);
            }
            try {
                msisdn = isEncrypted ? EncryptUtils
                    .decrypt_256(msisdn,
                        EncryptUtils.getDeviceKey())
                    : msisdn;
            }catch (Exception e)
            {
                logger.error("Exception occurred while decrypting msisdn {} ",e);
            }
            msisdn = Utils.normalizePhoneNumber(msisdn);
            OtpResult otpResult = otpService.generateOtp(msisdn, isFourDigitPin, true);
            String otp = otpResult != null ? otpResult.getOtp() : null;

            if(!StringUtils.isBlank(otp)) {

                executorService.submit(new TwilioCall(msisdn, otp));
                logger.info("Twilio call queued for " + msisdn + " with otp" + otp);

            }
        }
        

        JSONObject obj = new JSONObject();
        obj.put("success", true);
        return HttpResponseService.createOKResponse(obj.toJSONString());
    }

    class TwilioCall implements Callable {

        private String msisdn;
        private String otp;

        public TwilioCall(String msisdn, String otp) {
            this.msisdn = msisdn;
            this.otp = otp;
        }

        @Override
        public Object call() throws Exception {

            String url = musicConfig.getTwilioBaseUrl() + "?account=" + musicConfig.getTwilioId() + "&pin=" + musicConfig.getTwilioPin() + "&msisdn=" + msisdn + "&otp=" + otp + "&campaignid="
                    + musicConfig.getTwilioCampaignId();
            logger.info("[{}], url [{}], Initiating twilio call for", new Object[]{ msisdn, url});

          
            int retry = 0;

            while(retry < 3) {
                try {
                    org.apache.http.client.HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    org.apache.http.HttpResponse response = client.execute(get);
                    if(response.getStatusLine().getStatusCode() <= 300 && response.getStatusLine().getStatusCode() >= 200) {
                        logger.info("[{}], url [{}], Successfully generated twilio call for", new Object[]{ msisdn, url});

                        break;
                    }
                    else {
                        logger.error("[{}], retry [{}], Error generating twilio call", new Object[]{ msisdn, retry});
                        ++retry;
                    }
                }
                catch (Exception e) {
                    logger.error("[{}], retry [{}], Error generating twilio call : {}", new Object[]{ msisdn, retry, e.getMessage(), e });
                	
                    
                    LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, 
                            ExceptionTypeEnum.THIRD_PARTY.TWILIO.name(),
                            "",
                            "MusicTwilioCallService.call", 
                            "Error generating twilio call  " + msisdn );
                    
                    ++retry;
                }
            }

            return null;
        }
    }
}