// import api from "./api";

// const authService = {
//     login: async (username, password) => {
//         const response = await api.post("/auth/login", {
//             username,
//             password
//         });
//         localStorage.setItem("token", response.data.token);
//         localStorage.setItem("user", JSON.stringify(response.data.user));
//         return response.data;
//     },

//     register: async (username, password) => {
//         const response = await api.post("/auth/register", {
//             username,
//             password
//         });
//         return response.data;
//     },

//     logout: () => {
//         localStorage.removeItem("token");
//         localStorage.removeItem("user");
//     },

//     getCurrentUser: () => {
//         return JSON.parse(localStorage.getItem("user"));
//     },

//     // ==============================================
//     // ✅ THÊM MỚI: Chức năng quên mật khẩu
//     // ==============================================

//     // Yêu cầu tạo token đặt lại mật khẩu
//     forgotPassword: async (username) => {
//         const response = await api.post("/auth/forgot-password", {
//             username
//         });
//         return response.data;
//     },

//     // Đặt lại mật khẩu mới bằng token
//     resetPassword: async (token, newPassword) => {
//         const response = await api.post("/auth/reset-password", {
//             token,
//             newPassword
//         });
//         return response.data;
//     }
// };

// export default authService;