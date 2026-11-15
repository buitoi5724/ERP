import React, { useState } from "react";
import { Link } from "react-router-dom";
import authService from "../services/authService";
import "./AuthPages.css";

const ForgotPasswordPage = () => {
    const [username, setUsername] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        if (!username.trim()) {
            setError("Vui lòng nhập tên đăng nhập");
            return;
        }

        try {
            setLoading(true);
            const result = await authService.forgotPassword(username.trim());
            // Hiển thị thông báo từ Backend (không hiện mã)
            setSuccess(result?.message || "✅ Đã gửi mã đặt lại mật khẩu về email của bạn");
        } catch (err) {
            console.error("Forgot password error:", err);
            setError(
                err?.response?.data?.message ||
                err?.message ||
                "Có lỗi xảy ra, vui lòng thử lại"
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page-container">
            <div className="auth-page-card">
                <h2 className="auth-page-title">Quên mật khẩu</h2>

                {error && <p className="auth-page-error">{error}</p>}
                {success && (
                    <div className="auth-page-success-box">
                        <p className="auth-page-success">{success}</p>
                        <p className="auth-page-note">
                            Mã có hiệu lực trong 1 giờ. Vui lòng kiểm tra hộp thư email của bạn.
                        </p>
                        <Link to="/reset-password" className="auth-page-link">
                            → Đi đến trang nhập mã
                        </Link>
                    </div>
                )}

                {!success && (
                    <form onSubmit={handleSubmit}>
                        <div className="auth-page-group">
                            <label className="auth-page-label">Tên đăng nhập</label>
                            <input
                                className="auth-page-input"
                                type="text"
                                placeholder="Nhập tên đăng nhập của bạn"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                disabled={loading}
                                autoComplete="username"
                            />
                        </div>
                        <button
                            type="submit"
                            className="auth-page-button"
                            disabled={loading}
                        >
                            {loading ? "Đang xử lý..." : "Gửi yêu cầu"}
                        </button>
                    </form>
                )}

                <p className="auth-page-footer">
                    <Link to="/login" className="auth-page-link">
                        ← Quay lại đăng nhập
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;