package com.wynk.config;

public class StatsdConfig {

    // statsd properties
    private String statsdServerHost;
    private int    statsdServerPort;

    public String getStatsdServerHost() {
        return statsdServerHost;
    }

    public void setStatsdServerHost(String statsdServerHost) {
        this.statsdServerHost = statsdServerHost;
    }

    public int getStatsdServerPort() {
        return statsdServerPort;
    }

    public void setStatsdServerPort(int statsdServerPort) {
        this.statsdServerPort = statsdServerPort;
    }

}
