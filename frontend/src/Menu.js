import React from "react";
import { NavLink } from "react-router-dom";

import {
    FaChartLine,
    FaCogs,
    FaUsers,
    FaClock,
    FaTools,
    FaBoxOpen,
    FaClipboardCheck,
    FaShoppingCart,
    FaWarehouse,
    FaUserTie,
    FaBuilding,
    FaSignOutAlt,
    FaTachometerAlt
} from "react-icons/fa";

export default function Menu({ user, onLogout }) {

    const isAdmin =
        user?.roles?.includes("ADMIN");

    return (

        <aside className="menu">

            {/* =========================
                LOGO
            ========================= */}

            <div className="menu-brand">

                <div className="menu-brand-icon">
                    <FaChartLine />
                </div>

                <div className="menu-brand-info">
                    <h1>ERP SHOP</h1>
                    <span>Management System</span>
                </div>

            </div>


            {/* =========================
                MENU
            ========================= */}

            <nav className="menu-nav">

                {/* =========================
                    TỔNG QUAN
                ========================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            TỔNG QUAN
                        </div>

                        <ul>

                            <li>
                                <NavLink
                                    to="/dashboard"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaTachometerAlt />
                                    <span>Dashboard</span>
                                </NavLink>
                            </li>

                        </ul>

                    </div>

                )}


                {/* =========================
                    BÁN HÀNG
                ========================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            BÁN HÀNG
                        </div>

                        <ul>

                            <li>
                                <NavLink
                                    to="/shopping"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaShoppingCart />
                                    <span>Bán hàng</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/customers"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaUsers />
                                    <span>Khách hàng</span>
                                </NavLink>
                            </li>

                        </ul>

                    </div>

                )}


                {/* =========================
                    SẢN PHẨM & KHO
                ========================= */}

                {isAdmin && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            SẢN PHẨM & KHO
                        </div>

                        <ul>

                            <li>
                                <NavLink
                                    to="/products"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaBoxOpen />
                                    <span>Sản phẩm</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/product-cat"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaBoxOpen />
                                    <span>Danh mục sản phẩm</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/inventory"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaWarehouse />
                                    <span>Kho hàng</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/suppliers"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaUsers />
                                    <span>Nhà cung cấp</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/downtime"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaClock />
                                    <span>Thời gian dừng</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/scrap"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaTools />
                                    <span>Hàng lỗi / phế liệu</span>
                                </NavLink>
                            </li>

                        </ul>

                    </div>

                )}


                {/* =========================
                    PHÊ DUYỆT
                ========================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            PHÊ DUYỆT
                        </div>

                        <ul>

                            <li>
                                <NavLink
                                    to="/approval"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaClipboardCheck />
                                    <span>Yêu cầu phê duyệt</span>
                                </NavLink>
                            </li>

                        </ul>

                    </div>

                )}


                {/* =========================
                    NHÂN SỰ
                ========================= */}

                {isAdmin && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            NHÂN SỰ & QUẢN TRỊ
                        </div>

                        <ul>

                            <li>
                                <NavLink
                                    to="/accounts"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaUsers />
                                    <span>Tài khoản</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/employees"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaUserTie />
                                    <span>Nhân viên</span>
                                </NavLink>
                            </li>


                            <li>
                                <NavLink
                                    to="/departments"
                                    className={({ isActive }) =>
                                        isActive
                                            ? "menu-link active"
                                            : "menu-link"
                                    }
                                >
                                    <FaBuilding />
                                    <span>Phòng ban</span>
                                </NavLink>
                            </li>

                        </ul>

                    </div>

                )}

            </nav>


            {/* =========================
                USER
            ========================= */}

            {user && (

                <div className="menu-user">

                    <div className="menu-user-avatar">
                        {(user.username || "U")
                            .charAt(0)
                            .toUpperCase()}
                    </div>

                    <div className="menu-user-info">

                        <strong>
                            {user.username || "User"}
                        </strong>

                        <span>
                            {user.roles?.[0] || "USER"}
                        </span>

                    </div>

                </div>

            )}


            {/* =========================
                LOGIN / LOGOUT
            ========================= */}

            <div className="menu-bottom">

                {user ? (

                    <button
                        onClick={onLogout}
                        className="menu-logout"
                    >
                        <FaSignOutAlt />
                        <span>Đăng xuất</span>
                    </button>

                ) : (

                    <NavLink
                        to="/login"
                        className={({ isActive }) =>
                            isActive
                                ? "menu-link active"
                                : "menu-link"
                        }
                    >
                        <FaCogs />
                        <span>Đăng nhập</span>
                    </NavLink>

                )}

            </div>

        </aside>

    );
}