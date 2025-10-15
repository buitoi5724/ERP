import { useEffect, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ToastContainer, toast } from 'react-toastify';
import { Dialog } from 'primereact/dialog';
import { Card } from 'primereact/card';
import { InputText } from 'primereact/inputtext';
import { Button } from 'primereact/button';

import 'react-toastify/dist/ReactToastify.css';
import 'primereact/resources/themes/lara-light-indigo/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';

import accountService from './accountService';

export default function AccountComponent() {
  const [accounts, setAccounts] = useState([]);
  const [account, setAccount] = useState({ email: '', name: '', password: '', username: '' });
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);
  const [showAddForm, setShowAddForm] = useState(false);

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = () => {
    accountService.getAccounts()
      .then(data => setAccounts(data))
      .catch(() => toast.error("Lỗi tải danh sách account"));
  };

  const validateAccount = (acc, isEdit = false) => {
    if (!acc.email.trim()) return toast.error('Bạn chưa nhập email'), false;
    if (!acc.name.trim()) return toast.error('Bạn chưa nhập name'), false;
    if (!acc.password.trim()) return toast.error('Bạn chưa nhập password'), false;
    if (!acc.username.trim()) return toast.error('Bạn chưa nhập username'), false;

    const duplicateEmail = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.email === acc.email);
    if (duplicateEmail) return toast.error('Email đã tồn tại'), false;

    const duplicateUsername = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.username === acc.username);
    if (duplicateUsername) return toast.error('Username đã tồn tại'), false;

    const duplicateName = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.name === acc.name);
    if (duplicateName) return toast.error('Name đã tồn tại'), false;

    return true;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validateAccount(account)) return;

    accountService.createAccount(account)
      .then(() => {
        toast.success('Thêm account thành công');
        setAccount({ email: '', name: '', password: '', username: '' });
        setShowAddForm(false);
        loadAccounts();
      })
      .catch((err) => {
        if (err.response?.data?.message) toast.error(err.response.data.message);
        else toast.error("Đã xảy ra lỗi");
      });
  };

  const deleteAccount = (id) => {
    if (!window.confirm("Bạn chắc chắn xoá account này?")) return;

    accountService.deleteAccount(id)
      .then(() => {
        toast.success('Xoá account thành công');
        loadAccounts();
      })
      .catch(() => toast.error('Lỗi khi xoá account'));
  };

  const editAccount = (id) => {
    accountService.getAccountById(id)
      .then((data) => {
        setSelectedAccount(data);
        setShowEditForm(true);
      })
      .catch(() => toast.error('Lỗi khi lấy dữ liệu account'));
  };

  const saveAccount = () => {
    if (!validateAccount(selectedAccount, true)) return;

    accountService.updateAccount(selectedAccount.id, selectedAccount)
      .then(() => {
        toast.success('Cập nhật account thành công');
        loadAccounts();
        setShowEditForm(false);
      })
      .catch(() => toast.error('Lỗi khi cập nhật account'));
  };

  const actionBodyTemplate = (rowData) => (
    <>
      <Button label="Edit" icon="pi pi-pencil" className="p-button-sm p-button-text" onClick={() => editAccount(rowData.id)} />
      <Button label="Xoá" icon="pi pi-trash" className="p-button-sm p-button-danger p-button-text" onClick={() => deleteAccount(rowData.id)} />
    </>
  );

  return (
    <div className="p-4 space-y-6">
      {/* Danh sách tài khoản + nút thêm */}
<Card>
  <div className="flex justify-between items-center mb-3">
    <h1 className="text-xl font-bold">Danh sách Accounts</h1>
    <div className="ml-auto">   {/* đẩy nút sang phải */}
      <Button
        label="Thêm Account"
        icon="pi pi-plus"
        className="p-button-success"
        onClick={() => setShowAddForm(true)}
      />
    </div>
  </div>

  <DataTable value={accounts} paginator rows={5} responsiveLayout="scroll">
    <Column field="id" header="ID" sortable />
    <Column field="email" header="Email" sortable />
    <Column field="name" header="Name" sortable />
    <Column field="password" header="Password" />
    <Column field="username" header="Username" sortable />
    <Column header="Thao tác" body={actionBodyTemplate} />
  </DataTable>
</Card>

      {/* Dialog thêm account */}
<Dialog
  header="Thêm Account mới"
  visible={showAddForm}
  style={{ width: '30vw' }}
  onHide={() => setShowAddForm(false)}
  modal
>
  <form onSubmit={handleSubmit} className="p-fluid grid formgrid">
    <div className="field col-12 md:col-6">
      <label>Email</label>
      <InputText value={account.email} onChange={(e) => setAccount({ ...account, email: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Name</label>
      <InputText value={account.name} onChange={(e) => setAccount({ ...account, name: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Password</label>
      <InputText type="password" value={account.password} onChange={(e) => setAccount({ ...account, password: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Username</label>
      <InputText value={account.username} onChange={(e) => setAccount({ ...account, username: e.target.value })} />
    </div>

    <div className="flex justify-end gap-2 mt-3">
      <Button type="button" label="Hủy" icon="pi pi-times" className="p-button-secondary p-button-sm" onClick={() => setShowAddForm(false)} />
      <Button type="submit" label="Lưu" icon="pi pi-check" className="p-button-primary p-button-sm" />
    </div>
  </form>
</Dialog>

{/* Dialog chỉnh sửa account */}
<Dialog
  header="Chỉnh sửa Account"
  visible={showEditForm}
  style={{ width: '30vw' }}
  onHide={() => setShowEditForm(false)}
  modal
>
  {selectedAccount && (
    <div className="p-fluid grid formgrid">
      <div className="field col-12 md:col-6">
        <label>Email</label>
        <InputText value={selectedAccount.email} onChange={(e) => setSelectedAccount({ ...selectedAccount, email: e.target.value })} />
      </div>
      <div className="field col-12 md:col-6">
        <label>Name</label>
        <InputText value={selectedAccount.name} onChange={(e) => setSelectedAccount({ ...selectedAccount, name: e.target.value })} />
      </div>
      <div className="field col-12 md:col-6">
        <label>Password</label>
        <InputText type="password" value={selectedAccount.password} onChange={(e) => setSelectedAccount({ ...selectedAccount, password: e.target.value })} />
      </div>
      <div className="field col-12 md:col-6">
        <label>Username</label>
        <InputText value={selectedAccount.username} onChange={(e) => setSelectedAccount({ ...selectedAccount, username: e.target.value })} />
      </div>

      <div className="flex justify-end gap-2 mt-3">
        <Button type="button" label="Hủy" icon="pi pi-times" className="p-button-secondary p-button-sm" onClick={() => setShowEditForm(false)} />
        <Button type="button" label="Lưu" icon="pi pi-check" className="p-button-primary p-button-sm" onClick={saveAccount} />
      </div>
    </div>
  )}
</Dialog>


{/* Dialog chỉnh sửa */}
<Dialog
  header="Thêm Account mới"
  visible={showAddForm}
  style={{ width: '30vw' }}
  onHide={() => setShowAddForm(false)}
  modal
  footer={
    <div className="flex justify-end gap-2">
      <Button
        type="button"
        label="Hủy"
        icon="pi pi-times"
        className="p-button-secondary p-button-sm"
        onClick={() => setShowAddForm(false)}
      />
      <Button
        type="submit"
        label="Lưu"
        icon="pi pi-check"
        className="p-button-primary p-button-sm"
        onClick={handleSubmit}
      />
    </div>
  }
>
  <form onSubmit={handleSubmit} className="p-fluid grid formgrid">
    <div className="field col-12 md:col-6">
      <label>Email</label>
      <InputText value={account.email} onChange={(e) => setAccount({ ...account, email: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Name</label>
      <InputText value={account.name} onChange={(e) => setAccount({ ...account, name: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Password</label>
      <InputText type="password" value={account.password} onChange={(e) => setAccount({ ...account, password: e.target.value })} />
    </div>
    <div className="field col-12 md:col-6">
      <label>Username</label>
      <InputText value={account.username} onChange={(e) => setAccount({ ...account, username: e.target.value })} />
    </div>
  </form>
</Dialog>


      <ToastContainer />
    </div>
  );
}
