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
  const [globalSearch, setGlobalSearch] = useState("");

  const toast = useRef(null);

  const normalize = (val) =>
    String(val ?? "").toLowerCase().trim();

  // ================= LOAD DATA =================
  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data || []);
    } catch {
      toast.current.show({
        severity: "error",
        summary: "Lỗi",
        detail: "Không thể load accounts",
      });
    }
  };

  const loadCustomers = async () => {
    setLoading(true);
    try {
      const customerData = await customersService.getAll();

      const customersWithAccount = customerData.map((c) => {
        const acc = accounts.find((a) => a.id === c.accountId);
        return {
          ...c,
          accountDisplay: acc ? `${acc.username} | ${acc.email}` : "",
        };
      });

      setCustomers(customersWithAccount);
    } catch {
      toast.current.show({
        severity: "error",
        summary: "Lỗi",
        detail: "Không thể load customers",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  useEffect(() => {
    if (accounts.length) loadCustomers();
  }, [accounts]);

  // ================= SEARCH =================
  const filteredCustomers = customers.filter((c) => {
    if (!globalSearch.trim()) return true;
    const keyword = normalize(globalSearch);

    return (
      normalize(c.name).includes(keyword) ||
      normalize(c.phone).includes(keyword) ||
      normalize(c.address).includes(keyword) ||
      normalize(c.accountDisplay).includes(keyword)
    );
  });

  // ================= CRUD =================
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
        toast.current.show({
          severity: "success",
          summary: "Đã xóa thành công",
        });
      },
    });
  };

  const removeSelectedCustomers = () => {
    confirmDialog({
      message: `Bạn có chắc muốn xóa ${selectedCustomers.length} khách hàng?`,
      acceptLabel: "Có",
      rejectLabel: "Không",
      accept: async () => {
        await Promise.all(
          selectedCustomers.map((c) => customersService.remove(c.id))
        );
        setSelectedCustomers([]);
        loadCustomers();
        toast.current.show({
          severity: "success",
          summary: "Đã xóa thành công",
        });
      },
    });
  };

  const saveData = async (data) => {
    try {
      if (data.id) await customersService.update(data.id, data);
      else await customersService.create(data);

      setFormVisible(false);
      loadCustomers();
      toast.current.show({
        severity: "success",
        summary: "Lưu thành công",
      });
    } catch {
      toast.current.show({
        severity: "error",
        summary: "Lỗi",
        detail: "Không thể lưu dữ liệu",
      });
    }
  };

  // ================= EXPORT =================
  const exportExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(customers);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Customers");
    XLSX.writeFile(workbook, "customers.xlsx");
  };

  // ================= TOOLBAR =================
  const leftToolbarTemplate = () => (
    <Button label="➕ Thêm mới" icon="pi pi-plus" onClick={openNew} />
  );

  const rightToolbarTemplate = () => (
    <div className="flex align-items-center gap-2">
      <span className="p-input-icon-left">
        <i className="pi pi-search" />
        <InputText
          placeholder="Tìm tên, SĐT, địa chỉ, tài khoản..."
          value={globalSearch}
          onChange={(e) => setGlobalSearch(e.target.value)}
          style={{ width: "280px" }}
        />
      </span>

      <Button
        label="🗑 Xóa đã chọn"
        icon="pi pi-trash"
        className="p-button-danger"
        disabled={!selectedCustomers.length}
        onClick={removeSelectedCustomers}
      />
    </div>
  );

  // ================= RENDER =================
  return (
    <div className="customers-container">
      <Toast ref={toast} />
      <ConfirmDialog />

      <h2>Quản lý Customers</h2>

      <Toolbar
        className="mb-3"
        left={leftToolbarTemplate}
        right={rightToolbarTemplate}
      />

      <DataTable
        value={filteredCustomers}
        dataKey="id"          
        paginator
        rows={10}
        selection={selectedCustomers}
        onSelectionChange={(e) => setSelectedCustomers(e.value)}
        selectionMode="checkbox"
        stripedRows
        loading={loading}
        emptyMessage="Không có khách hàng"
      >
        <Column selectionMode="multiple" headerStyle={{ width: "3rem" }} />
        <Column field="name" header="Tên" sortable />
        <Column field="phone" header="SĐT" sortable />
        <Column field="address" header="Địa chỉ" sortable />
        <Column header="Tài khoản" body={(row) => row.accountDisplay || "—"} />

        <Column
          header="Hành động"
          body={(row) => (
            <div style={{ display: "flex", gap: "0.25rem", justifyContent: "center" }}>
              <Button icon="pi pi-eye" rounded text onClick={() => viewCustomer(row)} />
              <Button icon="pi pi-pencil" rounded text onClick={() => editCustomer(row)} />
              <Button
                icon="pi pi-trash"
                rounded
                text
                severity="danger"
                onClick={() => removeCustomer(row.id)}
              />
            </div>
          )}
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
