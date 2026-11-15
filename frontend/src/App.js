import { Routes, Route } from "react-router-dom";
import { useContext } from "react";
import Menu from "./Menu";

// =========================================================
// APPROVAL
// =========================================================
import ApprovalPage from "./pages/approval/ApprovalPage";
import ApprovalDetailPage from "./pages/approval/ApprovalDetailPage";
import ApprovalPendingPage from "./pages/approval/ApprovalPendingPage";
import ApprovalHistoryPage from "./pages/approval/ApprovalHistoryPage";

// =========================================================
// ACCOUNT
// =========================================================
import AccountComponent from "./pages/account/AccountComponent";

// =========================================================
// PRODUCT
// =========================================================
import Product from "./pages/product/Product";
import ProductDetail from "./pages/product/ProductDetail";

// =========================================================
// SHOPPING
// =========================================================
import Shopping from "./pages/shopping/shopping";
import CartShopping from "./pages/shopping/cartShopping";
import ShoppingDetail from "./pages/shopping/shoppingDetail";
import InvoiceShopping from "./pages/shopping/InvoiceShopping";

// =========================================================
// INVENTORY
// =========================================================
import Inventory from "./pages/inventory/InventoryPage";
import InventoryEntryPage from "./pages/inventory/InventoryEntryPage";

// =========================================================
// SUPPLIER
// =========================================================
import SupplierPage from "./pages/supplier/SupplierPage";

// =========================================================
// CUSTOMER
// =========================================================
import CustomersPage from "./pages/customers/CustomersPage";

// =========================================================
// AUTH — ✅ SỬA ĐÚNG ĐƯỜNG DẪN
// =========================================================
import LoginPage from "./pages/auth/components/LoginForm";
import RegisterPage from "./pages/auth/components/RegisterForm";
import ForgotPasswordPage from "./pages/auth/pages/ForgotPasswordPage";
import ResetPasswordPage from "./pages/auth/pages/ResetPasswordPage";

// =========================================================
// AUTH CONTEXT
// =========================================================
import { AuthContext } from "./pages/auth/context/AuthContext";

// =========================================================
// CSS
// =========================================================
import "./App.css";
import "primereact/resources/themes/lara-light-cyan/theme.css";
import "primeflex/primeflex.css";

// =========================================================
// APP
// =========================================================
export default function App() {
    const { user, logout } = useContext(AuthContext);

    return (
        <div className="app-container">
            <Menu user={user} onLogout={logout} />
            <div className="content">
                <Routes>
                    {/* AUTH — KHÔNG CẦN ĐĂNG NHẬP */}
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                    <Route path="/reset-password" element={<ResetPasswordPage />} />

                    {/* ADMIN */}
                    {user?.roles?.includes("ADMIN") && (
                        <>
                            <Route path="/accounts" element={<AccountComponent />} />
                            <Route path="/products" element={<Product />} />
                            <Route path="/products/:id" element={<ProductDetail />} />
                            <Route path="/inventory" element={<Inventory />} />
                            <Route path="/inventory-entry" element={<InventoryEntryPage />} />
                            <Route path="/suppliers" element={<SupplierPage />} />
                        </>
                    )}

                    {/* APPROVAL */}
                    {user && (
                        <>
                            <Route path="/approval" element={<ApprovalPage />} />
                            <Route path="/approval/pending" element={<ApprovalPendingPage />} />
                            <Route path="/approval/history" element={<ApprovalHistoryPage />} />
                            <Route path="/approval/:id" element={<ApprovalDetailPage />} />
                        </>
                    )}

                    {/* SHOPPING */}
                    {user && (
                        <>
                            <Route path="/shopping" element={<Shopping />} />
                            <Route path="/shopping/:id" element={<ShoppingDetail />} />
                            <Route path="/cart" element={<CartShopping />} />
                            <Route path="/invoice/:orderId" element={<InvoiceShopping />} />
                            <Route path="/customers" element={<CustomersPage />} />
                        </>
                    )}

                    {/* DEFAULT */}
                    <Route path="*" element={<h1>Welcome BuiToi</h1>} />
                </Routes>
            </div>
        </div>
    );
}