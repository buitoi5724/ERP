import { Routes, Route } from "react-router-dom";
import { useState } from "react";
import Menu from "./Menu";
import AccountComponent from "./pages/account/account";
import Product from "./pages/product/Product";
import ProductDetail from "./pages/product/ProductDetail";
import Shopping from "./pages/shopping/shopping";
import CartShopping from "./pages/shopping/cartShopping";
import ShoppingDetail from "./pages/shopping/shoppingDetail";
import InvoiceShopping from "./pages/shopping/InvoiceShopping";
import Header from './pages/login/Header';
import LoginForm from './pages/login/LoginForm';
import Inventory from "./pages/inventory/InventoryPage";

import "./App.css";
import "primereact/resources/themes/lara-light-cyan/theme.css";
import "primeflex/primeflex.css";

export default function App() {
  // trạng thái login
  const [user, setUser] = useState(null);
  const [loginVisible, setLoginVisible] = useState(false);

  // hiển thị dialog login
  const handleLoginClick = () => setLoginVisible(true);
  const handleLoginClose = () => setLoginVisible(false);

  // khi login thành công
  const handleLoginSuccess = (userData) => {
    setUser(userData); // { username, role }
    setLoginVisible(false);
  };

  // logout
const handleLogout = () => {
  setUser(null);       // xóa user → menu/admin/shopping ẩn hết
  setLoginVisible(false); // không mở LoginForm tự động
};

  return (
    <div className="app-container">
<Header
  onLoginClick={handleLoginClick}
  user={user}
  onLogoutClick={handleLogout} // khớp với Header
/>

      {/* menu truyền user + logout */}
      <Menu user={user} onLogout={handleLogout} />

      {/* Login dialog */}
      {!user && (
        <LoginForm
          visible={loginVisible}
          onClose={handleLoginClose}
          onLoginSuccess={handleLoginSuccess}
        />
      )}

      <div className="content">
        <Routes>
          {/* Admin pages */}
          {user?.role === "admin" && (
            <>
              <Route path="/accounts" element={<AccountComponent />} />
              <Route path="/products" element={<Product />} />
              <Route path="/products/:id" element={<ProductDetail />} />
              <Route path="/inventory" element={<Inventory />} />
            </>
          )}

          {/* Shopping (user + admin) */}
          {user && (
            <>
              <Route path="/shopping" element={<Shopping />} />
              <Route path="/shopping/:id" element={<ShoppingDetail />} />
              <Route path="/cart" element={<CartShopping />} />
              <Route path="/invoice/:orderId" element={<InvoiceShopping />} />
            </>
          )}

          {/* Fallback */}
          <Route path="*" element={<h1>Welcome BuiToi</h1>} />
        </Routes>
      </div>
    </div>
  );
}
