package com.example.erp.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.erp.dto.ForgotPasswordRequest;
import com.example.erp.dto.ResetPasswordRequest;
import com.example.erp.entity.PasswordResetToken;
import com.example.erp.entity.User;
import com.example.erp.entity.Employee; // ✅ Thêm
import com.example.erp.repository.PasswordResetTokenRepository;
import com.example.erp.repository.UserRepository;
import com.example.erp.repository.EmployeeRepository; // ✅ Thêm
import com.example.erp.service.PasswordResetService;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final EmployeeRepository employeeRepository; // ✅ Thêm

    @Value("${spring.mail.from}")
    private String fromEmail;

    // ✅ Thêm EmployeeRepository vào constructor
    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public String createResetToken(ForgotPasswordRequest request) {
        // 1. Tìm user
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + request.getUsername()));

        // 2. Xóa token cũ nếu có
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // 3. Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );
        tokenRepository.save(resetToken);

        // ✅ 4. Lấy EMAIL TỪ EMPLOYEE
        Employee emp = employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin nhân viên"));

        String email = emp.getEmail(); // Email nằm trong Employee
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Nhân viên chưa cập nhật địa chỉ email");
        }

        // ✅ 5. Gửi mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Đặt lại mật khẩu - Hệ thống ERP");
        message.setText(
            "Xin chào " + emp.getEmpName() + ",\n\n" +
            "Mã đặt lại mật khẩu của bạn là:\n" +
            token + "\n\n" +
            "Mã này có hiệu lực trong 1 giờ.\n" +
            "Vui lòng nhập mã này vào trang đặt lại mật khẩu.\n\n" +
            "Trân trọng!"
        );
        mailSender.send(message);

        return "✅ Đã gửi mã đặt lại mật khẩu về email: " + email;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đã hết hạn"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Token đã hết hạn, vui lòng yêu cầu lại");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}