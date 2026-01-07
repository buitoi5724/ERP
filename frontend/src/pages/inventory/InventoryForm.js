import React, { useState, useEffect } from "react";
import { Dropdown } from "primereact/dropdown";
import { InputNumber } from "primereact/inputnumber";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Calendar } from "primereact/calendar";
import { Dialog } from "primereact/dialog";
import { v4 as uuidv4 } from "uuid";
import InventoryService from "./inventoryService";
import "./inventory.css";

const InventoryForm = ({ visible, actionType, onClose }) => {
  const [form, setForm] = useState({
    selectedProduct: null,
    warehouse: "DEFAULT",
    quantity: null,
    costPrice: null,
    salePrice: null,
    minStock: 0,
    maxStock: null,
    status: "ACTIVE",
    note: "",
    date: new Date(),
    batchCode: "",
    mfgDate: null,
    expDate: null,
    receiptCode: "",
    supplier: null,
    createdBy: "",
    customer: null // chỉ xuất kho liên quan khách hàng
  });

  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [customers, setCustomers] = useState([]);

  const updateForm = (key, value) =>
    setForm(prev => ({ ...prev, [key]: value }));

  // Load dữ liệu khi mở form
  useEffect(() => {
  // Products
  InventoryService.getAllProducts()
    .then(res => setProducts(Array.isArray(res) ? res : res.content || []))
    .catch(err => console.error("Lỗi load products:", err));

  // Suppliers
  fetch("http://localhost:8080/api/suppliers") // URL backend đầy đủ
    .then(res => res.json())
    .then(data => setSuppliers(data)) // đã là array
    .catch(err => console.error("Lỗi load suppliers fetch:", err));

  // Customers
  InventoryService.getAllCustomers()
    .then(res => setCustomers(Array.isArray(res) ? res : res.content || []))
    .catch(err => console.error("Lỗi load customers:", err));
}, []);

  // Auto fill giá và kho khi chọn sản phẩm
  useEffect(() => {
    if (form.selectedProduct) {
      updateForm("costPrice", form.selectedProduct.costPrice || form.selectedProduct.price);
      updateForm("salePrice", form.selectedProduct.salePrice || null);
      updateForm("warehouse", form.selectedProduct.warehouse || "DEFAULT");
    }
  }, [form.selectedProduct]);

 const handleSubmit = async () => {
  if (!form.selectedProduct) return alert("Chọn sản phẩm");
  if (!form.quantity || form.quantity <= 0) return alert("Số lượng không hợp lệ");
  if (actionType === "import" && (!form.costPrice || form.costPrice <= 0))
    return alert("Giá vốn không hợp lệ");

  console.log("FORM SUBMIT:", form); // 🔍 log kiểm tra

  const batchId = form.batchCode || uuidv4();
  const receiptCode = form.receiptCode || `PN-${Date.now()}`;

  try {
    if (actionType === "import") {
      await InventoryService.addInventory({
        productId: form.selectedProduct.id,          // ✅ SỬA
        productName: form.selectedProduct.name,      // ✅ SỬA
        warehouse: form.warehouse,
        quantity: form.quantity,
        costPrice: form.costPrice,
        salePrice: form.salePrice,
        minStock: form.minStock,
        maxStock: form.maxStock,
        status: form.status,
        note: form.note,
        date: form.date,
        batchId,
        receiptCode,
        mfgDate: form.mfgDate,
        expDate: form.expDate,
        supplierId: form.supplier?.id || null,       // ✅ tránh null crash
        createdBy: form.createdBy || "admin"
      });

      alert("Nhập kho thành công!");
    } else {
      await InventoryService.removeInventory({
        productId: form.selectedProduct.id,          // ✅ SỬA
        warehouse: form.warehouse,
        quantity: form.quantity,
        customerId: form.customer?.id || null,
        date: form.date
      });

      alert("Xuất kho thành công!");
    }

    resetForm();
    onClose();
  } catch (err) {
    console.error("LỖI SUBMIT:", err);
    alert(actionType === "import" ? "Nhập kho thất bại" : "Xuất kho thất bại");
  }
};

  const resetForm = () => {
    setForm({
      selectedProduct: null,
      warehouse: "DEFAULT",
      quantity: null,
      costPrice: null,
      salePrice: null,
      minStock: 0,
      maxStock: null,
      status: "ACTIVE",
      note: "",
      date: new Date(),
      batchCode: "",
      mfgDate: null,
      expDate: null,
      receiptCode: "",
      supplier: null,
      createdBy: "",
      customer: null
    });
  };

  return (
    <Dialog
      header={actionType === "import" ? "Nhập kho" : "Xuất kho"}
      visible={visible}
      modal
      className="inventory-dialog"
      onHide={onClose}
    >
      <div className="inventory-form-container">

        {/* Sản phẩm */}
        <div className="form-group">
          <label>Sản phẩm</label>
   <Dropdown
  value={form.selectedProduct}
  options={products}
  optionLabel="name"
  placeholder="-- Chọn sản phẩm --"
  onChange={e => updateForm("selectedProduct", e.value)}
  className="w-full"
/>
        </div>

        {/* Kho */}
        <div className="form-group">
          <label>Kho</label>
          <InputText
            value={form.warehouse}
            onChange={e => updateForm("warehouse", e.target.value)}
            className="w-full"
          />
        </div>

        {/* Nhập kho */}
        {actionType === "import" && (
          <>
            <div className="form-group">
              <label>Nhà cung cấp</label>
        <Dropdown
  value={form.supplier}
  options={suppliers} // ✅ chắc chắn là array
  optionLabel="name"   // tên hiển thị
  placeholder="-- Chọn nhà cung cấp --"
  onChange={e => updateForm("supplier", e.value)}
  className="w-full"
/>
            </div>
            <div className="form-group">
              <label>Mã phiếu nhập</label>
              <InputText value={form.receiptCode} onChange={e => updateForm("receiptCode", e.target.value)} className="w-full"/>
            </div>
            <div className="form-group">
              <label>Mã lô</label>
              <InputText value={form.batchCode} onChange={e => updateForm("batchCode", e.target.value)} className="w-full"/>
            </div>
            <div className="form-group">
              <label>NSX</label>
              <Calendar value={form.mfgDate} onChange={e => updateForm("mfgDate", e.value)} showIcon/>
            </div>
            <div className="form-group">
              <label>HSD</label>
              <Calendar value={form.expDate} onChange={e => updateForm("expDate", e.value)} showIcon/>
            </div>
          </>
        )}

        {/* Xuất kho */}
        {actionType === "export" && (
          <div className="form-group">
            <label>Khách hàng</label>
            <Dropdown
              value={form.customer}
              options={customers}
              optionLabel="name"
              placeholder="-- Chọn khách hàng --"
              onChange={e => updateForm("customer", e.value)}
              className="w-full"
            />
          </div>
        )}

        {/* Số lượng, giá vốn, giá bán */}
        <div className="form-group">
          <label>Số lượng</label>
          <InputNumber value={form.quantity} onValueChange={e => updateForm("quantity", e.value)} min={1} className="w-full"/>
        </div>
        <div className="form-group">
          <label>Giá vốn</label>
          <InputNumber value={form.costPrice} onValueChange={e => updateForm("costPrice", e.value)} min={0} className="w-full"/>
        </div>
        <div className="form-group">
          <label>Giá bán</label>
          <InputNumber value={form.salePrice} onValueChange={e => updateForm("salePrice", e.value)} min={0} className="w-full"/>
        </div>

        <div className="form-actions">
          <Button
            label={actionType === "import" ? "Nhập kho" : "Xuất kho"}
            onClick={handleSubmit}
            className={`p-button-${actionType === "import" ? "success" : "danger"}`}
          />
          <Button label="Hủy" className="p-button-secondary" onClick={onClose} />
        </div>

      </div>
    </Dialog>
  );
};

export default InventoryForm;
