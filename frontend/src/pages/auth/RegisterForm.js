// import React, { useState } from "react";
// import axios from "axios";

// const RegisterForm = () => {

//     const [username, setUsername] = useState("");

//     const [password, setPassword] = useState("");

//     const handleRegister = async (e) => {

//         e.preventDefault();

//         try {

//             await axios.post(
//                 "http://localhost:8080/api/auth/register",
//                 {
//                     username,
//                     password
//                 }
//             );

//             alert("Đăng ký thành công!");

//         } catch (error) {

//             console.error(error);

//             alert("Đăng ký thất bại!");
//         }
//     };

//     return (
//         <form onSubmit={handleRegister}>

//             <input
//                 type="text"
//                 placeholder="Username"
//                 value={username}
//                 onChange={(e) =>
//                     setUsername(e.target.value)
//                 }
//             />

//             <br /><br />

//             <input
//                 type="password"
//                 placeholder="Password"
//                 value={password}
//                 onChange={(e) =>
//                     setPassword(e.target.value)
//                 }
//             />

//             <br /><br />

//             <button type="submit">
//                 Đăng ký
//             </button>

//         </form>
//     );
// };

// export default RegisterForm;