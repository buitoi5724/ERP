
import api from "../../services/api";

const accountService = {
    // Lấy danh sách tài khoản
    getAccounts: async () => {
        const response = await api.get("/users");
        return response.data;
    },

    // Lấy tài khoản theo ID
    getAccountById: async (id) => {
        const response = await api.get(`/users/${id}`);
        return response.data;
    },

    // Tạo tài khoản thông thường
    createAccount: async (account) => {
        const response = await api.post("/users", account);
        return response.data;
    },

    // Cập nhật tài khoản
    updateAccount: async (id, account) => {
        const response = await api.put(`/users/${id}`, account);
        return response.data;
    },

    // Xóa tài khoản
    deleteAccount: async (id) => {
        await api.delete(`/users/${id}`);
    },

    // Lấy danh sách phòng ban
    getDepartments: async () => {
        const response = await api.get("/departments");
        return response.data;
    },

    // Tạo tài khoản nhân viên
    createEmployeeAccount: async (data) => {
        const response = await api.post("/users/employees", data);
        return response.data;
    }
};

export default accountService;
