package com.wynk.service.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynk.dto.TweetMeta;
import com.wynk.utils.HttpClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterService {
    private static Logger logger = LoggerFactory.getLogger(TwitterService.class.getCanonicalName());
    private static final String TWITTER_BASIC_AUTH = "Basic ODVhcTFDSlY3Q1lWQVFNTVNnMHRHaEpLYjprR1drR2Z6d3ZxcHZDVE9QMmxhS1ExR0puSkFlWG1xMm5aMUR1c1ZnMlRkSWNJTmYyQQ==";
    private static final String AUTH_TOKEN_URL = "https://api.twitter.com/oauth2/token?grant_type=client_credentials";
    private static final String TWEETS_API_URL =
            "https://api.twitter.com/1.1/statuses/user_timeline.json?count=100&trim_user=true&exclude_replies=true&screen_name=";
    private static String token = StringUtils.EMPTY;
    private static final int CONNECTION_TIMEOUT_MILLIS = 2000;

    private static void getToken() {
        Map<String, String> headers = new HashMap<>();

        headers.put(
                "authorization", TWITTER_BASIC_AUTH);
        headers.put("content-type", "application/x-www-form-urlencoded");
        String response = HttpClient.postData(AUTH_TOKEN_URL, "", headers, CONNECTION_TIMEOUT_MILLIS);
        if (StringUtils.isNotEmpty(response)) {
            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if (StringUtils.isNotEmpty(jsonObject.get("access_token").toString())) {
                token = jsonObject.get("access_token").getAsString();
            }
        }
    }

    public static String getLatestTweetId(String handle) {
        try {
            if (StringUtils.isEmpty(handle)) {
                return null;
            }
            if (StringUtils.isEmpty(token)) {
                getToken();
            }
            String url = TWEETS_API_URL + handle;
            HashMap<String, String> headers = new HashMap<>();
            headers.put("authorization", "Bearer " + token);
            Type listType = new TypeToken<ArrayList<TweetMeta>>() {
            }.getType();
            List<TweetMeta> tweetMetas = null;
            try {
                String json = HttpClient.getContentWithHeaders(url, headers);
                if (StringUtils.isEmpty(json)) {
                    throw new Exception("Retry");
                }
                tweetMetas = new Gson().fromJson(json, listType);

            } catch (Exception e) {
                logger.info("Retry twitter service");
                getToken();
                headers.put("authorization", "Bearer " + token);
                String json = HttpClient.getContentWithHeaders(url, headers);
                tweetMetas = new Gson().fromJson(json, listType);

            }
            if (CollectionUtils.isNotEmpty(tweetMetas)) {
                for (TweetMeta tweetMeta : tweetMetas) {
                    if (checkIfWynkTweet(tweetMeta.getText())) {
                        return tweetMeta.getId();
                    }
                }
                return tweetMetas.get(0).getId();
            }

        } catch (Exception e) {
            logger.error("Error Getting tweets for handle " + handle);
        }
        return null;
    }

//    private static TweetResponse convertToResponse(TweetMeta tweetMeta) {
//        TweetResponse tweetResponse = new TweetResponse();
//        tweetResponse.setTweetId(tweetMeta.getId());
//        tweetResponse.setText(tweetMeta.getText());
//        if (CollectionUtils.isNotEmpty(tweetMeta.getEntities().getUrls())) {
//            tweetResponse.setTwitterUrl(tweetMeta.getEntities().getUrls().get(0).getExpanded_url());
//        }
//        return tweetResponse;
//    }

    private static boolean checkIfWynkTweet(String text) {
        if (StringUtils.containsIgnoreCase(text, "#wynkmusic") || StringUtils.containsIgnoreCase(text, "@wynkmusic") || StringUtils.containsIgnoreCase(text, "wynk.in")) {
            return true;
        }

        return false;
    }

//    public static void main(String args[]) {
//        getLatestTweetId("TheArijitSingh");
//    }
}
