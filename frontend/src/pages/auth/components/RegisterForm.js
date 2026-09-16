import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import "./RegisterForm.css";
import authService from "../services/authService";

const RegisterForm = () => {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        username: "",
        password: "",
        confirmPassword: "",
    });

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    // =========================
    // Xử lý nhập dữ liệu
    // =========================
    const handleChange = (e) => {
        const { name, value } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));

        setError("");
        setSuccess("");
    };

    // =========================
    // Xử lý đăng ký
    // =========================
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Kiểm tra username
        if (!form.username.trim()) {
            setError("Vui lòng nhập username");
            return;
        }

        // Kiểm tra password
        if (!form.password) {
            setError("Vui lòng nhập password");
            return;
        }

        // Kiểm tra độ dài password
        if (form.password.length < 6) {
            setError("Password phải có ít nhất 6 ký tự");
            return;
        }

        // Kiểm tra password nhập lại
        if (!form.confirmPassword) {
            setError("Vui lòng nhập lại password");
            return;
        }

        // Kiểm tra password khớp
        if (form.password !== form.confirmPassword) {
            setError("Password nhập lại không khớp");
            return;
        }

        try {
            setLoading(true);
            setError("");
            setSuccess("");

            // Gọi API đăng ký
            const data = await authService.register(
                form.username.trim(),
                form.password
            );

            console.log("Register response:", data);

            setSuccess(
                "Đăng ký thành công. Đang chuyển đến trang đăng nhập..."
            );

            // Chuyển sang trang Login
            setTimeout(() => {
                navigate("/login");
            }, 1000);

        } catch (error) {
            console.error("Register error:", error);

            if (error.response) {
                const status = error.response.status;

                if (status === 400) {
                    setError(
                        error.response.data?.message ||
                        "Thông tin đăng ký không hợp lệ"
                    );

                } else if (status === 409) {
                    setError("Username đã tồn tại");

                } else {
                    setError(
                        "Đăng ký thất bại. Vui lòng thử lại."
                    );
                }

            } else {
                setError(
                    "Không thể kết nối đến máy chủ."
                );
            }

        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-form-container">

            <div className="register-form-card">

                {/* Tiêu đề */}
                <h2 className="register-form-title">
                    Đăng ký tài khoản
                </h2>

                {/* Thông báo lỗi */}
                {error && (
                    <p className="register-form-error">
                        {error}
                    </p>
                )}

                {/* Thông báo thành công */}
                {success && (
                    <p className="register-form-success">
                        {success}
                    </p>
                )}

                <form onSubmit={handleSubmit}>

                    {/* Username */}
                    <div className="register-form-group">

                        <label className="register-form-label">
                            Username
                        </label>

                        <input
                            className="register-form-input"
                            type="text"
                            name="username"
                            placeholder="Nhập username"
                            value={form.username}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="username"
                        />

                    </div>

                    {/* Password */}
                    <div className="register-form-group">

                        <label className="register-form-label">
                            Password
                        </label>

                        <input
                            className="register-form-input"
                            type="password"
                            name="password"
                            placeholder="Nhập password"
                            value={form.password}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="new-password"
                        />

                    </div>

                    {/* Confirm Password */}
                    <div className="register-form-group">

                        <label className="register-form-label">
                            Nhập lại Password
                        </label>

                        <input
                            className="register-form-input"
                            type="password"
                            name="confirmPassword"
                            placeholder="Nhập lại password"
                            value={form.confirmPassword}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="new-password"
                        />

                    </div>

                    {/* Button */}
                    <button
                        type="submit"
                        className="register-form-button"
                        disabled={loading}
                    >
                        {loading
                            ? "Đang đăng ký..."
                            : "Đăng ký"
                        }
                    </button>

                </form>

            </div>

        </div>
    );
};

export default RegisterForm;