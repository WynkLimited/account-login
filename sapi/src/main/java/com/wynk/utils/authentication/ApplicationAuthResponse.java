package com.wynk.utils.authentication;

/**
 * @author : Kunal Sharma
 * @since : 23/07/22, Saturday
 **/
public class ApplicationAuthResponse {

    private boolean authenticated;

    public ApplicationAuthResponse(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
