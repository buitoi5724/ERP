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

const InventoryForm = ({ visible, actionType, onClose, products }) => {

  const [form, setForm] = useState({
    // ===== PRODUCT =====
    selectedProduct: null,
    warehouse: "DEFAULT",

    // ===== QUANTITY & PRICE =====
    quantity: null,
    costPrice: null,
    salePrice: null,

    // ===== STOCK RULE =====
    minStock: 0,
    maxStock: null,

    // ===== STATUS =====
    status: "ACTIVE",
    note: "",

    // ===== IMPORT INFO =====
    date: new Date(),
    batchCode: "",
    mfgDate: null,
    expDate: null,
    receiptCode: "",
    supplier: "",
    createdBy: ""
  });

  const updateForm = (key, value) =>
    setForm(prev => ({ ...prev, [key]: value }));

  // Auto fill price when choose product
  useEffect(() => {
    if (form.selectedProduct) {
      updateForm("costPrice", form.selectedProduct.costPrice || form.selectedProduct.price);
      updateForm("salePrice", form.selectedProduct.salePrice || null);
      updateForm("warehouse", form.selectedProduct.warehouse || "DEFAULT");
    }
  }, [form.selectedProduct]);

  // ================= SUBMIT =================
  const handleSubmit = async () => {
    const {
      selectedProduct,
      quantity,
      costPrice,
      salePrice,
      warehouse,
      minStock,
      maxStock,
      status,
      note,
      date,
      batchCode,
      receiptCode,
      mfgDate,
      expDate,
      supplier,
      createdBy
    } = form;

    if (!selectedProduct) return alert("Vui lòng chọn sản phẩm");
    if (!quantity || quantity <= 0) return alert("Số lượng không hợp lệ");
    if (actionType === "import" && (!costPrice || costPrice <= 0))
      return alert("Giá vốn không hợp lệ");

    const finalBatchId = batchCode || uuidv4();
    const finalReceiptCode = receiptCode || "PN-" + Date.now();

    try {
      if (actionType === "import") {
        await InventoryService.addInventory({
          productId: selectedProduct.productId,
          productCode: selectedProduct.productCode,
          productName: selectedProduct.productName,

          warehouse,
          quantity,

          costPrice,
          salePrice,
          minStock,
          maxStock,
          status,
          note,

          date,
          batchId: finalBatchId,
          receiptCode: finalReceiptCode,
          mfgDate,
          expDate,
          supplier,
          createdBy
        });
        alert("Nhập kho thành công");
      } else {
        await InventoryService.removeInventory({
          productId: selectedProduct.productId,
          warehouse,
          quantity,
          date
        });
        alert("Xuất kho thành công");
      }

      resetForm();
      onClose();
    } catch (err) {
      console.error(err);
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
      supplier: "",
      createdBy: ""
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

        {/* PRODUCT */}
        <div className="form-group">
          <label>Sản phẩm</label>
          <Dropdown
            value={form.selectedProduct}
            options={products}
            optionLabel="productName"
            placeholder="-- Chọn sản phẩm --"
            onChange={(e) => updateForm("selectedProduct", e.value)}
            className="w-full"
          />
        </div>

        {/* WAREHOUSE */}
        <div className="form-group">
          <label>Kho</label>
          <InputText
            value={form.warehouse}
            onChange={(e) => updateForm("warehouse", e.target.value)}
            className="w-full"
          />
        </div>

        {/* IMPORT ONLY */}
        {actionType === "import" && (
          <>
            <div className="form-group">
              <label>Mã phiếu nhập</label>
              <InputText
                value={form.receiptCode}
                onChange={(e) => updateForm("receiptCode", e.target.value)}
                className="w-full"
              />
            </div>

            <div className="form-group">
              <label>Mã lô</label>
              <InputText
                value={form.batchCode}
                onChange={(e) => updateForm("batchCode", e.target.value)}
                className="w-full"
              />
            </div>

            <div className="form-group">
              <label>NSX</label>
              <Calendar value={form.mfgDate} onChange={(e) => updateForm("mfgDate", e.value)} showIcon />
            </div>

            <div className="form-group">
              <label>HSD</label>
              <Calendar value={form.expDate} onChange={(e) => updateForm("expDate", e.value)} showIcon />
            </div>

            <div className="form-group">
              <label>Nhà cung cấp</label>
              <InputText value={form.supplier} onChange={(e) => updateForm("supplier", e.target.value)} />
            </div>

            <div className="form-group">
              <label>Người thực hiện</label>
              <InputText value={form.createdBy} onChange={(e) => updateForm("createdBy", e.target.value)} />
            </div>
          </>
        )}

        {/* PRICE */}
        <div className="form-group">
          <label>Giá vốn</label>
          <InputNumber value={form.costPrice} onValueChange={(e) => updateForm("costPrice", e.value)} min={0} />
        </div>

        <div className="form-group">
          <label>Giá bán</label>
          <InputNumber value={form.salePrice} onValueChange={(e) => updateForm("salePrice", e.value)} min={0} />
        </div>

        {/* STOCK */}
        <div className="form-group">
          <label>Số lượng</label>
          <InputNumber value={form.quantity} onValueChange={(e) => updateForm("quantity", e.value)} min={1} />
        </div>

        <div className="form-group">
          <label>Tồn tối thiểu</label>
          <InputNumber value={form.minStock} onValueChange={(e) => updateForm("minStock", e.value)} min={0} />
        </div>

        <div className="form-group">
          <label>Tồn tối đa</label>
          <InputNumber value={form.maxStock} onValueChange={(e) => updateForm("maxStock", e.value)} min={0} />
        </div>

        {/* STATUS */}
        <div className="form-group">
          <label>Trạng thái</label>
          <Dropdown
            value={form.status}
            options={[
              { label: "Hoạt động", value: "ACTIVE" },
              { label: "Ngừng", value: "INACTIVE" }
            ]}
            onChange={(e) => updateForm("status", e.value)}
          />
        </div>

        <div className="form-group">
          <label>Ghi chú</label>
          <InputText value={form.note} onChange={(e) => updateForm("note", e.target.value)} />
        </div>

        {/* DATE */}
        <div className="form-group">
          <label>Ngày thực hiện</label>
          <Calendar value={form.date} onChange={(e) => updateForm("date", e.value)} showIcon />
        </div>

        <div className="form-actions">
          <Button
            label={actionType === "import" ? "Nhập kho" : "Xuất kho"}
            className={`p-button-${actionType === "import" ? "success" : "danger"}`}
            onClick={handleSubmit}
          />
          <Button label="Hủy" className="p-button-secondary" onClick={onClose} />
        </div>

      </div>
    </Dialog>
  );
};

export default InventoryForm;
