import React from 'react';

const AccountList = ({ accounts, loading, refreshAccounts }) => {
    return (
        <div className="card shadow-sm">
            <div className="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                <h5 className="mb-0">Accounts Overview</h5>
                <button 
                    className="btn btn-sm btn-secondary" 
                    onClick={refreshAccounts}
                    disabled={loading}
                >
                    {loading ? 'Refreshing...' : 'Refresh'}
                </button>
            </div>
            <div className="card-body p-0">
                {accounts.length === 0 ? (
                    <div className="p-4 text-center text-muted">No accounts found. Create one to get started!</div>
                ) : (
                    <div className="table-responsive">
                        <table className="table table-hover mb-0">
                            <thead className="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Owner</th>
                                    <th>Account Number</th>
                                    <th className="text-end">Balance</th>
                                    <th>IBAN</th>
                                </tr>
                            </thead>
                            <tbody>
                                {accounts.map(account => (
                                    <tr key={account.id}>
                                        <td>{account.id}</td>
                                        <td className="fw-bold">{account.ownerName}</td>
                                        <td><code>{account.accountNumber}</code></td>
                                        <td className="text-end text-success fw-bold">
                                            {account.balance.toFixed(3)} TND
                                        </td>
                                        <td><small className="text-muted">{account.iban}</small></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AccountList;
