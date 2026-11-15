import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./RegisterForm.css";
import authService from "../services/authService";

const RegisterForm = ({ isAdmin = false }) => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        username: "",
        email: "", // ✅ Thêm Email
        password: "",
        confirmPassword: "",
        role: "SALES",
        deptCode: "SALES"
    });
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));
        setError("");
        setSuccess("");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.username.trim()) {
            setError("Vui lòng nhập username");
            return;
        }
        // ✅ Kiểm tra Email
        if (!form.email.trim()) {
            setError("Vui lòng nhập email");
            return;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            setError("Email không hợp lệ");
            return;
        }
        if (!form.password) {
            setError("Vui lòng nhập password");
            return;
        }
        if (form.password.length < 6) {
            setError("Password phải có ít nhất 6 ký tự");
            return;
        }
        if (!form.confirmPassword) {
            setError("Vui lòng nhập lại password");
            return;
        }
        if (form.password !== form.confirmPassword) {
            setError("Password nhập lại không khớp");
            return;
        }
        try {
            setLoading(true);
            setError("");
            setSuccess("");
            // ✅ Gửi đủ thông tin lên backend
            const requestData = {
                username: form.username.trim(),
                email: form.email.trim(), // ✅ Gửi Email
                password: form.password,
                role: form.role,
                deptCode: form.deptCode.toUpperCase()
            };
            // Gọi API đăng ký
            const data = await authService.register(requestData);
            console.log("Register response:", data);
            setSuccess("Đăng ký thành công. Đang chuyển đến trang đăng nhập...");
            setTimeout(() => {
                navigate("/login");
            }, 1000);
        } catch (error) {
            console.error("Register error:", error);
            if (error.response) {
                const status = error.response.status;
                if (status === 400) {
                    setError(error.response.data?.message || "Thông tin đăng ký không hợp lệ");
                } else if (status === 409) {
                    setError("Username đã tồn tại");
                } else {
                    setError("Đăng ký thất bại. Vui lòng thử lại.");
                }
            } else {
                setError("Không thể kết nối đến máy chủ.");
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-form-container">
            <div className="register-form-card">
                <h2 className="register-form-title">
                    {isAdmin ? "Tạo tài khoản mới" : "Đăng ký tài khoản"}
                </h2>
                {error && <p className="register-form-error">{error}</p>}
                {success && <p className="register-form-success">{success}</p>}
                <form onSubmit={handleSubmit}>
                    {/* Username */}
                    <div className="register-form-group">
                        <label className="register-form-label">Username</label>
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
                    {/* ✅ Email */}
                    <div className="register-form-group">
                        <label className="register-form-label">Email</label>
                        <input
                            className="register-form-input"
                            type="email"
                            name="email"
                            placeholder="Nhập email nhận thông báo"
                            value={form.email}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="email"
                        />
                    </div>
                    {/* Password */}
                    <div className="register-form-group">
                        <label className="register-form-label">Password</label>
                        <input
                            className="register-form-input"
                            type="password"
                            name="password"
                            placeholder="Nhập password (tối thiểu 6 ký tự)"
                            value={form.password}
                            onChange={handleChange}
                            disabled={loading}
                            autoComplete="new-password"
                        />
                    </div>
                    {/* Confirm Password */}
                    <div className="register-form-group">
                        <label className="register-form-label">Nhập lại Password</label>
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
                    {/* ✅ CHỈ HIỂN THỊ KHI LÀ ADMIN */}
                    {isAdmin && (
                        <>
                            {/* Chọn Vai trò */}
                            <div className="register-form-group">
                                <label className="register-form-label">Vai trò</label>
                                <select
                                    className="register-form-input"
                                    name="role"
                                    value={form.role}
                                    onChange={handleChange}
                                    disabled={loading}
                                >
                                    <option value="SALES">Nhân viên kinh doanh</option>
                                    <option value="ADMIN">Quản trị viên</option>
                                </select>
                            </div>
                            {/* Chọn Phòng ban */}
                            <div className="register-form-group">
                                <label className="register-form-label">Phòng ban</label>
                                <select
                                    className="register-form-input"
                                    name="deptCode"
                                    value={form.deptCode}
                                    onChange={handleChange}
                                    disabled={loading}
                                >
                                    <option value="SALES">Phòng Kinh doanh</option>
                                    <option value="ADMIN">Phòng Quản trị</option>
                                    <option value="WAREHOUSE">Phòng Kho</option>
                                    <option value="BGD">Ban Giám Đốc</option>
                                </select>
                            </div>
                        </>
                    )}
                    {/* Button */}
                    <button
                        type="submit"
                        className="register-form-button"
                        disabled={loading}
                    >
                        {loading
                            ? isAdmin ? "Đang tạo tài khoản..." : "Đang đăng ký..."
                            : isAdmin ? "Tạo tài khoản" : "Đăng ký"
                        }
                    </button>
                </form>
            </div>
        </div>
    );
};

export default RegisterForm;