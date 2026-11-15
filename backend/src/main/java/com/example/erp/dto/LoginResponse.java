package com.example.erp.dto;

public class LoginResponse {

    private String token;

    private UserInfoResponse user;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserInfoResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }
}