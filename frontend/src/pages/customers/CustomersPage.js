import { useEffect, useState, useRef } from "react";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { ConfirmDialog, confirmDialog } from "primereact/confirmdialog";
import { Toolbar } from "primereact/toolbar";
import { InputText } from "primereact/inputtext";
import * as XLSX from "xlsx";

import customersService from "./customersService";
import accountService from "../account/accountService";
import CustomersForm from "./CustomersForm";

import "./Customers.css";

export default function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [selectedCustomers, setSelectedCustomers] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [loading, setLoading] = useState(false);

  const toast = useRef(null);

  // Filter
  const emptyFilters = {
    global: { value: '', matchMode: 'contains' },
    name: { value: '', matchMode: 'contains' },
    phone: { value: '', matchMode: 'contains' },
    address: { value: '', matchMode: 'contains' },
    accountDisplay: { value: '', matchMode: 'contains' } // filter tài khoản
  };
  const [filters, setFilters] = useState(emptyFilters);

  // Load data
  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data || []);
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi", detail: "Không thể load accounts" });
    }
  };

  const loadCustomers = async () => {
    setLoading(true);
    try {
      const customerData = await customersService.getAll();

      // Tạo accountDisplay để filter
      const customersWithAccount = customerData.map(c => {
        const acc = accounts.find(a => a.id === c.accountId);
        return {
          ...c,
          accountDisplay: acc ? `${acc.username} | ${acc.email}` : "Chưa liên kết"
        };
      });

      setCustomers(customersWithAccount);
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi", detail: "Không thể load customers" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  // Khi accounts load xong, load customers để accountDisplay chính xác
  useEffect(() => {
    if (accounts.length > 0) {
      loadCustomers();
    }
  }, [accounts]);

  // Open form
  const openNew = () => {
    setSelectedCustomer(null);
    setFormVisible(true);
  };

  const editCustomer = (row) => {
    setSelectedCustomer(row);
    setFormVisible(true);
  };

  const viewCustomer = (row) => {
    setSelectedCustomer(row);
    setFormVisible(true);
  };

  const removeCustomer = (id) => {
    confirmDialog({
      message: "Bạn có chắc muốn xóa?",
      acceptLabel: "Có",
      rejectLabel: "Không",
      accept: async () => {
        await customersService.remove(id);
        loadCustomers();
        toast.current.show({ severity: "success", summary: "Đã xóa thành công!" });
      }
    });
  };

  const removeSelectedCustomers = () => {
    confirmDialog({
      message: `Bạn có chắc muốn xóa ${selectedCustomers.length} khách hàng?`,
      acceptLabel: "Có",
      rejectLabel: "Không",
      accept: async () => {
        await Promise.all(selectedCustomers.map(c => customersService.remove(c.id)));
        setSelectedCustomers([]);
        loadCustomers();
        toast.current.show({ severity: "success", summary: "Đã xóa thành công!" });
      }
    });
  };

  const saveData = async (data) => {
    try {
      if (data.id) await customersService.update(data.id, data);
      else await customersService.create(data);

      setFormVisible(false);
      loadCustomers();
      toast.current.show({ severity: "success", summary: "Lưu thành công!" });
    } catch {
      toast.current.show({ severity: "error", summary: "Lỗi", detail: "Không thể lưu dữ liệu!" });
    }
  };

  const exportExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(customers);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Customers");
    XLSX.writeFile(workbook, "customers.xlsx");
  };

  const leftToolbarTemplate = () => (
    <Button label="➕ Thêm mới" icon="pi pi-plus" onClick={openNew} />
  );

  const rightToolbarTemplate = () => (
    <Button
      label="🗑 Xóa đã chọn"
      icon="pi pi-trash"
      className="p-button-danger"
      disabled={!selectedCustomers.length}
      onClick={removeSelectedCustomers}
    />
  );

  const accountTemplate = (row) => {
    return row.accountDisplay; // đã map khi loadCustomers
  };

  return (
    <div className="customers-container">
      <Toast ref={toast} />
      <ConfirmDialog />

      <h2>Quản lý Customers</h2>

      <Toolbar className="mb-3" left={leftToolbarTemplate} right={rightToolbarTemplate}>
        <span className="p-input-icon-left">
          <i className="pi pi-search" />
          <InputText
            placeholder="Tìm kiếm toàn cục..."
            value={filters.global?.value || ""}
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
        value={customers}
        paginator
        rows={10}
        rowsPerPageOptions={[10, 20, 50]}
        selection={selectedCustomers}
        onSelectionChange={(e) => setSelectedCustomers(e.value)}
        selectionMode="checkbox"
        stripedRows
        responsiveLayout="scroll"
        loading={loading}
        filters={filters}
        onFilter={(e) => setFilters(e.filters)}
        globalFilterFields={['name', 'phone', 'address', 'accountDisplay']}
        emptyMessage="Không có khách hàng"
        filterDisplay="row"
      >
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />

        <Column
          field="name"
          header="Tên"
          sortable
          filter
          filterPlaceholder="Tìm tên..."
          filterMatchMode="contains"
          style={{ minWidth: '200px' }}
        />
        <Column
          field="phone"
          header="SĐT"
          sortable
          filter
          filterPlaceholder="Tìm SĐT..."
          filterMatchMode="contains"
          style={{ minWidth: '150px' }}
        />
        <Column
          field="address"
          header="Địa chỉ"
          sortable
          filter
          filterPlaceholder="Tìm địa chỉ..."
          filterMatchMode="contains"
          style={{ minWidth: '250px' }}
        />
        <Column
          header="Tài khoản"
          body={accountTemplate}
          sortable
          filter
          filterField="accountDisplay"
          filterPlaceholder="Tìm tài khoản..."
          filterMatchMode="contains"
          style={{ minWidth: '200px' }}
        />
        <Column
          header="Hành động"
          body={(row) => (
            <div style={{ display: 'flex', gap: '0.25rem', justifyContent: 'center' }}>
              <Button icon="pi pi-eye" rounded text tooltip="Xem chi tiết" onClick={() => viewCustomer(row)} />
              <Button icon="pi pi-pencil" rounded text tooltip="Sửa" onClick={() => editCustomer(row)} />
              <Button icon="pi pi-trash" rounded text severity="danger" tooltip="Xóa" onClick={() => removeCustomer(row.id)} />
            </div>
          )}
          style={{ width: '150px', textAlign: 'center' }}
        />
      </DataTable>

  <div className="export-button-container">
  <Button
    label="📥 Xuất Excel"
    icon="pi pi-file-excel"
    onClick={exportExcel}
  />
</div>
      <CustomersForm
        visible={formVisible}
        onHide={() => setFormVisible(false)}
        onSubmit={saveData}
        customer={selectedCustomer}
        accounts={accounts}
      />
    </div>
  );
}
