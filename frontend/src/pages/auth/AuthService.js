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
//     }
// };

// export default authService;