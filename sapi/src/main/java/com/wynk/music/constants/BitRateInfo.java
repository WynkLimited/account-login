package com.wynk.music.constants;

import org.json.simple.JSONObject;

/**
 * Created by bhuvangupta on 10/12/13.
 */
public class BitRateInfo {

    private int bitRate;
    private int sampleRate;
    private int bitDepth;
    private int audioChannels;

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(int bitDepth) {
        this.bitDepth = bitDepth;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("bitRate", bitRate);
        jsonObj.put("sampleRate", sampleRate);
        jsonObj.put("bitDepth", bitDepth);
        jsonObj.put("audioChannels", audioChannels);
        return jsonObj;
    }

    public void fromJsonObject(JSONObject jsonObj) {
        if(jsonObj.get("bitRate") instanceof Number) {
            setBitRate(((Number) jsonObj.get("bitRate")).intValue());
        }
        if(jsonObj.get("sampleRate") instanceof Number) {
            setSampleRate(((Number) jsonObj.get("sampleRate")).intValue());
        }
        if(jsonObj.get("bitDepth") instanceof Number) {
            setBitDepth(((Number) jsonObj.get("bitDepth")).intValue());
        }
        if(jsonObj.get("audioChannels") instanceof Number) {
            setAudioChannels(((Number) jsonObj.get("audioChannels")).intValue());
        }
    }
}
