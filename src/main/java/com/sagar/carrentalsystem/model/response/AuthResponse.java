package com.sagar.carrentalsystem.model.response;

public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse() {
    // Default constructor is necessary for serialization
    }

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
