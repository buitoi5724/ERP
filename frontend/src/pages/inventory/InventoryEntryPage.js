import React, { useState, useEffect } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import InventoryService from "./inventoryService"; // Service layer
import InventoryForm from "./InventoryForm"; // Form nhập / xuất kho
import "./inventory.css";

export default function InventoryEntryPage() {
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [items, setItems] = useState([]);

  const [formVisible, setFormVisible] = useState(false);
  const [formAction, setFormAction] = useState("import"); // import / export

  // Load dữ liệu khi component mount
  useEffect(() => {
    loadProducts();
    loadSuppliers();
    loadItems();
  }, []);

  const loadProducts = () => {
    InventoryService.getAllProducts()
      .then(data => setProducts(data))
      .catch(err => console.error("Lỗi load products:", err));
  };

  const loadSuppliers = () => {
    InventoryService.getAllSuppliers
      ? InventoryService.getAllSuppliers().then(setSuppliers)
      : fetch("/api/suppliers").then(res => res.json()).then(setSuppliers)
          .catch(err => console.error("Lỗi load suppliers:", err));
  };

  const loadItems = () => {
    InventoryService.getAllInventoryItems()
      .then(data => setItems(data))
      .catch(err => console.error("Lỗi load inventory items:", err));
  };

  const openForm = (actionType) => {
    setFormAction(actionType); // import / export
    setFormVisible(true);
  };

  const closeForm = () => {
    setFormVisible(false);
    loadItems(); // reload dữ liệu sau khi đóng form
  };

  const formatDate = value => (value ? new Date(value).toLocaleDateString() : "");

  return (
    <div className="inventory-page">
      <h1 className="inventory-title">Quản lý kho</h1>

      {/* Nút mở form nhập kho / xuất kho */}
      <div className="inventory-actions p-mb-3">
        <Button label="Nhập kho" className="p-button-success p-mr-2" onClick={() => openForm("import")} />
        <Button label="Xuất kho" className="p-button-danger" onClick={() => openForm("export")} />
      </div>

      {/* Bảng hiển thị tồn kho / hàng nhập */}
      <div className="inventory-table-section">
        <h2>Danh sách hàng nhập gần đây</h2>
        <DataTable
          value={items}
          paginator
          rows={10}
          emptyMessage="Không có dữ liệu"
        >
          <Column field="serialNumber" header="Serial" />
          <Column field="batchNumber" header="Batch" />
          <Column
            field="expirationDate"
            header="Hạn SD"
            body={row => formatDate(row.expirationDate)}
          />
          <Column field="status" header="Trạng thái" />
          <Column field="supplierId" header="Supplier ID" />
          <Column field="inventoryId" header="Inventory ID" />
        </DataTable>
      </div>

      {/* Form nhập / xuất kho */}
      {formVisible && (
        <InventoryForm
          visible={formVisible}
          actionType={formAction}
          onClose={closeForm}
          products={products}
          suppliers={suppliers}
        />
      )}
    </div>
  );
}
