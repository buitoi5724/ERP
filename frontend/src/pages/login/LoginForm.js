import React, { useState } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";

const LoginForm = ({ visible, onClose, onLoginSuccess }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

const handleSubmit = (e) => {
  e.preventDefault();
  if (!username.trim()) return;


  // Giả lập danh sách user
  const users = [
    { username: "admin", password: "123", role: "admin" },
    { username: "user1", password: "123", role: "user" },
    { username: "user2", password: "123", role: "user" }
  ];

  
  // Tìm user hợp lệ
  const foundUser = users.find(
    (u) => u.username === username && u.password === password
  );

  if (!foundUser) {
    alert("Username hoặc password không đúng!");
    return;
  }

  onLoginSuccess(foundUser);
  onClose();
};

  return (
    <Dialog header="Đăng nhập" visible={visible} onHide={onClose}>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        /><br/><br/>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        /><br/><br/>
        <Button label="Đăng nhập" type="submit" />
      </form>
    </Dialog>
  );
};

export default LoginForm;
