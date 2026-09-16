// // src/services/authService.js

// import axios from "axios";

// const API_BASE_URL = "http://localhost:8080/api";

// const authService = {

//     login: async (username, password) => {

//         const response = await axios.post(
//             `${API_BASE_URL}/auth/login`,
//             {
//                 username,
//                 password
//             }
//         );

//         // Lưu token
//         localStorage.setItem("token", response.data.token);

//         // Lưu user nếu cần
//         localStorage.setItem(
//             "user",
//             JSON.stringify(response.data.user)
//         );

//         return response.data;
//     },

//     logout: () => {
//         localStorage.removeItem("token");
//         localStorage.removeItem("user");
//     },

//     getCurrentUser: () => {
//         return JSON.parse(localStorage.getItem("user"));
//     }
// };

// export default authService;