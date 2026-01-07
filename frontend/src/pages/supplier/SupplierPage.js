import { useEffect, useState, useRef } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { ConfirmDialog, confirmDialog } from "primereact/confirmdialog";
import { Toolbar } from "primereact/toolbar";
import { InputText } from "primereact/inputtext";

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
  const [selectedSupplier, setSelectedSupplier] = useState(null);
  const [selectedSuppliers, setSelectedSuppliers] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [filters, setFilters] = useState({
    global: { value: "", matchMode: "contains" },
    name: { value: "", matchMode: "contains" },
    phone: { value: "", matchMode: "contains" },
    address: { value: "", matchMode: "contains" },
    taxCode: { value: "", matchMode: "contains" },
    accountDisplay: { value: "", matchMode: "contains" },
  });

  const toast = useRef(null);

  // Load accounts
  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data || []);
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi", detail: "Không thể load accounts" });
    }
  };

  // Load suppliers
  const loadSuppliers = async () => {
    try {
      const res = await getSuppliers();
      const suppliersWithAccount = res.data.map(s => {
        const acc = accounts.find(a => a.id === s.accountId);
        return {
          ...s,
          accountDisplay: acc ? `${acc.username} | ${acc.email}` : "Chưa liên kết"
        };
      });
      setSuppliers(suppliersWithAccount);
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi", detail: "Không thể load suppliers" });
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  useEffect(() => {
    if (accounts.length > 0) {
      loadSuppliers();
    }
  }, [accounts]);

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
      accept: async () => {
        await deleteSupplier(id);
        loadSuppliers();
        toast.current.show({ severity: "success", summary: "Đã xóa thành công!" });
      }
    });
  };

  const removeSelectedSuppliers = () => {
    confirmDialog({
      message: `Bạn có chắc muốn xóa ${selectedSuppliers.length} nhà cung cấp?`,
      acceptLabel: "Có",
      rejectLabel: "Không",
      accept: async () => {
        await Promise.all(selectedSuppliers.map(s => deleteSupplier(s.id)));
        setSelectedSuppliers([]);
        loadSuppliers();
        toast.current.show({ severity: "success", summary: "Đã xóa thành công!" });
      }
    });
  };

  const saveData = async (data) => {
    try {
      if (data.id) await updateSupplier(data.id, data);
      else await createSupplier(data);
      setFormVisible(false);
      loadSuppliers();
      toast.current.show({ severity: "success", summary: "Lưu thành công!" });
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi khi lưu dữ liệu!" });
    }
  };

  const accountTemplate = (row) => row.accountDisplay;

  // Toolbar
  const leftToolbarTemplate = () => (
    <Button label="➕ Thêm mới" icon="pi pi-plus" onClick={openNew} />
  );

  const rightToolbarTemplate = () => (
    <Button
      label="🗑 Xóa đã chọn"
      icon="pi pi-trash"
      className="p-button-danger"
      disabled={!selectedSuppliers.length}
      onClick={removeSelectedSuppliers}
    />
  );

  return (
    <div className="supplier-container">
      <Toast ref={toast} />
      <ConfirmDialog />

      <h2>Quản lý Nhà cung cấp</h2>

      <Toolbar className="mb-3" left={leftToolbarTemplate} right={rightToolbarTemplate}>
        <span className="p-input-icon-left">
          <i className="pi pi-search" />
          <InputText
            placeholder="Tìm kiếm toàn cục..."
            value={filters.global.value}
            onChange={(e) =>
              setFilters(prev => ({
                ...prev,
                global: { ...prev.global, value: e.target.value }
              }))
            }
          />
        </span>
      </Toolbar>

   <DataTable
  value={suppliers.map(s => ({
    ...s,
    phone: s.phone?.toString() || "", // chuyển phone sang string
    address: s.address || ""
  }))}
  paginator
  rows={10}
  rowsPerPageOptions={[10, 20, 50]}
  selection={selectedSuppliers}
  onSelectionChange={e => setSelectedSuppliers(e.value)}
  selectionMode="checkbox"
  stripedRows
  responsiveLayout="scroll"
  filters={filters}
  onFilter={e => setFilters(e.filters)}
  globalFilterFields={["name", "phone", "address", "taxCode", "accountDisplay"]}
  emptyMessage="Không có nhà cung cấp"
  filterDisplay="row"
>
  <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />

  <Column
    field="name"
    header="Tên NCC"
    sortable
    filter
    filterPlaceholder="Tìm tên..."
    filterMatchMode="contains"
  />
  <Column
    field="phone"
    header="SĐT"
    sortable
    filter
    filterPlaceholder="Tìm SĐT..."
    filterMatchMode="contains"
  />
  <Column
    field="address"
    header="Địa chỉ"
    sortable
    filter
    filterPlaceholder="Tìm địa chỉ..."
    filterMatchMode="contains"
  />
  <Column
    field="taxCode"
    header="Mã số thuế"
    sortable
    filter
    filterPlaceholder="Tìm MST..."
    filterMatchMode="contains"
  />
  <Column
    header="Tài khoản"
    body={row => row.accountDisplay}
    sortable
    filter
    filterField="accountDisplay"
    filterPlaceholder="Tìm tài khoản..."
    filterMatchMode="contains"
  />

  <Column
    header="Hành động"
    body={row => (
      <div style={{ display: 'flex', gap: '0.25rem', justifyContent: 'center' }}>
        <Button icon="pi pi-pencil" rounded text onClick={() => editSupplier(row)} />
        <Button icon="pi pi-trash" rounded text severity="danger" onClick={() => removeSupplier(row.id)} />
      </div>
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
