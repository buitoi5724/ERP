
import { Routes, Route } from "react-router-dom";
import { useContext } from "react";
import Menu from "./Menu";

// ======================
// APPROVAL
// ======================
import ApprovalPage from "./pages/approval/ApprovalPage";
import ApprovalDetailPage from "./pages/approval/ApprovalDetailPage";

import AccountComponent from "./pages/account/AccountComponent";

import Product from "./pages/product/Product";
import ProductDetail from "./pages/product/ProductDetail";

import Shopping from "./pages/shopping/shopping";
import CartShopping from "./pages/shopping/cartShopping";
import ShoppingDetail from "./pages/shopping/shoppingDetail";
import InvoiceShopping from "./pages/shopping/InvoiceShopping";

import Inventory from "./pages/inventory/InventoryPage";
import InventoryEntryPage from "./pages/inventory/InventoryEntryPage";

import SupplierPage from "./pages/supplier/SupplierPage";

import CustomersPage from "./pages/customers/CustomersPage";

// Auth
import LoginPage from "./pages/auth/pages/LoginPage";
import RegisterPage from "./pages/auth/pages/RegisterPage";

import {
    AuthContext
} from "./pages/auth/context/AuthContext";

import "./App.css";

import "primereact/resources/themes/lara-light-cyan/theme.css";
import "primeflex/primeflex.css";


export default function App() {

    const {
        user,
        logout
    } = useContext(AuthContext);


    return (

        <div className="app-container">

            {/* MENU BÊN TRÁI */}
            <Menu
                user={user}
                onLogout={logout}
            />


            {/* NỘI DUNG BÊN PHẢI */}
            <div className="content">

                <Routes>

                    {/* ======================
                        AUTH
                    ======================= */}

                    <Route
                        path="/login"
                        element={<LoginPage />}
                    />

                    <Route
                        path="/register"
                        element={<RegisterPage />}
                    />


                    {/* ======================
                        ADMIN
                    ======================= */}

                    {user?.roles?.includes("ADMIN") && (

                        <>

                            <Route
                                path="/accounts"
                                element={<AccountComponent />}
                            />

                            <Route
                                path="/products"
                                element={<Product />}
                            />

                            <Route
                                path="/products/:id"
                                element={<ProductDetail />}
                            />

                            <Route
                                path="/inventory"
                                element={<Inventory />}
                            />

                            <Route
                                path="/inventory-entry"
                                element={<InventoryEntryPage />}
                            />

                            <Route
                                path="/suppliers"
                                element={<SupplierPage />}
                            />

                        </>

                    )}


                    {/* ======================
                        PHÊ DUYỆT
                        ADMIN + MANAGER
                    ======================= */}

                    {user?.roles?.some(role =>
                        ["ADMIN", "MANAGER"].includes(role)
                    ) && (

                        <>

                            <Route
                                path="/approval"
                                element={<ApprovalPage />}
                            />

                            <Route
                                path="/approval/:id"
                                element={<ApprovalDetailPage />}
                            />

                        </>

                    )}


                    {/* ======================
                        USER + ADMIN
                    ======================= */}

                    {user && (

                        <>

                            <Route
                                path="/shopping"
                                element={<Shopping />}
                            />

                            <Route
                                path="/shopping/:id"
                                element={<ShoppingDetail />}
                            />

                            <Route
                                path="/cart"
                                element={<CartShopping />}
                            />

                            <Route
                                path="/invoice/:orderId"
                                element={<InvoiceShopping />}
                            />

                            <Route
                                path="/customers"
                                element={<CustomersPage />}
                            />

                        </>

                    )}


                    {/* ======================
                        DEFAULT
                    ======================= */}

                    <Route
                        path="*"
                        element={
                            <h1>
                                Welcome BuiToi
                            </h1>
                        }
                    />

                </Routes>

            </div>

        </div>

    );

}

