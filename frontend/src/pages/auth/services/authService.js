import api from "../../../services/api";
import authStorage from "../utils/authStorage";

const authService = {
    // Đăng nhập
    login: async (username, password) => {
        const response = await api.post("/auth/login", {
            username,
            password
        });
        const data = response.data;
        authStorage.saveToken(data.token);
        authStorage.saveUser(data.user);
        return data;
    },

    // ✅ Đăng ký - SỬA LẠI NHẬN ĐỐI TƯỢNG ĐẦY ĐỦ
    register: async (registerData) => {
        const response = await api.post("/auth/register", registerData);
        return response.data;
    },

    // Đăng xuất
    logout: () => {
        authStorage.clear();
    },

    // Lấy user đang đăng nhập
    getCurrentUser: () => {
        return authStorage.getUser();
    },

    // Quên mật khẩu
    forgotPassword: async (username) => {
        const response = await api.post("/auth/forgot-password", { username });
        return response.data;
    },

    // Đặt lại mật khẩu
    resetPassword: async (token, newPassword) => {
        const response = await api.post("/auth/reset-password", { token, newPassword });
        return response.data;
    }
};

export default authService;