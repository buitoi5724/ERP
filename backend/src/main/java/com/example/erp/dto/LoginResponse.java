package com.example.erp.dto;

public class LoginResponse {

    private String token;
    private UserInfoResponse user;

    public LoginResponse(String token, UserInfoResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserInfoResponse getUser() {
        return user;
    }
}