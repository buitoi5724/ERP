import React from "react";
import { Link } from "react-router-dom";
import { FaChartLine, FaCogs, FaUsers, FaClock, FaTools, FaBoxOpen } from "react-icons/fa";

export default function Menu() {
  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h1>MENU</h1>
      </div>
      <nav className="sidebar-nav">
        <ul>
          <MenuItem icon={<FaUsers />} label="Accounts" to="/accounts" highlight />
          <MenuItem icon={<FaChartLine />} label="Edit" to="/plant" />
          <MenuItem icon={<FaChartLine />} label="Setup" to="/analytics" />
          <MenuGroup icon={<FaCogs />} label="Setup">
            <SubMenuItem label="Machine" to="/setup/machine" />
            <SubMenuItem label="Products" to="/setup/products" />
          </MenuGroup>
          <MenuItem icon={<FaUsers />} label="Employees" to="/employees" />
          <MenuItem icon={<FaClock />} label="Downtime" to="/downtime" />
          <MenuItem icon={<FaTools />} label="Scrap" to="/scrap" />
          <MenuItem icon={<FaBoxOpen />} label="Product Cat" to="/product-cat" />
          <MenuItem icon={<FaTools />} label="Tool Life" to="/toollife" />
        </ul>
      </nav>
    </div>
  );
}

function MenuItem({ icon, label, to, highlight }) {
  return (
    <li className={`menu-item ${highlight ? 'highlight' : ''}`}>
      <Link to={to} className="link">
        <span className="icon">{icon}</span>
        {label}
      </Link>
    </li>
  );
}

function MenuGroup({ icon, label, children }) {
  return (
    <>
      <li className="menu-group">
        <span className="icon">{icon}</span>
        {label}
      </li>
      <ul className="submenu-list">{children}</ul>
    </>
  );
}

function SubMenuItem({ label, to }) {
  return (
    <li className="submenu-item">
      <Link to={to} className="link">
        {label}
      </Link>
    </li>
  );
}
