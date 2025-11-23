import React from "react";
import { Button } from "primereact/button";
import "./Header.css"; // import CSS

const Header = ({ user, onLoginClick, onLogoutClick }) => {
  return (
    <div className="header">
      {!user ? (
        <Button label="Đăng nhập" onClick={onLoginClick} />
      ) : (
        <>
          <span>Xin chào, {user}</span>
          <Button label="Đăng xuất" onClick={onLogoutClick} />
        </>
      )}
    </div>
  );
};

export default Header;
