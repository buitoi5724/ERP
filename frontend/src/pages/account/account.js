import { useEffect, useState, useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Toolbar } from 'primereact/toolbar';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';
import { Dialog } from 'primereact/dialog';

import accountService from './accountService';

import 'primereact/resources/themes/lara-light-indigo/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

export default function AccountComponent() {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [selectedAccounts, setSelectedAccounts] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
const [globalSearch, setGlobalSearch] = useState("");
  // Khởi tạo filters chuẩn cho PrimeReact
const emptyFilters = {
  global: { value: '', matchMode: 'startsWith' }, // <-- đây
  email: { value: '', matchMode: 'startsWith' },
  name: { value: '', matchMode: 'startsWith' },
  username: { value: '', matchMode: 'startsWith' },
};

const filteredAccounts = accounts.filter(acc => {
  const value = globalSearch.toLowerCase();
  if (!value) return true; // nếu ô tìm kiếm rỗng => hiển thị tất cả
  return (
    acc.email?.toLowerCase().startsWith(value) ||
    acc.name?.toLowerCase().startsWith(value) ||
    acc.username?.toLowerCase().startsWith(value)
  );
});
  const [filters, setFilters] = useState(emptyFilters);

  const toast = useRef(null);

  // Load danh sách account
  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data || []);
    } catch {
      toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Không thể load accounts' });
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  // Validate account
  const validateAccount = (acc) => {
    if (!acc?.email?.trim()) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Bạn chưa nhập email' }); return false; }
    if (!acc?.name?.trim()) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Bạn chưa nhập name' }); return false; }
    if (!acc?.password?.trim()) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Bạn chưa nhập password' }); return false; }
    if (!acc?.username?.trim()) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Bạn chưa nhập username' }); return false; }

    const duplicateEmail = accounts.find(a => a.id !== (acc.id || null) && a.email === acc.email);
    if (duplicateEmail) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Email đã tồn tại' }); return false; }

    const duplicateUsername = accounts.find(a => a.id !== (acc.id || null) && a.username === acc.username);
    if (duplicateUsername) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Username đã tồn tại' }); return false; }

    const duplicateName = accounts.find(a => a.id !== (acc.id || null) && a.name === acc.name);
    if (duplicateName) { toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Name đã tồn tại' }); return false; }

    return true;
  };

  // Lưu account (create hoặc update)
  const saveAccount = async () => {
    if (!validateAccount(selectedAccount)) return;

    try {
      if (selectedAccount.id) {
        await accountService.updateAccount(selectedAccount.id, selectedAccount);
      } else {
        await accountService.createAccount(selectedAccount);
      }

      toast.current.show({ severity: 'success', summary: 'Thành công', detail: 'Lưu account thành công' });
      setFormVisible(false);
      setSelectedAccount(null);
      loadAccounts();
    } catch {
      toast.current.show({ severity: 'error', summary: 'Lỗi', detail: 'Không thể lưu account' });
    }
  };

  // Xóa account
  const removeAccount = (id) => {
    confirmDialog({
      message: 'Bạn có chắc muốn xóa account này?',
      acceptLabel: 'Có',
      rejectLabel: 'Không',
      accept: async () => {
        await accountService.deleteAccount(id);
        loadAccounts();
        toast.current.show({ severity: 'success', summary: 'Đã xóa thành công' });
      },
    });
  };

  const removeSelectedAccounts = () => {
    confirmDialog({
      message: `Bạn có chắc muốn xóa ${selectedAccounts.length} account đã chọn?`,
      acceptLabel: 'Có',
      rejectLabel: 'Không',
      accept: async () => {
        await Promise.all(selectedAccounts.map(a => accountService.deleteAccount(a.id)));
        setSelectedAccounts([]);
        loadAccounts();
        toast.current.show({ severity: 'success', summary: 'Đã xóa thành công' });
      },
    });
  };

  // Toolbar
  const leftToolbarTemplate = () => (
    <Button
      label="➕ Thêm mới"
      icon="pi pi-plus"
      onClick={() => { setSelectedAccount({ email:'', name:'', password:'', username:'' }); setFormVisible(true); }}
    />
  );


  
  const rightToolbarTemplate = () => (
    <Button
      label="🗑 Xóa đã chọn"
      icon="pi pi-trash"
      className="p-button-danger"
      disabled={!selectedAccounts.length}
      onClick={removeSelectedAccounts}
    />
  );

  const actionBodyTemplate = (rowData) => (
    <div style={{ display: 'flex', gap: '0.25rem', justifyContent: 'center' }}>
      <Button icon="pi pi-pencil" rounded text tooltip="Sửa" onClick={() => { setSelectedAccount(rowData); setFormVisible(true); }} />
      <Button icon="pi pi-trash" rounded text severity="danger" tooltip="Xóa" onClick={() => removeAccount(rowData.id)} />
    </div>
  );

  return (
    <div className="p-4">
      <Toast ref={toast} />
      <ConfirmDialog />

      <h2>Quản lý Accounts</h2>
<Toolbar className="mb-3" left={leftToolbarTemplate} right={rightToolbarTemplate}>
  <span className="p-input-icon-left">
    <i className="pi pi-search" />
<InputText
  placeholder="Tìm kiếm theo chữ cái đầu..."
  value={globalSearch}
  onChange={(e) => setGlobalSearch(e.target.value)}
/>
  </span>
</Toolbar>

<DataTable
  value={accounts}
  paginator
  rows={10}
  selection={selectedAccounts}
  onSelectionChange={(e) => setSelectedAccounts(e.value)}
  selectionMode="checkbox"
  stripedRows
  responsiveLayout="scroll"
  filters={filters}
  onFilter={(e) => setFilters(prev => ({
    ...prev,
    ...e.filters,
    global: e.filters.global || prev.global
  }))}
  globalFilterFields={['email','name','username']}
  filterDisplay="row"
  emptyMessage="Không có account"
>
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
        <Column field="email" header="Email" sortable filter filterPlaceholder="Tìm email..." />
        <Column field="name" header="Name" sortable filter filterPlaceholder="Tìm name..." />
        <Column field="username" header="Username" sortable filter filterPlaceholder="Tìm username..." />
        <Column field="password" header="Password" />
        <Column header="Thao tác" body={actionBodyTemplate} />
      </DataTable>

      <Dialog
        header={selectedAccount?.id ? "Chỉnh sửa Account" : "Thêm Account mới"}
        visible={formVisible}
        style={{ width: '30vw' }}
        onHide={() => setFormVisible(false)}
        modal
      >
        {selectedAccount && (
          <div className="p-fluid grid formgrid">
            <div className="field col-12 md:col-6">
              <label>Email</label>
              <InputText value={selectedAccount.email || ""} onChange={(e) => setSelectedAccount({ ...selectedAccount, email: e.target.value })} />
            </div>
            <div className="field col-12 md:col-6">
              <label>Name</label>
              <InputText value={selectedAccount.name || ""} onChange={(e) => setSelectedAccount({ ...selectedAccount, name: e.target.value })} />
            </div>
            <div className="field col-12 md:col-6">
              <label>Password</label>
              <InputText type="password" value={selectedAccount.password || ""} onChange={(e) => setSelectedAccount({ ...selectedAccount, password: e.target.value })} />
            </div>
            <div className="field col-12 md:col-6">
              <label>Username</label>
              <InputText value={selectedAccount.username || ""} onChange={(e) => setSelectedAccount({ ...selectedAccount, username: e.target.value })} />
            </div>

            <div className="flex justify-end gap-2 mt-3">
              <Button label="Hủy" icon="pi pi-times" className="p-button-secondary" onClick={() => setFormVisible(false)} />
              <Button label="Lưu" icon="pi pi-check" className="p-button-primary" onClick={saveAccount} />
            </div>
          </div>
        )}
      </Dialog>
    </div>
  );
}
