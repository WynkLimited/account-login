package com.wynk.service.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.wynk.dto.NdsUserInfo;
import com.wynk.service.api.NdsUserInfoApiService;
import com.wynk.utils.LogstashLoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wynk.service.api.NdsUserInfoApiService.fallbackNDSUserInfo;

public class NDSUserInfoCommand extends HystrixCommand<NdsUserInfo> {
    private static final Logger logger = LoggerFactory.getLogger(NDSUserInfoCommand.class.getCanonicalName());
    String msisdn;
    String uid;
    NdsUserInfoApiService ndsApiService;

    public NDSUserInfoCommand(Setter setter, String msisdn, String uid, NdsUserInfoApiService ndsApiService) {
        super(setter);
        this.msisdn = msisdn;
        this.uid = uid;
        this.ndsApiService = ndsApiService;
    }

    @Override
    protected NdsUserInfo run() throws Exception {
        return ndsApiService.getNdsUserInfoViaHystrix(msisdn);
    }

    @Override
    protected NdsUserInfo getFallback() {
        logger.info("Fallback triggered in NDS call for msisdn {}", msisdn, getExecutionException());
        LogstashLoggerUtils.createStandardLog(HystrixUtils.HYSTRIX_FALLBACK_LOGS, uid, msisdn, "", HystrixUtils.getCommandProperties(this));
        return fallbackNDSUserInfo;
    }
}
