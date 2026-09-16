import api from "../../../services/api";
import authStorage from "../utils/authStorage";


const authService = {


    // Đăng nhập
    login: async (username, password) => {


        const response =
            await api.post(
                "/auth/login",
                {
                    username,
                    password
                }
            );


        const data = response.data;


        // lưu token
        authStorage.saveToken(
            data.token
        );


        // lưu user
        authStorage.saveUser(
            data.user
        );


        return data;

    },



    // Đăng ký tài khoản
    register: async (username, password) => {


        const response =
            await api.post(
                "/auth/register",
                {
                    username,
                    password
                }
            );


        return response.data;

    },



    // Đăng xuất
    logout: () => {

        authStorage.clear();

    },



    // lấy user đang đăng nhập
    getCurrentUser: () => {

        return authStorage.getUser();

    }


};


export default authService;