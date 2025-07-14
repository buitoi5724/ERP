import React from "react";
import { Link } from "react-router-dom";
import { FaChartLine, FaCogs, FaUsers, FaClock, FaTools, FaBoxOpen } from "react-icons/fa";

export default function Menu() {
   return (
    <div className="menu">
      <h1>MENU</h1>
      <ul>
        <li><Link to="/accounts"><FaUsers /> Accounts</Link></li>
        <li><Link to="/plant"><FaChartLine /> Edit</Link></li>
        <li><Link to="/analytics"><FaChartLine /> Setup</Link></li>
       
      
        <li><Link to="/employees"><FaUsers /> Employees</Link></li>
        <li><Link to="/downtime"><FaClock /> Downtime</Link></li>
        <li><Link to="/scrap"><FaTools /> Scrap</Link></li>
        <li><Link to="/product-cat"><FaBoxOpen /> Product Cat</Link></li>
        <li><Link to="/toollife"><FaTools /> Tool Life</Link></li>
        <li><Link to="/toollife"><FaTools/> LogIn </Link></li>
      </ul>
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
