package com.wynk.common;

import com.wynk.dto.PackProvider;

public class PackProviderAuthResponse {

    private boolean     authenticated;
    private PackProvider application;

    public PackProviderAuthResponse(boolean authenticated, PackProvider application) {
        this.authenticated = authenticated;
        this.application = application;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public PackProvider getApplication() {
        return application;
    }

    public void setApplication(PackProvider application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "PackProviderAuthResponse{" +
                "authenticated=" + authenticated +
                ", application=" + application +
                '}';
    }
}
