
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

    // =========================================================
    // KIỂM TRA QUYỀN
    // =========================================================

    const isAdmin =
        user?.roles?.includes("ADMIN");

    const isApprover =
        user?.approver === true;


    // =========================================================
    // MENU LINK CLASS
    // =========================================================

    const getMenuLinkClass = ({ isActive }) => {

        return isActive
            ? "menu-link active"
            : "menu-link";

    };


    return (

        <aside className="menu">

            {/* =================================================
                LOGO
            ================================================= */}

            <div className="menu-brand">

                <div className="menu-brand-icon">
                    <FaChartLine />
                </div>

                <div className="menu-brand-info">

                    <h1>
                        ERP SHOP
                    </h1>

                    <span>
                        Management System
                    </span>

                </div>

            </div>


            {/* =================================================
                MENU
            ================================================= */}

            <nav className="menu-nav">


                {/* =================================================
                    TỔNG QUAN
                ================================================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            TỔNG QUAN
                        </div>

                        <ul>

                            <li>

                                <NavLink
                                    to="/dashboard"
                                    className={getMenuLinkClass}
                                >

                                    <FaTachometerAlt />

                                    <span>
                                        Dashboard
                                    </span>

                                </NavLink>

                            </li>

                        </ul>

                    </div>

                )}


                {/* =================================================
                    BÁN HÀNG
                ================================================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            BÁN HÀNG
                        </div>

                        <ul>

                            <li>

                                <NavLink
                                    to="/shopping"
                                    className={getMenuLinkClass}
                                >

                                    <FaShoppingCart />

                                    <span>
                                        Bán hàng
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/customers"
                                    className={getMenuLinkClass}
                                >

                                    <FaUsers />

                                    <span>
                                        Khách hàng
                                    </span>

                                </NavLink>

                            </li>

                        </ul>

                    </div>

                )}


                {/* =================================================
                    SẢN PHẨM & KHO
                    CHỈ ADMIN
                ================================================= */}

                {isAdmin && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            SẢN PHẨM & KHO
                        </div>

                        <ul>

                            <li>

                                <NavLink
                                    to="/products"
                                    className={getMenuLinkClass}
                                >

                                    <FaBoxOpen />

                                    <span>
                                        Sản phẩm
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/product-cat"
                                    className={getMenuLinkClass}
                                >

                                    <FaBoxOpen />

                                    <span>
                                        Danh mục sản phẩm
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/inventory"
                                    className={getMenuLinkClass}
                                >

                                    <FaWarehouse />

                                    <span>
                                        Kho hàng
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/suppliers"
                                    className={getMenuLinkClass}
                                >

                                    <FaUsers />

                                    <span>
                                        Nhà cung cấp
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/downtime"
                                    className={getMenuLinkClass}
                                >

                                    <FaClock />

                                    <span>
                                        Thời gian dừng
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/scrap"
                                    className={getMenuLinkClass}
                                >

                                    <FaTools />

                                    <span>
                                        Hàng lỗi / phế liệu
                                    </span>

                                </NavLink>

                            </li>

                        </ul>

                    </div>

                )}


                {/* =================================================
                    PHÊ DUYỆT
                ================================================= */}

                {user && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            PHÊ DUYỆT
                        </div>

                        <ul>


                            {/* =================================================
                                ĐƠN CỦA TÔI
                                TẤT CẢ USER ĐĂNG NHẬP ĐỀU THẤY
                            ================================================= */}

                            <li>

                                <NavLink
                                    to="/approval"
                                    className={getMenuLinkClass}
                                >

                                    <FaClipboardCheck />

                                    <span>
                                        Đơn của tôi
                                    </span>

                                </NavLink>

                            </li>


                            {/* =================================================
                                CHỜ TÔI PHÊ DUYỆT
                                CHỈ APPROVER
                            ================================================= */}

                            {isApprover && (

                                <li>

                                    <NavLink
                                        to="/approval/pending"
                                        className={getMenuLinkClass}
                                    >

                                        <FaClipboardCheck />

                                        <span>
                                            Chờ tôi phê duyệt
                                        </span>

                                    </NavLink>

                                </li>

                            )}


                            {/* =================================================
                                LỊCH SỬ PHÊ DUYỆT
                                CHỈ APPROVER
                            ================================================= */}

                            {isApprover && (

                                <li>

                                    <NavLink
                                        to="/approval/history"
                                        className={getMenuLinkClass}
                                    >

                                        <FaClock />

                                        <span>
                                            Lịch sử phê duyệt
                                        </span>

                                    </NavLink>

                                </li>

                            )}

                        </ul>

                    </div>

                )}


                {/* =================================================
                    NHÂN SỰ & QUẢN TRỊ
                    CHỈ ADMIN
                ================================================= */}

                {isAdmin && (

                    <div className="menu-section">

                        <div className="menu-section-title">
                            NHÂN SỰ & QUẢN TRỊ
                        </div>

                        <ul>

                            <li>

                                <NavLink
                                    to="/accounts"
                                    className={getMenuLinkClass}
                                >

                                    <FaUsers />

                                    <span>
                                        Tài khoản
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/employees"
                                    className={getMenuLinkClass}
                                >

                                    <FaUserTie />

                                    <span>
                                        Nhân viên
                                    </span>

                                </NavLink>

                            </li>


                            <li>

                                <NavLink
                                    to="/departments"
                                    className={getMenuLinkClass}
                                >

                                    <FaBuilding />

                                    <span>
                                        Phòng ban
                                    </span>

                                </NavLink>

                            </li>

                        </ul>

                    </div>

                )}

            </nav>


            {/* =================================================
                USER INFO
            ================================================= */}

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


            {/* =================================================
                LOGIN / LOGOUT
            ================================================= */}

            <div className="menu-bottom">

                {user ? (

                    <button
                        type="button"
                        onClick={onLogout}
                        className="menu-logout"
                    >

                        <FaSignOutAlt />

                        <span>
                            Đăng xuất
                        </span>

                    </button>

                ) : (

                    <NavLink
                        to="/login"
                        className={getMenuLinkClass}
                    >

                        <FaCogs />

                        <span>
                            Đăng nhập
                        </span>

                    </NavLink>

                )}

            </div>

        </aside>

    );

}
