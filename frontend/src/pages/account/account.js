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
import './account.css';

export default function AccountComponent() {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [selectedAccounts, setSelectedAccounts] = useState([]);
  const [formVisible, setFormVisible] = useState(false);
  const [globalSearch, setGlobalSearch] = useState('');

  const toast = useRef(null);

  /* ================= LOAD DATA ================= */
  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data || []);
    } catch {
      toast.current.show({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Không thể load accounts'
      });
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  /* ================= SEARCH REALTIME ================= */
  const filteredAccounts = accounts.filter(acc => {
    if (!globalSearch.trim()) return true;

    const keyword = globalSearch.toLowerCase();

    return (
      acc.email?.toLowerCase().includes(keyword) ||
      acc.name?.toLowerCase().includes(keyword) ||
      acc.username?.toLowerCase().includes(keyword)
    );
  });

  /* ================= VALIDATE ================= */
  const toastError = (msg) => {
    toast.current.show({ severity: 'error', summary: 'Lỗi', detail: msg });
    return false;
  };

  const validateAccount = (acc) => {
    if (!acc.email?.trim()) return toastError('Bạn chưa nhập email');
    if (!acc.name?.trim()) return toastError('Bạn chưa nhập name');
    if (!acc.username?.trim()) return toastError('Bạn chưa nhập username');
    if (!acc.password?.trim()) return toastError('Bạn chưa nhập password');

    if (accounts.some(a => a.id !== acc.id && a.email === acc.email))
      return toastError('Email đã tồn tại');

    if (accounts.some(a => a.id !== acc.id && a.username === acc.username))
      return toastError('Username đã tồn tại');

    return true;
  };

  /* ================= SAVE ================= */
  const saveAccount = async () => {
    if (!validateAccount(selectedAccount)) return;

    try {
      selectedAccount.id
        ? await accountService.updateAccount(selectedAccount.id, selectedAccount)
        : await accountService.createAccount(selectedAccount);

      toast.current.show({
        severity: 'success',
        summary: 'Thành công',
        detail: 'Lưu account thành công'
      });

      setFormVisible(false);
      setSelectedAccount(null);
      loadAccounts();
    } catch {
      toastError('Không thể lưu account');
    }
  };

  /* ================= DELETE ================= */
  const removeAccount = (id) => {
    confirmDialog({
      message: 'Bạn có chắc muốn xóa account này?',
      acceptLabel: 'Có',
      rejectLabel: 'Không',
      accept: async () => {
        await accountService.deleteAccount(id);
        loadAccounts();
        toast.current.show({ severity: 'success', summary: 'Đã xóa thành công' });
      }
    });
  };

  const removeSelectedAccounts = () => {
    confirmDialog({
      message: `Bạn có chắc muốn xóa ${selectedAccounts.length} account đã chọn?`,
      acceptLabel: 'Có',
      rejectLabel: 'Không',
      accept: async () => {
        await Promise.all(
          selectedAccounts.map(a => accountService.deleteAccount(a.id))
        );
        setSelectedAccounts([]);
        loadAccounts();
        toast.current.show({ severity: 'success', summary: 'Đã xóa thành công' });
      }
    });
  };

  /* ================= TOOLBAR ================= */
  const leftToolbarTemplate = () => (
    <div className="flex align-items-center gap-2">
      <Button
        label="➕ Thêm mới"
        icon="pi pi-plus"
        onClick={() => {
          setSelectedAccount({ email: '', name: '', username: '', password: '' });
          setFormVisible(true);
        }}
      />

   
    </div>
  );

const rightToolbarTemplate = () => (
  <div className="flex align-items-center gap-2">
    <span className="p-input-icon-left">
      <i className="pi pi-search" />
      <InputText
        placeholder="Tìm email, name, username..."
        value={globalSearch}
        onChange={(e) => setGlobalSearch(e.target.value)}
        className="search-input"
      />
    </span>

    <Button
      label="🗑 Xóa đã chọn"
      icon="pi pi-trash"
      className="p-button-danger"
      disabled={!selectedAccounts.length}
      onClick={removeSelectedAccounts}
    />
  </div>
);

  const actionBodyTemplate = (row) => (
    <div className="flex gap-1 justify-content-center">
      <Button
        icon="pi pi-pencil"
        rounded
        text
        onClick={() => {
          setSelectedAccount(row);
          setFormVisible(true);
        }}
      />
      <Button
        icon="pi pi-trash"
        rounded
        text
        severity="danger"
        onClick={() => removeAccount(row.id)}
      />
    </div>
  );

  /* ================= UI ================= */
  return (
    <div className="p-4">
      <Toast ref={toast} />
      <ConfirmDialog />

      <h2>Quản lý Accounts</h2>

      <Toolbar
        className="mb-3"
        left={leftToolbarTemplate}
        right={rightToolbarTemplate}
      />

      <DataTable
        value={filteredAccounts}
        paginator
        rows={10}
        selection={selectedAccounts}
        onSelectionChange={(e) => setSelectedAccounts(e.value)}
        selectionMode="checkbox"
        stripedRows
        responsiveLayout="scroll"
        emptyMessage="Không có account"
      >
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
        <Column field="email" header="Email" sortable />
        <Column field="name" header="Name" sortable />
        <Column field="username" header="Username" sortable />
        <Column field="password" header="Password" />
        <Column header="Thao tác" body={actionBodyTemplate} />
      </DataTable>

      <Dialog
        header={selectedAccount?.id ? 'Chỉnh sửa Account' : 'Thêm Account mới'}
        visible={formVisible}
        style={{ width: '30vw' }}
        onHide={() => setFormVisible(false)}
        modal
      >
        {selectedAccount && (
          <div className="p-fluid grid formgrid">
            {['email', 'name', 'username', 'password'].map((field) => (
              <div key={field} className="field col-12 md:col-6">
                <label>{field}</label>
                <InputText
                  type={field === 'password' ? 'password' : 'text'}
                  value={selectedAccount[field] || ''}
                  onChange={(e) =>
                    setSelectedAccount({ ...selectedAccount, [field]: e.target.value })
                  }
                />
              </div>
            ))}

            <div className="flex justify-end gap-2 mt-3">
              <Button label="Hủy" className="p-button-secondary" onClick={() => setFormVisible(false)} />
              <Button label="Lưu" className="p-button-primary" onClick={saveAccount} />
            </div>
          </div>
        )}
      </Dialog>
    </div>
  );
}
