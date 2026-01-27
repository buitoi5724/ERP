import React, { useEffect, useState } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip
} from "recharts";

import InventoryService from "./inventoryService";
import InventoryForm from "./InventoryForm";
import "./inventory.css";

export default function InventoryPages() {
  const [inventory, setInventory] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);

  const [formVisible, setFormVisible] = useState(false);
  const [formType, setFormType] = useState("IMPORT"); // IMPORT | EXPORT
const [searchText, setSearchText] = useState("");

 const fetchInventory = async () => {
  try {
    const res = await InventoryService.getAllInventory("DEFAULT");

    console.log("===== RAW RESPONSE FROM BACKEND =====");
    console.log(res);
    console.log("=====================================");

    const data = Array.isArray(res)
      ? res
      : res?.content || [];

    console.log("===== INVENTORY ARRAY USED BY UI =====");
    console.log(data);
    console.log("======================================");

    setInventory(data);
  } catch (err) {
    console.error("Lỗi khi lấy inventory:", err);
  }
};

useEffect(() => {
  InventoryService.getAllInventory("DEFAULT")
    .then(res => {
      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      setInventory(data);
    })
    .catch(err => console.error(err));
}, []);

  const openForm = (type) => {
    setFormType(type);
    setFormVisible(true);
  };

  const closeForm = () => {
    setFormVisible(false);
    fetchInventory(); // reload tồn sau nhập / xuất
  };

  const formatNumber = v => v ?? 0;
  const formatDate = v =>
    v ? new Date(v).toLocaleDateString("vi-VN") : "";


  const filteredInventory = inventory.filter(item =>
  item.productName
    ?.toLowerCase()
    .includes(searchText.toLowerCase())
);

  return (
    <div className="inventory-page">
      <h1 className="inventory-title">Quản lý kho</h1>

      {/* ===== ACTION ===== */}
      <div className="inventory-actions">
        <Button
          label="Nhập kho"
          icon="pi pi-plus"
          className="p-button-success"
          onClick={() => openForm("IMPORT")}
        />
        <Button
          label="Xuất kho"
          icon="pi pi-minus"
          className="p-button-warning"
          onClick={() => openForm("EXPORT")}
        />
      </div>

      {/* ===== BIỂU ĐỒ ===== */}
      <div className="inventory-chart-section">
        <h2>Tồn kho</h2>

        <select
          className="inventory-select"
          value={selectedProduct?.productId || ""}
          onChange={e => {
            const id = Number(e.target.value);
            setSelectedProduct(inventory.find(p => p.productId === id));
          }}
        >
          <option value="">-- Chọn sản phẩm --</option>
          {inventory.map(p => (
            <option key={p.productId} value={p.productId}>
              {p.productName} ({p.quantity || 0})
            </option>
          ))}
        </select>

        {selectedProduct && (
          <ResponsiveContainer width="100%" height={300}>
            <BarChart
              data={[
                {
                  name: selectedProduct.productName,
                  quantity: selectedProduct.quantity || 0
                }
              ]}
            >
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="quantity" />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>

      {/* ===== TABLE ===== */}
     <div className="inventory-table-section">
  <h2>Danh sách tồn kho</h2>

  {/* 🔍 SEARCH */}
  <div className="inventory-search">
    <input
      type="text"
      className="inventory-search-input"
      placeholder="Tìm sản phẩm..."
      value={searchText}
      onChange={e => setSearchText(e.target.value)}
    />
  </div>

  <DataTable
    value={filteredInventory}
    dataKey="productId"
    stripedRows
    responsiveLayout="scroll"
  >
          <Column field="productName" header="Sản phẩm" />
          <Column field="quantity" header="Tồn" body={r => formatNumber(r.quantity)} />
          <Column field="availableQuantity" header="Khả dụng" />
          <Column field="reservedQuantity" header="Đã giữ" />
          <Column field="lastImportDate" header="Nhập gần nhất" body={r => formatDate(r.lastImportDate)} />
          <Column field="lastExportDate" header="Xuất gần nhất" body={r => formatDate(r.lastExportDate)} />
          <Column field="warehouse" header="Kho" />
        </DataTable>
      </div>

      {/* ===== FORM NHẬP / XUẤT ===== */}
      {formVisible && (
        <InventoryForm
          visible={formVisible}
          type={formType}
          onClose={closeForm}
        />
      )}
    </div>
  );
}
