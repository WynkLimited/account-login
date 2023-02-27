package com.wynk.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by bhuvangupta on 04/02/14.
 */
public abstract class AbstractSMSSender {

    protected Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    public abstract void sendMessage(String msisdn, String fromShortCode, String text, Boolean useDND);

    public abstract void shutdown();

    public abstract String getConnectionPoolStats();

    public abstract String getThreadPoolStats();

    public abstract String getResponseCodeStats();
    
    public abstract String getTodayResponseCodeStats();

    protected Object[] convertToHexString(String input, boolean xmlEncode) {
        if(input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if(xmlEncode) {
            input = xmlEncode(input);
        }
        boolean containNonAscii = false;
        for(int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if(ch >= 0 && ch <= 127) {
                sb.append(ch);
            }
            else {
                containNonAscii = true;
                sb.append("&");
                sb.append(Integer.toHexString((int) ch));
                sb.append(";");
            }
        }
        String msg = sb.toString();
        if(containNonAscii) {
            msg = msg.replaceAll("&#xa;", " ");
        }
        Object[] obj = new Object[2];
        obj[0] = containNonAscii;
        obj[1] = msg;
        return obj;
    }

    protected String xmlEncode(String s) {
        StringBuffer str = new StringBuffer(new String("".getBytes(), Charset.forName("UTF-8")));
        int len = (s != null)? s.length() : 0;
        for(int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch){
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '\r':
                case '\n':
                    str.append("&#x" + Integer.toHexString(ch) + ';');
                    break;
                default:
                    str.append(ch);
            }
        }

        return str.toString();
    }

}
