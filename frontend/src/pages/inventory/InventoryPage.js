import React, { useState, useEffect } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip } from "recharts";
import { Button } from "primereact/button";
import { useNavigate } from "react-router-dom";
import InventoryService from "./inventoryService";
import "./inventory.css";

export default function InventoryPages() {
  const [inventory, setInventory] = useState([]);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const navigate = useNavigate();

  const fetchInventory = async () => {
    try {
      const res = await InventoryService.getAllInventory("DEFAULT");
      setInventory(res || []);
    } catch (err) {
      console.error("Lỗi khi lấy inventory:", err);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  const formatNumber = (value) => (value != null ? value : 0);
  const formatCurrency = (value) => (value != null ? `${value}₫` : "");
  const formatDate = (value) => (value ? new Date(value).toLocaleDateString() : "");

  return (
    <div className="inventory-page">
      <h1 className="inventory-title">Quản lý kho</h1>

      {/* Nút chuyển sang trang nhập kho */}
      <div className="inventory-actions">
        <Button label="Nhập kho" onClick={() => navigate("/inventory-entry")} className="p-button-success" />
      </div>

      {/* Chọn sản phẩm để xem biểu đồ */}
      <div className="inventory-chart-section">
        <h2>Tồn kho</h2>
        <select
          className="inventory-select"
          value={selectedProduct ? selectedProduct.productId : ""}
          onChange={(e) => {
            const productId = Number(e.target.value);
            setSelectedProduct(inventory.find(p => p.productId === productId) || null);
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
            <BarChart data={[{ name: selectedProduct.productName, quantity: selectedProduct.quantity || 0 }]}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="quantity" fill="#f97316" />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>

      {/* Bảng tồn kho tổng */}
      {inventory.length > 0 && (
        <div className="inventory-table-section">
          <h2>Danh sách tồn kho</h2>
          <DataTable
            value={inventory}
            stripedRows
            size="small"
            responsiveLayout="scroll"
            emptyMessage="Không có dữ liệu tồn kho"
          >
            <Column field="productName" header="Sản phẩm" frozen />
            <Column
              field="salePrice"
              header="Giá"
              body={(row) => formatCurrency(row.salePrice)}
              style={{ textAlign: "right", width: "120px" }}
            />
            <Column
              field="quantity"
              header="Tồn"
              body={(row) => formatNumber(row.quantity)}
              style={{ textAlign: "center", width: "80px" }}
            />
            <Column
              field="reservedQuantity"
              header="Đã giữ"
              body={(row) => formatNumber(row.reservedQuantity)}
              style={{ textAlign: "center", width: "90px" }}
            />
            <Column
              field="availableQuantity"
              header="Khả dụng"
              body={(row) => formatNumber(row.availableQuantity)}
              style={{ textAlign: "center", width: "90px" }}
            />
            <Column
              field="createdDate"
              header="Ngày tạo tồn"
              body={(row) => formatDate(row.createdDate)}
              style={{ width: "120px" }}
            />
            <Column
              field="lastImportDate"
              header="Nhập gần nhất"
              body={(row) => formatDate(row.lastImportDate)}
              style={{ width: "130px" }}
            />
            <Column
              field="lastExportDate"
              header="Xuất gần nhất"
              body={(row) => formatDate(row.lastExportDate)}
              style={{ width: "130px" }}
            />
            <Column
              field="warehouse"
              header="Kho"
              style={{ width: "100px", textAlign: "center" }}
            />
          </DataTable>
        </div>
      )}
    </div>
  );
}
