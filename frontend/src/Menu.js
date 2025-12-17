import React from "react";
import { NavLink } from "react-router-dom";
import {
  FaChartLine,
  FaCogs,
  FaUsers,
  FaClock,
  FaTools,
  FaBoxOpen,
} from "react-icons/fa";

export default function Menu() {
  return (
    <div className="menu">
      <h1>MENU</h1>
      <ul>
        <li>
          <NavLink to="/accounts" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaUsers /> Accounts
          </NavLink>
        </li>
        <li>
          <NavLink to="/products" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaChartLine /> Products
          </NavLink>
        </li>
        <li>
          <NavLink to="/shopping" className={({ isActive }) => (isActive ? "active" : "")}>
            🛒 <FaChartLine /> Shopping
          </NavLink>
        </li>
  <li>
  <NavLink to="/inventory" className={({ isActive }) => (isActive ? "active" : "")}>
    <FaUsers /> Inventory
  </NavLink>
</li>

<li>
  <NavLink to="/downtime" className={({ isActive }) => (isActive ? "active" : "")}>
    <FaClock /> Downtime
  </NavLink>
</li>
        <li>
          <NavLink to="/scrap" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaTools /> Scrap
          </NavLink>
        </li>
        <li>
          <NavLink to="/product-cat" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaBoxOpen /> Product Cat
          </NavLink>
        </li>
        <li>
          <NavLink to="/toollife" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaTools /> Tool Life
          </NavLink>
        </li>
        <li>
          <NavLink to="/login" className={({ isActive }) => (isActive ? "active" : "")}>
            <FaCogs /> LogIn
          </NavLink>
        </li>
      </ul>
    </div>
  );
}
