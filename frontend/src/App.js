import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Menu from "./Menu";
import AccountComponent from "./pages/account/account";
import "./App.css";
import ProductsCompoent from "./pages/product/abc";
import "primereact/resources/themes/lara-light-cyan/theme.css";



export default function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Menu />
        <div className="content">
          <Routes>
            <Route path="/accounts" element={<AccountComponent />} />
            <Route path="/products" element={<ProductsCompoent/>} />
            <Route path="/employees" element={<h1>Employees Page</h1>} />
            <Route path="*" element={<h1>Welcome BuiToi</h1>} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}
