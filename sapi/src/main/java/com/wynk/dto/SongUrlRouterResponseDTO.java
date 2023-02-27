package com.wynk.dto;

import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aakash on 28/06/17.
 */
public class SongUrlRouterResponseDTO {

    private String songUrl;
    private Map<String,String> cookieMap;
    private List<String> wapStreamingUrlList;

    public SongUrlRouterResponseDTO() {
    }

    public SongUrlRouterResponseDTO(String songUrl, Map<String, String> cookieMap) {
        this.songUrl = songUrl;
        this.cookieMap = cookieMap;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public void setCookieMap(Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public List<String> getWapStreamingUrlList() {
        return wapStreamingUrlList;
    }

    public void setWapStreamingUrlList(List<String> wapStreamingUrlList) {
        this.wapStreamingUrlList = wapStreamingUrlList;
    }

    @Override
    public String toString() {
        return "SongUrlRouterResponseDTO{" +
                "songUrl='" + songUrl + '\'' +
                ", cookieMap=" + cookieMap +
                ", wapStreamingUrlList=" + wapStreamingUrlList +
                '}';
    }
}
