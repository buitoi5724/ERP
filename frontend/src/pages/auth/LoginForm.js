// import React, { useState } from "react";
// import axios from "axios";
// import { Dialog } from "primereact/dialog";
// import { Button } from "primereact/button";
// import { useNavigate } from "react-router-dom"; // 👈 THÊM

// const LoginForm = ({ visible, onClose, onLoginSuccess }) => {

//     const navigate = useNavigate(); // 👈 THÊM

//     const [form, setForm] = useState({
//         username: "",
//         password: ""
//     });

//     const [isRegister, setIsRegister] = useState(false);

//     const handleChange = (e) => {
//         setForm({
//             ...form,
//             [e.target.name]: e.target.value
//         });
//     };

//     const handleSubmit = async (e) => {
//         e.preventDefault();

//         try {
//             const url = isRegister
//                 ? "http://localhost:8080/api/auth/register"
//                 : "http://localhost:8080/api/auth/login";

//             const response = await axios.post(url, form);

//             // REGISTER
//             if (isRegister) {
//                 alert("Đăng ký thành công!");
//                 setIsRegister(false);
//                 setForm({ username: "", password: "" });
//                 return;
//             }

//             // LOGIN
//            const { token, user } = response.data;

// localStorage.setItem("token", token);
// localStorage.setItem("user", JSON.stringify(user));

// const roles = user.roles || [];

// localStorage.setItem("roles", JSON.stringify(roles));

// axios.defaults.headers.common["Authorization"] =
//     "Bearer " + token;

// alert("Đăng nhập thành công!");

// onLoginSuccess(response.data);
// onClose();

// if (roles.includes("ADMIN")) {
//     navigate("/admin");
// } else {
//     navigate("/");
// }

//         } catch (error) {
//             console.error(error);
//             alert(isRegister ? "Đăng ký thất bại!" : "Sai username hoặc password!");
//         }
//     };

//     return (
//         <Dialog
//             header={isRegister ? "Đăng ký tài khoản" : "Đăng nhập"}
//             visible={visible}
//             onHide={onClose}
//         >
//             <form onSubmit={handleSubmit}>

//                 <input
//                     name="username"
//                     type="text"
//                     placeholder="Username"
//                     value={form.username}
//                     onChange={handleChange}
//                 />

//                 <br /><br />

//                 <input
//                     name="password"
//                     type="password"
//                     placeholder="Password"
//                     value={form.password}
//                     onChange={handleChange}
//                 />

//                 <br /><br />

//                 <Button
//                     label={isRegister ? "Đăng ký" : "Đăng nhập"}
//                     type="submit"
//                 />

//             </form>

//             <br />

//             <Button
//                 text
//                 onClick={() => setIsRegister(!isRegister)}
//                 label={
//                     isRegister
//                         ? "Đã có tài khoản? Đăng nhập"
//                         : "Chưa có tài khoản? Đăng ký"
//                 }
//             />
//         </Dialog>
//     );
// };

// export default LoginForm;