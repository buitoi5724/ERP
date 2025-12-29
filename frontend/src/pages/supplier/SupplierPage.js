import { useEffect, useState, useRef } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { ConfirmDialog, confirmDialog } from "primereact/confirmdialog";
import accountService from "../account/accountService";
import SupplierForm from "./SupplierForm";
import {
  getSuppliers,
  createSupplier,
  updateSupplier,
  deleteSupplier,
} from "./supplierService";
import "./supplier.css";

export default function SupplierPage() {
  const [suppliers, setSuppliers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [selectedSupplier, setSelectedSupplier] = useState(null);

  const toast = useRef(null);

  const loadSuppliers = () => {
    getSuppliers().then((res) => setSuppliers(res.data));
  };

  const loadAccounts = () => {
    accountService.getAccounts().then((res) => setAccounts(res.data || res));
  };

  useEffect(() => {
    loadSuppliers();
    loadAccounts();
  }, []);

  const openNew = () => {
    setSelectedSupplier(null);
    setFormVisible(true);
  };

  const editSupplier = (row) => {
    setSelectedSupplier(row);
    setFormVisible(true);
  };

  const removeSupplier = (id) => {
    confirmDialog({
      message: "Bạn có chắc muốn xóa?",
      acceptLabel: "Có",
      rejectLabel: "Không",
      accept: () => {
        deleteSupplier(id).then(() => {
          loadSuppliers();
          toast.current.show({
            severity: "success",
            summary: "Đã xóa thành công!",
          });
        });
      },
    });
  };

  const saveData = (data) => {
    const apiCall = data.id
      ? updateSupplier(data.id, data)
      : createSupplier(data);

    apiCall
      .then(() => {
        loadSuppliers();
        setFormVisible(false);
        toast.current.show({
          severity: "success",
          summary: "Lưu thành công!",
        });
      })
      .catch(() =>
        toast.current.show({
          severity: "error",
          summary: "Lỗi khi lưu dữ liệu!",
        })
      );
  };

  const accountTemplate = (row) => {
    const acc = accounts.find((a) => a.id === row.accountId);
    return acc ? `${acc.username} | ${acc.email}` : "Chưa liên kết";
  };

  return (
    <div className="supplier-container">
      <Toast ref={toast} />
      <ConfirmDialog />

      <div className="supplier-header">
        <h2>Quản lý Nhà cung cấp</h2>
        <Button
          label="➕ Thêm mới"
          onClick={openNew}
          className="supplier-add-btn"
        />
      </div>

      <DataTable
        value={suppliers}
        paginator
        rows={10}
        stripedRows
        responsiveLayout="scroll"
      >
        <Column field="name" header="Tên NCC" sortable />
        <Column field="phone" header="SĐT" />
        <Column field="address" header="Địa chỉ" />
        <Column field="taxCode" header="Mã số thuế" />
        <Column header="Tài khoản" body={accountTemplate} sortable />

        <Column
          header="Hành động"
          body={(row) => (
            <>
              <Button
                icon="pi pi-pencil"
                rounded
                text
                onClick={() => editSupplier(row)}
              />
              <Button
                icon="pi pi-trash"
                rounded
                text
                severity="danger"
                onClick={() => removeSupplier(row.id)}
              />
            </>
          )}
        />
      </DataTable>

      <SupplierForm
        visible={formVisible}
        onHide={() => setFormVisible(false)}
        onSubmit={saveData}
        supplier={selectedSupplier}
        accounts={accounts}
      />
    </div>
  );
}
