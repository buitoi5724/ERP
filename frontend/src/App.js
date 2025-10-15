import { Routes, Route } from "react-router-dom";
import Menu from "./Menu";
import AccountComponent from "./pages/account/account";
import Product from "./pages/product/Product";
import "./App.css";
import "primereact/resources/themes/lara-light-cyan/theme.css";
import "primeflex/primeflex.css";
import ProductDetail from "./pages/product/ProductDetail";
import Shopping from "./pages/shopping/shopping";
import CartShopping from "./pages/shopping/cartShopping";
import ShoppingDetail from "./pages/shopping/shoppingDetail";

export default function App() {
  return (
    <div className="app-container">
      <Menu />
      <div className="content">
        <Routes>
          <Route path="/accounts" element={<AccountComponent />} />
          <Route path="/products" element={<Product />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/employees" element={<h1>Employees Page</h1>} />

          {/* 🛍️ Shopping routes */}
          <Route path="/shopping" element={<Shopping />} />
       <Route path="/shopping/:id" element={<ShoppingDetail />} />
          <Route path="/cart" element={<CartShopping />} />

          {/* Trang mặc định */}
          <Route path="*" element={<h1>Welcome BuiToi</h1>} />
        </Routes>
      </div>
    </div>
  );
}
