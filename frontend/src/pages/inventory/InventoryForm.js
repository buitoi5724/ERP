import React, { useEffect, useState } from "react";
import { Dialog } from "primereact/dialog";
import { Dropdown } from "primereact/dropdown";
import { InputText } from "primereact/inputtext";
import { InputNumber } from "primereact/inputnumber";
import { Calendar } from "primereact/calendar";
import { Button } from "primereact/button";
import InventoryService from "./inventoryService";
import "./inventory.css";

/* =======================
   CONSTANT
======================= */
const EMPTY_ITEM = {
  productId: null,
  supplierId: null,     // ✅ NCC theo item
  quantity: 1,
  price: 0,
  batchNumber: "",
  manufactureDate: null,
  expirationDate: null
};

const InventoryForm = ({ visible, onClose, type = "IMPORT" }) => {
  const isImport = type === "IMPORT";

  /* =======================
     STATE
  ======================= */
  const [common, setCommon] = useState({
    inventoryId: 1,
    warehouse: "DEFAULT",
    receiptCode: "",
    actionDate: new Date(),
    note: "",
 
    customerId: null // chỉ dùng cho EXPORT
  });

  const [items, setItems] = useState([{ ...EMPTY_ITEM }]);
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [customers, setCustomers] = useState([]);

  /* =======================
     LOAD DATA
  ======================= */
 useEffect(() => {
  const load = async () => {
    try {
      const [p, s, c] = await Promise.all([
        InventoryService.getAllProducts(),
        InventoryService.getAllSuppliers(),
        InventoryService.getAllCustomers()
      ]);

    const normalize = (res) => {
  if (Array.isArray(res)) return res;
  if (Array.isArray(res?.content)) return res.content;
  return [];
};

      setProducts(normalize(p));
      setSuppliers(normalize(s));
setCustomers(normalize(c));
console.log("CUSTOMERS DATA:", normalize(c));
    } catch (e) {
      console.error(e);
    }
  };
  load();
}, []);

  /* =======================
     HANDLER
  ======================= */
  const updateCommon = (key, value) =>
    setCommon(prev => ({ ...prev, [key]: value }));

  const updateItem = (index, key, value) => {
    setItems(prev => {
      const clone = [...prev];
      clone[index] = { ...clone[index], [key]: value };
      return clone;
    });
  };

  const addItem = () =>
    setItems(prev => [...prev, { ...EMPTY_ITEM }]);

  const removeItem = index => {
    if (items.length === 1) return;
    setItems(prev => prev.filter((_, i) => i !== index));
  };

  /* =======================
     VALIDATE
  ======================= */
  const validateForm = () => {
    if (!isImport && !common.customerId) {
      alert("Vui lòng chọn khách hàng");
      return false;
    }

    for (const item of items) {
      if (!item.productId) {
        alert("Chưa chọn sản phẩm");
        return false;
      }

      if (!item.quantity || item.quantity <= 0) {
        alert("Số lượng không hợp lệ");
        return false;
      }

      if (isImport) {
        if (!item.supplierId) {
          alert("Mỗi sản phẩm cần chọn nhà cung cấp");
          return false;
        }

        if (!item.manufactureDate || !item.expirationDate) {
          alert("Cần nhập ngày sản xuất và hạn dùng");
          return false;
        }

        if (item.expirationDate <= item.manufactureDate) {
          alert("Hạn dùng phải sau ngày sản xuất");
          return false;
        }
      }
    }

    return true;
  };

  /* =======================
     SUBMIT
  ======================= */
  const handleSubmit = async () => {
    if (!validateForm()) return;

  const payload = {
    
  receiptCode: common.receiptCode,
  warehouse: common.warehouse,
  date: common.actionDate.toISOString().slice(0, 10),
  note: common.note,

  ...( !isImport && { customerId: common.customerId }),

  items: items.map(item => ({
    productId: item.productId,
    quantity: Number(item.quantity),

    ...(isImport && {
      supplierId: item.supplierId,
      importPrice: Number(item.price),
      batchNumber: item.batchNumber,
      manufactureDate: item.manufactureDate?.toISOString().slice(0, 10),
      expirationDate: item.expirationDate?.toISOString().slice(0, 10)
    })
  }))
};
    try {
      isImport
        ? await InventoryService.importInventory(payload)
        : await InventoryService.exportInventory(payload);

      alert(isImport ? "Nhập kho thành công" : "Xuất kho thành công");
      onClose();
    } catch (e) {
      console.error(e);
      alert("Thao tác thất bại");
    }
  };

  /**Tính Tổng   */
const totalAmount = items.reduce((sum, item) => {
  const qty = Number(item.quantity) || 0;
  const price = Number(item.price) || 0;
  return sum + qty * price;
}, 0);


  /* =======================
     UI
  ======================= */
  return (
    <Dialog
      header={isImport ? "Nhập kho" : "Xuất kho"}
      visible={visible}
      modal
      className="inventory-dialog"
      onHide={onClose}
    >
      {/* ===== THÔNG TIN CHUNG ===== */}
      <div className="inventory-form-container">

        {!isImport && (
          <div className="form-group">
            <label>Khách hàng</label>
<Dropdown
  value={common.customerId}
  options={customers}
  optionLabel="name"
  optionValue="id"
  placeholder="Chọn khách hàng"
  onChange={e => updateCommon("customerId", e.value)}
/>
          </div>
        )}

        <div className="form-group">
          <label>Kho</label>
          <InputText
            value={common.warehouse}
            onChange={e =>
              updateCommon("warehouse", e.target.value)
            }
          />
        </div>

        <div className="form-group">
          <label>Mã phiếu</label>
          <InputText
            value={common.receiptCode}
            onChange={e =>
              updateCommon("receiptCode", e.target.value)
            }
          />
        </div>

        <div className="form-group">
          <label>Ngày</label>
          <Calendar
            value={common.actionDate}
            showIcon
            onChange={e =>
              updateCommon("actionDate", e.value)
            }
          />
        </div>

        <div className="form-group">
          <label>Ghi chú</label>
          <InputText
            value={common.note}
            onChange={e =>
              updateCommon("note", e.target.value)
            }
          />
        </div>
      </div>

      {/* ===== ITEMS ===== */}
      <h4 className="section-title">Danh sách sản phẩm</h4>

 <div className="add-item-wrapper">
  <Button
    label="Thêm sản phẩ"
    icon="pi pi-plus"
    className="add-item-btn"
    onClick={addItem}
  />
</div>

      {items.map((item, index) => (
        <div className="inventory-item-row">

          <Dropdown
            value={item.productId}
            options={products}
            optionLabel="name"
            optionValue="id"
            placeholder="Sản phẩm"
            onChange={e =>
              updateItem(index, "productId", e.value)
            }
          />

          {isImport && (
            <Dropdown
              value={item.supplierId}
              options={suppliers}
              optionLabel="name"
              optionValue="id"
              placeholder="Nhà cung cấp"
              onChange={e =>
                updateItem(index, "supplierId", e.value)
              }
            />
          )}

          <InputNumber
            value={item.quantity}
            min={1}
            placeholder="SL"
            onValueChange={e =>
              updateItem(index, "quantity", e.value)
            }
          />

          {isImport && (
            <>
              <InputNumber
                value={item.price}
                placeholder="Giá nhập"
                onValueChange={e =>
                  updateItem(index, "price", e.value)
                }
              />

              <InputText
                value={item.batchNumber}
                placeholder=" Số Lô "
                onChange={e =>
                  updateItem(index, "batchNumber", e.target.value)
                }
              />

              <Calendar
                value={item.manufactureDate}
                placeholder="Ngày SX"
                showIcon
                onChange={e =>
                  updateItem(index, "manufactureDate", e.value)
                }
              />

              <Calendar
                value={item.expirationDate}
                placeholder="Hạn SD"
                showIcon
                onChange={e =>
                  updateItem(index, "expirationDate", e.value)
                }
              />
            </>
          )}

          <Button
            icon="pi pi-trash"
            className="p-button-danger"
            onClick={() => removeItem(index)}
          />
        </div>
      ))}
{isImport && (
  <div className="inventory-total">
    <span className="total-label">Tổng tiền:</span>
    <span className="total-value">
      {totalAmount.toLocaleString("vi-VN")} ₫
    </span>
  </div>
)}
      <div className="action-buttons">
        <Button
          label={isImport ? "Nhập kho" : "Xuất kho"}
          className={isImport ? "p-button-success" : "p-button-warning"}
          onClick={handleSubmit}
        />

        
        <Button
          label="Hủy"
          className="p-button-secondary"
          onClick={onClose}
        />
        
      </div>
    </Dialog>
  );
};

export default InventoryForm;
