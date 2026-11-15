package com.example.erp.service;

import com.example.erp.dto.ForgotPasswordRequest;
import com.example.erp.dto.ResetPasswordRequest;

public interface PasswordResetService {
    String createResetToken(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}