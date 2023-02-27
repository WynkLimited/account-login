package com.wynk.service.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.wynk.service.api.WCFApiService;
import com.wynk.utils.LogstashLoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class WCFRecommendedPacksCommand extends HystrixCommand<List<Integer>> {
    private static final Logger logger = LoggerFactory.getLogger(WCFRecommendedPacksCommand.class.getCanonicalName());
    String service;
    WCFApiService apiService;
    String msisdn;

    public WCFRecommendedPacksCommand(HystrixCommand.Setter setter,  WCFApiService apiService, String service, String msisdn) {
        super(setter);
        this.msisdn = msisdn;
        this.service = service;
        this.apiService = apiService;
    }

    @Override
    protected List<Integer> run() throws Exception {
        return apiService.getRecommnededProductId(service, msisdn);
    }

    @Override
    protected List<Integer> getFallback() {
        logger.info("Fallback triggered in WCF Packs call for msisdn {}", msisdn, getExecutionException());
        LogstashLoggerUtils.createStandardLog(HystrixUtils.HYSTRIX_FALLBACK_LOGS, "", msisdn, "", HystrixUtils.getCommandProperties(this));
        return Collections.emptyList();
    }
}
