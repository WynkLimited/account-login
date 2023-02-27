package com.wynk.solace;

import com.google.common.collect.ImmutableMap;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.newcode.utils.JsonUtils;
import com.wynk.solace.dto.SolaceNumberData;
import com.wynk.solace.dto.SolaceUserData;
import com.wynk.utils.HttpClient;
import com.wynk.utils.LogstashLoggerUtils;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class SolaceQueueMessageProducer {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SolaceQueueMessageProducer.class.getCanonicalName());


    private final static String EAST_TOPIC = "WynkTriggerEastTopic";

    private final static String WEST_TOPIC = "WynkTriggerWestTopic";

    private final static String NORTH_TOPIC = "WynkTriggerNorthTopic";

    private final static String SOUTH_TOPIC = "WynkTriggerSouthTopic";

    private final static String solaceQueueHost = "https://esb.airtel.in/services/"; // HTTP endpoint for prod environment.

    private final static String vpnName = "AppsVPN";
    private final static String publisherUserName = "wynk";
    private final static String publisherPassword = "5tTSQeyK";

    private final static HttpClient httpClient = new HttpClient();

    private static Map<String, String> solaceHubTopicMapping = new HashMap<>();

    static {
        solaceHubTopicMapping.put("NORTH", NORTH_TOPIC);
        solaceHubTopicMapping.put("EAST", EAST_TOPIC);
        solaceHubTopicMapping.put("WEST", WEST_TOPIC);
        solaceHubTopicMapping.put("SOUTH", SOUTH_TOPIC);
    }

    public void publishMessage(SolaceUserData solaceUser) {
        try {
            String topicSelection = StringUtils.EMPTY;
            String topic = StringUtils.EMPTY;
            if (solaceUser != null && solaceUser.getCircle() != null && solaceUser.getUserType() != null) {
                topicSelection = StringUtils.upperCase(solaceUser.getCircle().getSolaceTopic());
                logger.debug("TopicSelection :{}", topicSelection);
                topic = solaceHubTopicMapping.get(topicSelection);
                logger.debug("Solace topic is :{}", topic);
            }
            if (StringUtils.isNotBlank(topic)) {
                logger.info(String.format("Publishing event for new user with uid : %s to topic %s", solaceUser.getuId(), topic));
                String msisdn = solaceUser.getMsisdn();
                msisdn = msisdn.substring(1);
                String is_wynk = "1";
                String lob = "MOBILITY";
                String subscriberCode = "8031D0EC4DA29CE0479ACAD2C41B7AA505338E5D0C2F1E84D93F940BED642D71";

                logger.debug("MSISDN is: {}", msisdn);
                SolaceNumberData solaceNumberData = new SolaceNumberData(subscriberCode, lob, msisdn, is_wynk, String.valueOf(solaceUser.getCircle().getCircleId()));
                String url = solaceQueueHost + topic;
                URI uri = new URI(url);
                logger.info("The URI for solace is : {}", uri);
                String payload = JsonUtils.GSON.toJson(solaceNumberData);

                logger.info("The payload is: {}", payload);
                String response = httpClient.postData(url, publisherUserName, publisherPassword, false, "application/json", payload, 5000);
                LogstashLoggerUtils.createSolaceLog(response, url, solaceNumberData, solaceUser.getuId());
                /*
                RequestEntity<String> requestEntity = new RequestEntity<String>(payload, requestHeaders, HttpMethod.POST, uri);
                logger.info(String.format("Solace Request : [%s]", requestEntity));
                ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
                logger.info("Solace Status code : {}",response.getStatusCode());
                */
                logger.info(String.format("Solace Response : [%s]", response));
                logger.info("Published Solace event for Msisdn: {}", solaceUser.getMsisdn());
            } else {
                logger.info(String.format("Unable to publish event for new user with uid : %s to User : %s", solaceUser.getuId(), solaceUser.toString()));
            }
        } catch (Exception e) {
      LogstashLoggerUtils.createLogstashExceptionLog(
          e,
          ExceptionTypeEnum.THIRD_PARTY.SOLACE.name(),
          solaceUser.getuId(),
          "SolaceQueueMessageProducer.publishMessage",
          e.getMessage());
      logger.error(
          MarkerFactory.getMarker("SOLACE_QUEUE_ERROR"), "Exception occurred in solace.", e);
    }
    }

}
