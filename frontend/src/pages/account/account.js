import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { SplitButton } from 'primereact/splitbutton';
import { useEffect, useState } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import accountService from './accountService';

export default function AccountComponent() {
    const [accounts, setAccounts] = useState([]);
    const [account, setAccount] = useState({ username: '', email: '' });

    const loadAccounts = () => {
        accountService.getAccounts()
            .then(data => setAccounts(data))
            .catch(err => {
                console.error('Failed to fetch accounts:', err);
                toast.error('Failed to load accounts.');
            });
    };

    useEffect(() => {
        loadAccounts();
    }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        accountService.createAccount(account)
            .then(data => {
                console.log('Account created:', data);
                toast.success('Account created successfully!');
                setAccount({ username: '', email: '' });
                loadAccounts();
            })
            .catch(err => {
                console.error('Error creating account:', err);
                toast.error('Failed to create account.');
            });
    };

    const deleteAccount = (id) => {
        accountService.deleteAccount(id)
            .then(() => {
                console.log(`Deleted account with id ${id}`);
                toast.success('Account deleted successfully!');
                loadAccounts();
            })
            .catch(err => {
                console.error('Error deleting account:', err);
                toast.error('Failed to delete account.');
            });
    };

    const actionBodyTemplate = (rowData) => (
        <button onClick={() => deleteAccount(rowData.id)}>Delete</button>
    );

    const save = () => {
        handleSubmit({ preventDefault: () => {} });
    };

    const items = [
        {
            label: 'Save',
            icon: 'pi pi-check',
            command: () => save()
        },
        {
            label: 'Something else',
            icon: 'pi pi-cog',
            command: () => console.log('Do something else')
        }
    ];

    return (
        <div style={{ padding: '20px' }}>
            <h2>Account List</h2>
            <div className="card" style={{ marginBottom: '30px' }}>
                <DataTable value={accounts}>
                    <Column field="id" header="ID"></Column>
                    <Column field="username" header="Username"></Column>
                    <Column field="email" header="Email"></Column>
                    <Column header="Actions" body={actionBodyTemplate}></Column>
                </DataTable>
            </div>

            <h3>Add New Account</h3>
            <form onSubmit={handleSubmit} style={{ marginBottom: '20px' }}>
                <div style={{ marginBottom: '10px' }}>
                    <label style={{ width: '100px', display: 'inline-block' }}>Username:</label>
                    <input
                        name="username"
                        value={account.username}
                        onChange={(e) => setAccount({ ...account, username: e.target.value })}
                    />
                </div>
                <div style={{ marginBottom: '10px' }}>
                    <label style={{ width: '100px', display: 'inline-block' }}>Email:</label>
                    <input
                        name="email"
                        value={account.email}
                        onChange={(e) => setAccount({ ...account, email: e.target.value })}
                    />
                </div>
                <button type="submit">Add Account</button>
            </form>

            <div style={{ marginTop: '20px' }}>
                <SplitButton label="Save Actions" model={items} />
            </div>

            <ToastContainer />
        </div>
    );
}
