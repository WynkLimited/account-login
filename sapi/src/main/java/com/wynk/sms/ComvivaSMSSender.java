package com.wynk.sms;

import com.wynk.utils.HttpClient;
import org.apache.commons.lang.StringUtils;

import java.net.URLEncoder;

/**
 * Created by bhuvangupta on 04/02/14.
 */
public class ComvivaSMSSender extends AbstractSMSSender {

    public static void main(String[] args) {

        ComvivaSMSSender test = new ComvivaSMSSender();
        try {
            // test.sendMessage("919899716596","Dear user, this is a final reminder. If the submit Tax is not submited today policy will be terminated.");
            test.sendMessage("9198xxxxxx", "AD-AMUSIC", "Don't be silly :)", true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendMessage(String msisdn, String fromShortCode, String text, Boolean useDND) {
        if(msisdn.startsWith("+")) {
            msisdn = msisdn.substring(1);
        }
        StringBuilder urlBuilder = new StringBuilder("http://125.19.36.147/csms/PushURL.fcgi?");
        try {
            urlBuilder.append("USERNAME=").append("hike1").append("&PASSWORD=").append("hike1").append("&MOBILENO=").append(msisdn).append("&REG_DELIVERY=1");

            urlBuilder.append("&ORIGIN_ADDR=").append(fromShortCode);

            {
                urlBuilder.append("&MESSAGE=").append(URLEncoder.encode(text, "UTF8"));
                urlBuilder.append("&TYPE=0");
            }

            String url = urlBuilder.toString();
            String response = HttpClient.getContent(url.toString(), 10000, null);
            System.out.println(response + " " + response.split(",|:")[1]);
            logger.info("Comviva SMS Response : " + (response.split(",|:")[1]));
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error sending comviva SMS : " + e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        // Implement me
    }

    @Override
    public String getConnectionPoolStats() {
        // Implement me
        return StringUtils.EMPTY;
    }

    @Override
    public String getThreadPoolStats() {
        // Implement me
        return StringUtils.EMPTY;
    }

    @Override
    public String getResponseCodeStats() {
        // Implement me
        return StringUtils.EMPTY;
    }

	@Override
	public String getTodayResponseCodeStats() {
		// Implement me
        return StringUtils.EMPTY;
	}
}
