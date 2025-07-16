import { useEffect, useState } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import accountService from './accountService';

export default function AccountComponent() {
  const [accounts, setAccounts] = useState([]);
  const [account, setAccount] = useState({ username: '', email: '' });

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = () => {
    accountService.getAccounts()
      .then(data => setAccounts(data))
      .catch(() => toast.error("Lỗi tải danh sách account"));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    accountService.createAccount(account)
      .then(() => {
        toast.success('Thêm account thành công');
        setAccount({ username: '', email: '' });
        loadAccounts();
      })
      .catch(() => toast.error('Lỗi khi thêm account'));
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

  const actionBodyTemplate = (rowData) => (
    <button onClick={() => deleteAccount(rowData.id)}>Xoá</button>
  );

  return (
    <div className="account-container">
      <div className="account-list">
        <h2>Danh sách Accounts</h2>
      <DataTable
  value={accounts}
  tableStyle={{ minWidth: '30rem' }}
  tableClassName="custom-table"
>
  <Column field="id" header="ID" ></Column>
  <Column field="username" header="Username" ></Column>
  <Column field="email" header="Email"></Column>
  <Column header="Thao tác" body={actionBodyTemplate} />
</DataTable>
      </div>

      <div className="account-form">
        <h3>Thêm Account mới</h3>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Username:</label>
            <input
              value={account.username}
              onChange={(e) => setAccount({ ...account, username: e.target.value })}
            />
          </div>
          <div>
            <label>Email:</label>
            <input
              value={account.email}
              onChange={(e) => setAccount({ ...account, email: e.target.value })}
            />
          </div>
          <button type="submit">Thêm Account</button>
        </form>
      </div>

      <ToastContainer />
    </div>
  );
}
