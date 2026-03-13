import React, { useState, useEffect } from 'react';
import BankService from './services/api';
import AccountList from './components/AccountList';
import CreateAccount from './components/CreateAccount';
import TransferForm from './components/TransferForm';

import 'bootstrap/dist/css/bootstrap.min.css';

function App() {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Function to fetch accounts from the backend
    const fetchAccounts = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await BankService.getAllAccounts();
            setAccounts(response.data);
        } catch (err) {
            console.error(err);
            setError('Failed to fetch accounts. Please check your backend is running.');
        } finally {
            setLoading(false);
        }
    };

    // Load accounts when component mounts
    useEffect(() => {
        fetchAccounts();
    }, []);

    const handleAccountCreated = (newAccount) => {
        // Optimistically update or refresh
        fetchAccounts();
    };

    const handleTransferCompleted = () => {
        // Refresh to show updated balances
        fetchAccounts();
    };

    return (
        <div className="App bg-light min-vh-100">
            {/* Navbar */}
            <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4">
                <div className="container">
                    <span className="navbar-brand fw-bold">
                        🏦 MyBank Dashboard
                    </span>
                    <div className="d-flex text-white">
                        <small>{new Date().toLocaleDateString()}</small>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <div className="container">
                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}
                
                <div className="row">
                    {/* Left Column: Actions */}
                    <div className="col-lg-4 mb-4">
                        <CreateAccount onAccountCreated={handleAccountCreated} />
                        <TransferForm 
                            accounts={accounts} 
                            onTransferCompleted={handleTransferCompleted} 
                        />
                    </div>
                    
                    {/* Right Column: Information */}
                    <div className="col-lg-8">
                        <AccountList 
                            accounts={accounts} 
                            loading={loading}
                            refreshAccounts={fetchAccounts}
                        />

                        <div className="alert alert-info mt-3 shadow-sm border-0">
                            <strong>Note:</strong> Transaction history can be accessed via API but is not yet displayed here for simplicity.
                            IDs are sequential (1, 2, 3...) after the recent fix.
                        </div>
                    </div>
                </div>
            </div>
            
            {/* Footer */}
            <footer className="text-center py-4 text-muted">
                <small>&copy; 2026 MyBank Inc. | Secure Banking System</small>
            </footer>
        </div>
    );
}

export default App;
