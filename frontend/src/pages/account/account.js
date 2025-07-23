
import { useEffect, useState } from 'react'; // React hooks
import { DataTable } from 'primereact/datatable'; // Bảng dữ liệu từ PrimeReact
import { Column } from 'primereact/column'; // Cột trong bảng PrimeReact
import { ToastContainer, toast } from 'react-toastify'; // Hiển thị thông báo
import { Dialog } from 'primereact/dialog'; // Popup dialog từ PrimeReact
import { useNavigate } from 'react-router-dom'; // Điều hướng giữa các route
// Import CSS cho các thư viện UI
import 'react-toastify/dist/ReactToastify.css';
import 'primereact/resources/themes/lara-light-indigo/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
// Import service để thao tác với API
import accountService from './accountService';
// Component chính
export default function AccountComponent() {
  // Khởi tạo state để lưu danh sách account
  const [accounts, setAccounts] = useState([]);
  // State lưu thông tin account đang nhập hoặc chỉnh sửa
  const [account, setAccount] = useState({ email: '', name: '', password: '', username: '' });
  // State lưu account đang được chọn để chỉnh sửa
  const [selectedAccount, setSelectedAccount] = useState(null);
  // Hiện/ẩn form chỉnh sửa
  const [showEditForm, setShowEditForm] = useState(false);
  // Hiện/ẩn popup xác nhận truy cập
  const [showAccessPopup, setShowAccessPopup] = useState(true);
  // Kiểm soát quyền truy cập
  const [accessGranted, setAccessGranted] = useState(false);
  // Hook để điều hướng sang route khác
  const navigate = useNavigate();
  // Khi accessGranted thay đổi thành true → load danh sách account
  useEffect(() => {
    if (accessGranted) {
      loadAccounts();
    }
  }, [accessGranted]);
  // Gọi API để lấy danh sách tài khoản
  const loadAccounts = () => {
    accountService.getAccounts()
      .then(data => setAccounts(data))
      .catch(() => toast.error("Lỗi tải danh sách account"));
  };
  // Hàm kiểm tra dữ liệu account trước khi thêm/sửa
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
    // Kiểm tra trùng lặp email, username, name (trừ chính nó khi đang edit)
    const duplicateEmail = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.email === acc.email);
    if (duplicateEmail) {
      toast.error('Email đã tồn tại');
      return false;
    }
    const duplicateUsername = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.username === acc.username);
    if (duplicateUsername) {
      toast.error('Username đã tồn tại');
      return false;
    }
    const duplicateName = accounts.find(a => a.id !== (isEdit ? acc.id : null) && a.name === acc.name);
    if (duplicateName) {
      toast.error('Name đã tồn tại');
      return false;
    }
    return true;
  };
  // Xử lý khi submit form tạo mới account
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validateAccount(account)) return;
    accountService.createAccount(account)
      .then(() => {
        toast.success('Thêm account thành công');
        setAccount({ email: '', name: '', password: '', username: '' }); // reset form
        loadAccounts(); // reload danh sách
      })
      .catch((err) => {
        if (err.response?.data?.message) {
          toast.error(err.response.data.message);
        } else {
          toast.error("Đã xảy ra lỗi");
        }
      });
  };
  // Xóa một account
  const deleteAccount = (id) => {
    if (!window.confirm("Bạn chắc chắn xoá account này?")) return;

    accountService.deleteAccount(id)
      .then(() => {
        toast.success('Xoá account thành công');
        loadAccounts(); // load lại danh sách
      })
      .catch(() => toast.error('Lỗi khi xoá account'));
  };
  // Lấy dữ liệu account theo ID để hiển thị lên form edit
  const editAccount = (id) => {
    accountService.getAccountById(id)
      .then((data) => {
        setSelectedAccount(data);
        setShowEditForm(true);
      })
      .catch(() => toast.error('Lỗi khi lấy dữ liệu account'));
  };
  // Lưu lại thông tin đã chỉnh sửa
  const saveAccount = () => {
    if (!validateAccount(selectedAccount, true)) return;
    accountService.updateAccount(selectedAccount.id, selectedAccount)
      .then(() => {
        toast.success('Cập nhật account thành công');
        loadAccounts(); // cập nhật danh sách
        setShowEditForm(false);
      })
      .catch(() => toast.error('Lỗi khi cập nhật account'));
  };
  // Template hiển thị nút edit & delete trong bảng
  const actionBodyTemplate = (rowData) => (
    <>
      <button onClick={() => editAccount(rowData.id)}>Edit</button>{' '}
      <button onClick={() => deleteAccount(rowData.id)}>Xoá</button>
    </>
  );
  return (
    <div className="account-container">
      {/*  Popup xác nhận truy cập */}
      <Dialog
        header="Xác nhận truy cập"
        visible={showAccessPopup}
        closable={false}
        modal
        style={{ width: '25vw' }}
      >
        <p>Bạn có muốn chỉnh sửa hay đăng nhập không?</p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
          <button
            onClick={() => {
              setAccessGranted(true); // Cho phép truy cập
              setShowAccessPopup(false); // Ẩn popup
            }}
          >
            Yes
          </button>
          <button
            onClick={() => {
              setShowAccessPopup(false); // Ẩn popup
              navigate('/pages/product/abc'); // Chuyển sang route khác
            }}
          >
            No
          </button>
        </div>
      </Dialog>
      {/*  Giao diện chính chỉ hiện khi được cấp quyền */}
      {accessGranted && (
        <>
          {/* Danh sách các tài khoản */}
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
          {/* Form thêm account mới */}
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
          {/* Popup dialog chỉnh sửa account */}
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
            )}loa
          </Dialog>
        </>
      )}
      {/* Container hiển thị toast thông báo */}
      <ToastContainer />
    </div>
  );
}
