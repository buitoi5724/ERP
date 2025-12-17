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
import InventoryForm from "./pages/inventory/InventoryForm";


import "./App.css";
import "primereact/resources/themes/lara-light-cyan/theme.css";
import "primeflex/primeflex.css";

export default function App() {
  const [loginVisible, setLoginVisible] = useState(false);

  const handleLoginClick = () => setLoginVisible(true);
  const handleLoginClose = () => setLoginVisible(false);

  return (
    <div className="app-container">
      <Header onLoginClick={handleLoginClick} />
      <Menu />
      <LoginForm visible={loginVisible} onClose={handleLoginClose} />

      <div className="content">
        <Routes>
          <Route path="/accounts" element={<AccountComponent />} />
          <Route path="/products" element={<Product />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/employees" element={<h1>Employees Page</h1>} />

          {/* 🛍️ Shopping */}
          <Route path="/shopping" element={<Shopping />} />
          <Route path="/shopping/:id" element={<ShoppingDetail />} />
          <Route path="/cart" element={<CartShopping />} />
          <Route path="/invoice/:invoiceId" element={<InvoiceShopping />} />

          {/* 🧺 Inventory */}
      <Route path="/inventory" element={<Inventory />} />

          <Route path="*" element={<h1>Welcome BuiToi</h1>} />
        </Routes>
      </div>
    </div>
  );
}
