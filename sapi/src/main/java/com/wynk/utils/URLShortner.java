package com.wynk.utils;

import com.wynk.db.ShardedRedisServiceManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Random;

/*
* URL Shortener
*/
public class URLShortner {
    //storage for generated keys
    private HashMap keyMap;  //key-url map
    private char urlChars[]; //character to number mapping
    private Random rndGen;
    private int keyLength;

    private ShardedRedisServiceManager shortUrlRedisServiceManager;
    private String redisHashKeyName;

    private URLShortner() {
        keyMap = new HashMap();
        rndGen = new Random();
        keyLength = 8;
        urlChars = new char[62];
        for (int i = 0; i < 62; i++) {
            int j = 0;
            if (i < 10) {
                j = i + 48;
            }
            else if (i > 9 && i <= 35) {
                j = i + 55;
            }
            else {
                j = i + 61;
            }
            urlChars[i] = (char) j;
        }
    }


    public URLShortner(int length, String redisHashKeyName, ShardedRedisServiceManager redisServiceManager) {
        this();
        this.keyLength = length;
        this.redisHashKeyName = redisHashKeyName;
        this.shortUrlRedisServiceManager = redisServiceManager;
    }


    //shortenURL
    public String shortenURL(String longURL) {
        String shortURL = "";
        if (validateURL(longURL)) {
            longURL = sanitizeURL(longURL);
            String existingUrl = null;
            if(shortUrlRedisServiceManager != null)
                existingUrl = shortUrlRedisServiceManager.hget(redisHashKeyName,longURL);
            if (!StringUtils.isEmpty(existingUrl)) {
                shortURL = existingUrl;
            }
            else {
                shortURL = getKey(longURL);
            }
        }
        return shortURL;
    }

    public String expandURL(String shortURL) {
        String longURL = "";
        String key = shortURL;
        longURL = (String) keyMap.get(key);
        return longURL;
    }

    boolean validateURL(String url) {
        //todo:
        return true;
    }

    String sanitizeURL(String url) {
        if(StringUtils.isEmpty(url) || (url.length() < 7))
            return url;

        if (url.substring(0, 7).equals("http://")) {
            url = url.substring(7);
        }

        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /*
    * Get Key method
    */
    private String getKey(String longURL) {
        String key;
        key = generateKey();
        keyMap.put(key, longURL);
        if(shortUrlRedisServiceManager != null)
            shortUrlRedisServiceManager.hset(redisHashKeyName,longURL,key);
        return key;
    }

    //generateKey
    private String generateKey() {
        String key = "";
        boolean flag = true;
        while (flag) {
            key = "";
            for (int i = 0; i <= keyLength; i++) {
                key += urlChars[rndGen.nextInt(62)];
            }
            if (!keyMap.containsKey(key)) {
                flag = false;
            }
        }
        return key;
    }

    //test the code
    public static void main(String args[]) {
        URLShortner u = new URLShortner(10,null,null);
        String urls[] =
                {"www.twangmusic.in/",
                        "www.google.com",
                        "www.airtellive.com.com"
                };

        for (int i = 0; i < urls.length; i++) {
            System.out.println("URL:" + urls[i] + "\tTiny: " + u.shortenURL(urls[i]) + "\tExpanded: " + u.expandURL(u.shortenURL(urls[i])));
        }
    }
}