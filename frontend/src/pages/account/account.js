import { useEffect, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ToastContainer, toast } from 'react-toastify';
import { Dialog } from 'primereact/dialog';
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

  // Load dữ liệu ban đầu khi component mount
  useEffect(() => {
    loadAccounts();
  }, []);

  // Gọi API để lấy danh sách account
  const loadAccounts = () => {
    accountService.getAccounts()
      .then(data => setAccounts(data))
      .catch(() => toast.error("Lỗi tải danh sách account"));
  };

  // ✅ Hàm kiểm tra tính hợp lệ và trùng lặp
  const validateAccount = (acc, isEdit = false) => {
    if (!acc.email.trim()) {
      toast.error('Bạn chưa nhập email');
      return false;
    }
    if (!acc.name.trim()) {
      toast.error('Bạn chưa nhập name');
      return false;
    }
    if (!acc.password.trim()) {
      toast.error('Bạn chưa nhập password');
      return false;
    }
    if (!acc.username.trim()) {
      toast.error('Bạn chưa nhập username');
      return false;
    }

    // Kiểm tra trùng email
    const duplicateEmail = accounts.find(a =>
      a.id !== (isEdit ? acc.id : null) && a.email === acc.email
    );
    if (duplicateEmail) {
      toast.error('Email đã tồn tại');
      return false;
    }
    // Kiểm tra trùng username
    const duplicateUsername = accounts.find(a =>
      a.id !== (isEdit ? acc.id : null) && a.username === acc.username
    );
    if (duplicateUsername) {
      toast.error('Username đã tồn tại');
      return false;
    }
    // Kiểm tra trùng name
    const duplicateName = accounts.find(a =>
      a.id !== (isEdit ? acc.id : null) && a.name === acc.name
    );
    if (duplicateName) {
      toast.error('Name đã tồn tại');
      return false;
    }
    return true;
  };
  // ✅ Thêm account mới
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validateAccount(account)) return;
    accountService.createAccount(account)
      .then(() => {
        toast.success('Thêm account thành công');
        setAccount({ email: '', name: '', password: '', username: '' });
        loadAccounts();
      })
      .catch((err) => {
        if (err.response?.data?.message) {
          toast.error(err.response.data.message);
        } else {
          toast.error("Đã xảy ra lỗi");
        }
      });
  };
  // ✅ Xoá account
  const deleteAccount = (id) => {
    if (!window.confirm("Bạn chắc chắn xoá account này?")) return;

    accountService.deleteAccount(id)
      .then(() => {
        toast.success('Xoá account thành công');
        loadAccounts();
      })
      .catch(() => toast.error('Lỗi khi xoá account'));
  };
  // ✅ Mở dialog và nạp dữ liệu để chỉnh sửa
  const editAccount = (id) => {
    accountService.getAccountById(id)
      .then((data) => {
        setSelectedAccount(data);
        setShowEditForm(true);
      })
      .catch(() => toast.error('Lỗi khi lấy dữ liệu account'));
  };
  // ✅ Lưu lại sau khi chỉnh sửa
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
  // ✅ Template cho các nút thao tác (Edit, Xoá)
  const actionBodyTemplate = (rowData) => (
    <>
      <button onClick={() => editAccount(rowData.id)}>Edit</button>{' '}
      <button onClick={() => deleteAccount(rowData.id)}>Xoá</button>
    </>
  );
  return (
    <div className="account-container">
      {/* Danh sách tài khoản */}
      <div className="account-list card">
        <h2>Danh sách Accounts</h2>
        <DataTable value={accounts} tableStyle={{ minWidth: '30rem' }}>
          <Column field="id" header="ID"></Column>
          <Column field="email" header="Email"></Column>
          <Column field="name" header="Name"></Column>
          <Column field="password" header="Password"></Column>
          <Column field="username" header="Username"></Column>
          <Column header="Thao tác" body={actionBodyTemplate}></Column>
        </DataTable>
      </div>
      {/* Form thêm mới tài khoản */}
      <div className="account-form">
        <h3>Thêm Account mới</h3>
       <form onSubmit={handleSubmit}>
  <div>
    <label>Email:</label>
    <input
      value={account.email}
      onChange={(e) => setAccount({ ...account, email: e.target.value })}
      list="emailSuggestions"
    />
    <datalist id="emailSuggestions">
      {accounts.map((acc) => (
        <option key={acc.id} value={acc.email} />
      ))}
    </datalist>
  </div>

  <div>
    <label>Name:</label>
    <input
      value={account.name}
      onChange={(e) => setAccount({ ...account, name: e.target.value })}
      list="nameSuggestions"
    />
    <datalist id="nameSuggestions">
      {accounts.map((acc) => (
        <option key={acc.id} value={acc.name} />
      ))}
    </datalist>
  </div>

  <div>
    <label>Password:</label>
    <input
      value={account.password}
      onChange={(e) => setAccount({ ...account, password: e.target.value })}
    />
  </div>

  <div>
    <label>Username:</label>
    <input
      value={account.username}
      onChange={(e) => setAccount({ ...account, username: e.target.value })}
      list="usernameSuggestions"
    />
    <datalist id="usernameSuggestions">
      {accounts.map((acc) => (
        <option key={acc.id} value={acc.username} />
      ))}
    </datalist>
  </div>

  <button type="submit">Thêm Account</button>
</form>
      </div>

      {/* Dialog chỉnh sửa tài khoản */}
      <Dialog
        header="Chỉnh sửa Account"
        visible={showEditForm}
        style={{ width: '30vw' }}
        onHide={() => setShowEditForm(false)}
      >
        {selectedAccount && (
          <form onSubmit={(e) => { e.preventDefault(); saveAccount(); }}>
            <div>
              <label>Email:</label>
              <input
                value={selectedAccount.email}
                onChange={(e) => setSelectedAccount({ ...selectedAccount, email: e.target.value })}
              />
            </div>
            <div>
              <label>Name:</label>
              <input
                value={selectedAccount.name}
                onChange={(e) => setSelectedAccount({ ...selectedAccount, name: e.target.value })}
              />
            </div>
            <div>
              <label>Password:</label>
              <input
                value={selectedAccount.password}
                onChange={(e) => setSelectedAccount({ ...selectedAccount, password: e.target.value })}
              />
            </div>
            <div>
              <label>Username:</label>
              <input
                value={selectedAccount.username}
                onChange={(e) => setSelectedAccount({ ...selectedAccount, username: e.target.value })}
              />
            </div>
            <button type="submit">Lưu</button>
          </form>
        )}
      </Dialog>

      {/* Toast notification */}
      <ToastContainer />
    </div>
  );
}
