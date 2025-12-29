import { useState, useEffect } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";
import "./supplier.css";

export default function SupplierForm({ visible, onHide, onSubmit, supplier, accounts }) {

  const [data, setData] = useState({
    name: "",
    phone: "",
    address: "",
    taxCode: "",
    accountId: null,
    active: true,
  });

  useEffect(() => {
    if (supplier) {
      setData({
        id: supplier.id,
        name: supplier.name,
        phone: supplier.phone,
        address: supplier.address,
        taxCode: supplier.taxCode,
        accountId: supplier.accountId || null,
        active: supplier.active ?? true,
      });
    } else {
      setData({
        name: "",
        phone: "",
        address: "",
        taxCode: "",
        accountId: null,
        active: true,
      });
    }
  }, [supplier]);

  // Tạo giá trị option hiển thị: "username | email"
  const accountOptions =
    accounts?.map((acc) => ({
      id: acc.id,
      name: `${acc.username} | ${acc.email}`,
    })) || [];

  const handleChange = (e, field) => {
    setData({ ...data, [field]: e.target.value });
  };

  const validateForm = () => {
    if (!data.name.trim()) return "Tên NCC không được bỏ trống!";
    if (!data.phone.trim()) return "Số điện thoại không được bỏ trống!";
    if (!data.accountId) return "Hãy chọn tài khoản quản lý!";
    return null;
  };

  const handleSubmit = () => {
    const error = validateForm();
    if (error) {
      alert(error);
      return;
    }
    onSubmit(data);
  };

  const footer = (
    <div>
      <Button label="Hủy" className="p-button-text" onClick={onHide} />
      <Button label="Lưu" onClick={handleSubmit} />
    </div>
  );

  return (
    <Dialog
      header={supplier ? "Cập nhật Nhà cung cấp" : "Thêm Nhà cung cấp"}
      visible={visible}
      style={{ width: "450px" }}
      footer={footer}
      onHide={onHide}
    >
      <div className="field">
        <label>Tên nhà cung cấp</label>
        <InputText value={data.name} onChange={(e) => handleChange(e, "name")} />
      </div>

      <div className="field">
        <label>Số điện thoại</label>
        <InputText value={data.phone} onChange={(e) => handleChange(e, "phone")} />
      </div>

      <div className="field">
        <label>Địa chỉ</label>
        <InputText value={data.address} onChange={(e) => handleChange(e, "address")} />
      </div>

      <div className="field">
        <label>Mã số thuế</label>
        <InputText value={data.taxCode} onChange={(e) => handleChange(e, "taxCode")} />
      </div>

      <div className="field">
        <label>Tài khoản quản lý</label>
        <Dropdown
          value={data.accountId}
          options={accountOptions}
          optionLabel="name"
          optionValue="id"
          placeholder="Chọn tài khoản"
          onChange={(e) => handleChange(e, "accountId")}
        />
      </div>
    </Dialog>
  );
}
