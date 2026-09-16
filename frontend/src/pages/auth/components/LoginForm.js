import React, { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import "./LoginForm.css";

import authService from "../services/authService";
import { AuthContext } from "../context/AuthContext";

const LoginForm = () => {
    const navigate = useNavigate();
    const { login } = useContext(AuthContext);

    const [form, setForm] = useState({
        username: "",
        password: "",
    });

    const [error, setError] = useState("");
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

        if (error) {
            setError("");
        }
    };

    // =========================
    // Xử lý đăng nhập
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

        try {
            setLoading(true);
            setError("");

            // Gọi API đăng nhập
            const data = await authService.login(
                form.username.trim(),
                form.password
            );

            console.log("Login response:", data);

            // Kiểm tra response
            if (!data || !data.user) {
                setError(
                    "Đăng nhập thành công nhưng không nhận được thông tin user"
                );
                return;
            }

            // Cập nhật user vào AuthContext
            login(data.user);

            // Lấy role từ Backend
            let roles = [];

            if (Array.isArray(data.user.roles)) {
                roles = data.user.roles;
            } else if (data.user.role) {
                roles = [data.user.role];
            }

            // Kiểm tra ADMIN
            if (roles.includes("ADMIN")) {
                navigate("/admin");
            } else {
                navigate("/");
            }

        } catch (error) {
            console.error("Login error:", error);

            if (error.response) {
                const status = error.response.status;

                if (status === 401 || status === 403) {
                    setError("Username hoặc password không đúng");

                } else if (status === 400) {
                    setError(
                        error.response.data?.message ||
                        "Thông tin đăng nhập không hợp lệ"
                    );

                } else {
                    setError("Đã xảy ra lỗi từ máy chủ");
                }

            } else {
                setError(
                    "Không thể kết nối đến máy chủ. Vui lòng kiểm tra Backend."
                );
            }

        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-form-container">

            <div className="login-form-card">

                {/* Tiêu đề */}
                <h2 className="login-form-title">
                    Đăng nhập
                </h2>

                {/* Thông báo lỗi */}
                {error && (
                    <p className="login-form-error">
                        {error}
                    </p>
                )}

                <form onSubmit={handleSubmit}>

                    {/* Username */}
                    <div className="login-form-group">

                        <label className="login-form-label">
                            Username
                        </label>

                        <input
                            className="login-form-input"
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
                    <div className="login-form-group">

                        <label className="login-form-label">
                            Password
                        </label>

                        <input
                            className="login-form-input"
                            type="password"
                            name="password"
                            placeholder="Nhập password"
                            value={form.password}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="current-password"
                        />

                    </div>

                    {/* Button */}
                    <button
                        type="submit"
                        className="login-form-button"
                        disabled={loading}
                    >
                        {loading
                            ? "Đang đăng nhập..."
                            : "Đăng nhập"
                        }
                    </button>

                    {/* Register */}
                    <p className="login-form-register">
                        Chưa có tài khoản?{" "}

                        <Link
                            to="/register"
                            className="login-form-register-link"
                        >
                            Đăng ký ngay
                        </Link>
                    </p>

                </form>

            </div>

        </div>
    );
};

export default LoginForm;