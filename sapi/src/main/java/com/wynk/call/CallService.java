package com.wynk.call;

import com.wynk.config.MusicConfig;
import com.wynk.utils.HttpClient;
import com.wynk.utils.WCFUtils;
import com.wynk.wcf.WCFApisConstants;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class CallService {

    @Autowired
    private WCFUtils wcfUtils;

    @Autowired
    private MusicConfig musicConfig;

    private String wcfSmsDomain;

    private String wcfSmsEndpoint;

    @PostConstruct
    void init() {
        wcfSmsDomain = musicConfig.getWcfSmsDomain();
        wcfSmsEndpoint = musicConfig.getWcfSmsEndpoint();
    }

    public String requestCall(String msisdn, String text, String priority) {
        String payload = createRequestObject(msisdn, text, priority).toJSONString();
        Map<String, String> headerMap = wcfUtils
                .getHeaderMap(WCFApisConstants.METHOD_POST, String.format(wcfSmsEndpoint, ""),
                        createRequestObject(msisdn, text, priority).toJSONString());
        return HttpClient.postData(String.format(wcfSmsEndpoint, wcfSmsDomain), payload, headerMap,
                WCFApisConstants.CONNECTION_TIMEOUT_MILLIS);
    }

    private JSONObject createRequestObject(String msisdn, String text, String priority) {
        JSONObject body = new JSONObject();
        body.put("msisdn", msisdn);
        body.put("text", text);
        body.put("priority", priority);
        return body;
    }
}
