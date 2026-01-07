import { useState, useEffect } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Button } from "primereact/button";
import "./Customers.css";

export default function CustomersForm({ visible, onHide, onSubmit, customer, accounts }) {
  const [data, setData] = useState({
    name: "",
    phone: "",
    address: "",
    accountId: null,
  });

  useEffect(() => {
    if (customer) {
      setData({
        id: customer.id,
        name: customer.name,
        phone: customer.phone,
        address: customer.address,
        accountId: customer.accountId || null,
      });
    } else {
      setData({ name: "", phone: "", address: "", accountId: null });
    }
  }, [customer]);

  const accountOptions = accounts?.map(acc => ({
    id: acc.id,
    name: `${acc.username} | ${acc.email}`,
  })) || [];

  const handleChange = (e, field) => {
    setData({ ...data, [field]: e.target.value });
  };

  const handleSubmit = () => {
    if (!data.name || !data.phone || !data.accountId) {
      alert("Tên, SĐT và tài khoản không được bỏ trống!");
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
      header={customer ? "Cập nhật Customer" : "Thêm Customer"}
      visible={visible}
      style={{ width: "450px" }}
      footer={footer}
      onHide={onHide}
    >
      <div className="field">
        <label>Tên khách hàng</label>
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
