//  COPYRIGHT  (c) 2002 Akamai Technologies Inc.,
//      All Rights Reserved.
//
// PROPRIETARY - AKAMAI AND AUTHORIZED CLIENTS ONLY.
//
// This document contains proprietary information that shall
// be distributed or routed only within Akamai Technologies
// Inc. (Akamai), and its authorized clients, except
// with written permission of Akamai.

// tokenFactory.java - API for generating Akamaized Distributed Auth cookies
//
// $Id: $

package com.akamai.authentication.URLToken;

public class URLTokenFactory {

    private static final String codeVersion = "1.1.7";

    public URLTokenFactory()
    {
    }

    public static String generateURLTokenString(String URL, long window, String salt, String extract, long time)
    {
        URLToken tok = new URLToken(URL, window, salt, extract, time);
        return(tok.getToken());
    }

    public static String generateURLTokenString(String URL, long window, String salt, String extract)
    {
        URLToken tok = new URLToken(URL, window, salt, extract, 0);
        return(tok.getToken());
    }

    public static URLToken generateURLTokenObject(String URL, long window, String salt, String extract, long time)
    {
        URLToken tok = new URLToken(URL, window, salt, extract, time);
        return(tok);
    }

    public static URLToken generateURLTokenObject(String URL, long window, String salt, String extract)
    {
        URLToken tok = new URLToken(URL, window, salt, extract, 0);
        return(tok);
    }

    public static String generateURL(String URL, String param, long window, String salt)
    {
        return generateURL(URL, param, window, salt, null, 0, false);
    }

    public static String generateURL(String URL, String param, long window, String salt, String extract)
    {
        return generateURL(URL, param, window, salt, extract, 0, false);
    }

    public static String generateURL(String URL, String param, long window, String salt, long time)
    {
        return generateURL(URL, param, window, salt, null, time, false);
    }

    public static String generateURL(String URL, String param, long window, String salt, String extract, long time)
    {
        return generateURL(URL, param, window, salt, extract, time, false);
    }

    public static String generateURL(String URL, String param, long window, String salt, String extract, long time, boolean extensionFix)
    {
        if (extensionFix) {
            boolean hasQuery = (URL.indexOf('?') >= 0);

            String baseUrl = URL;
            if (hasQuery) {
                baseUrl = URL.substring(0, URL.indexOf('?'));
            }

            String extension = baseUrl.substring(baseUrl.lastIndexOf('.'));

            String fix = "ext=" + extension;

            String newUrl = "";
            if (hasQuery) {
                newUrl = URL + '&' + fix;
            } else {
                newUrl = URL + '?' + fix;
            }

            URLToken token = new URLToken(newUrl, window, salt, extract, time);

            return generateURL(token, param, true, URL, fix);
        } else {
            URLToken token = new URLToken(URL, window, salt, extract, time);

            return generateURL(token, param, false, URL, "");
        }
    }
    
    private static String generateURL(URLToken token, String param, boolean extensionFix, String url, String fix)
    {
        StringBuffer ret = new StringBuffer(url);

        if (url.indexOf('?') < 0) {
            ret.append('?');
        } else {
            ret.append('&');
        }
        if (param == null || param.length() == 0) {
            param = new String("__gda__");
        }
        if (param.length() < 5 || param.length() > 12) {
            throw new IllegalArgumentException("Parameter must be between 5 and 12 characters in length");
        }
        ret.append(param);
        ret.append('=');
        ret.append(Long.toString(token.getExpires()));
        ret.append('_');
        ret.append(token.getToken());

        if (extensionFix) {
            ret.append("&");
            ret.append(fix);
        }
        
        return(ret.toString());
    }

    public static final String getVersion()
    {
        return codeVersion;
    }


    public static void main(String[] args)
    {
        URLTokenFactory fact = new URLTokenFactory();

        String url = "/srch_hungama/music/64/srch_hungama_2110250.mp3";
        String salt = "b5bp0r74lmu51c4pp";
        String extract = "ef057cd3b";
        long window = 20;
        long time = System.currentTimeMillis();

        System.out.println("Sample URL token generating program.");

        // Example #1: All variables used
        System.out.println("");
        System.out.println("Example #1:");
        System.out.println("  Input:");
        System.out.println("    URL:        " + url);
        System.out.println("    Start time: " + time);
        System.out.println("    Window:     " + window);
        System.out.println("    Expires:    " + (time + window));
        System.out.println("    Salt:       " + salt);
        System.out.println("    Param:      [Default: '__gda__']");
        System.out.println("    Extract:    " + extract);
        System.out.println("  Output:");
        System.out.println("    URL:        " + fact.generateURL(url, "token", window, salt, extract, time));

        // Example #2: Omit extract
        System.out.println("");
        System.out.println("Example #2:");
        System.out.println("  Input:");
        System.out.println("    URL:        " + url);
        System.out.println("    Start time: " + time);
        System.out.println("    Window:     " + window);
        System.out.println("    Expires:    " + (time + window));
        System.out.println("    Salt:       " + salt);
        System.out.println("    Param:      [Default: '__gda__']");
        System.out.println("    Extract:    [None]");
        System.out.println("  Output:");
        System.out.println("    URL:        " + fact.generateURL(url, "token", window, salt, time));

        // Example #3: Use current time
        System.out.println("");
        System.out.println("Example #3:");
        System.out.println("  Input:");
        System.out.println("    URL:        " + url);
        System.out.println("    Start time: [Default: Now]");
        System.out.println("    Window:     " + window);
        System.out.println("    Expires:    [Now + " + window + " seconds]");
        System.out.println("    Salt:       " + salt);
        System.out.println("    Param:      [Default: '__gda__']");
        System.out.println("    Extract:    " + extract);
        System.out.println("  Output:");
        System.out.println("    URL:        " + fact.generateURL(url, "token", window, salt));

        // Example #4: Use URL with query string; omit extract
//        url = "/index.html?xxx=yyy";
//        System.out.println("");
//        System.out.println("Example #4:");
//        System.out.println("  Input:");
//        System.out.println("    URL:        " + url);
//        System.out.println("    Start time: " + time);
//        System.out.println("    Window:     " + window);
//        System.out.println("    Expires:    " + (time + window));
//        System.out.println("    Salt:       " + salt);
//        System.out.println("    Param:      [Default: '__gda__']");
//        System.out.println("    Extract:    [None]");
//        System.out.println("  Output:");
//        System.out.println("    URL:        " + fact.generateURL(url, "token", window, salt, time));
    }
}
