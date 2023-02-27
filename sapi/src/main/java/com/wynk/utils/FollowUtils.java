package com.wynk.utils;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(FollowUtils.class.getCanonicalName());

    public static String getFollowCount(Long count) {
        StringBuilder followersString = new StringBuilder();
        if (count <= 0) {
            return "";
        }
        if (count < 100) {
            followersString.append("10+ ");
        } else if (count < 1000) {
            count /= 100 * 100;
            followersString.append(count);
            followersString.append("+ ");
        } else if (count < 10 * 100 * 1000) {
            count /= 1000;
            followersString.append(count);
            followersString.append("K ");
        } else {
            count /= (10 * 100 * 1000);
            followersString.append(count);
            followersString.append("M ");
        }
        followersString.append("Followers");
        return followersString.toString();
    }

    public static String getFollowCount(String id) {
        try {
            long stime = System.currentTimeMillis();

            String noOfFollower = HttpClient.getData("http://10.1.2.16:8181/graph/v1/follow/count" + "?id=" + id, 1000);
            System.out.println("noOfFollower- " + noOfFollower);
            logger.info(
                    "Time taken in fetching no of follwers data : " + (System.currentTimeMillis()
                            - stime));
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(noOfFollower);
            Long count = ((Number) jsonObject.get("count")).longValue();
            return getFollowCount(count);
        } catch (Exception e) {
            logger.error("Error fetching follow count for the content id " + id);
            return "";
        }
    }
}
