import React, { useState } from "react";
import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";

const LoginForm = ({ visible, onClose, onLoginSuccess }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // Ở đây giả lập login thành công
    if (username.trim() !== "") {
      onLoginSuccess(username);
      onClose();
    }
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
