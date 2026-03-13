import React, { useState } from 'react';
import BankService from '../services/api';

const TransferForm = ({ accounts, onTransferCompleted }) => {
    const [fromAccount, setFromAccount] = useState('');
    const [toAccount, setToAccount] = useState('');
    const [amount, setAmount] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const handleTransfer = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            await BankService.transfer(fromAccount, toAccount, parseFloat(amount));
            setSuccess('Transfer successful!');
            onTransferCompleted();
            // Reset fields
            setFromAccount('');
            setToAccount('');
            setAmount('');
        } catch (err) {
            setError(err.response?.data?.message || 'Transfer failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card shadow-sm mb-4">
            <div className="card-header bg-success text-white">
                <h5 className="mb-0">Quick Transfer</h5>
            </div>
            <div className="card-body">
                {error && <div className="alert alert-danger">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}
                
                <form onSubmit={handleTransfer}>
                    <div className="mb-3">
                        <label className="form-label">From Account</label>
                        <select 
                            className="form-select" 
                            value={fromAccount} 
                            onChange={(e) => setFromAccount(e.target.value)}
                            required
                        >
                            <option value="">Select Account</option>
                            {accounts.map(acc => (
                                <option key={acc.id} value={acc.id}>
                                    ID: {acc.id} - {acc.ownerName} (€{acc.balance.toFixed(2)})
                                </option>
                            ))}
                        </select>
                    </div>
                    
                    <div className="mb-3">
                        <label className="form-label">To Account ID</label>
                        <input 
                            type="number" 
                            className="form-control" 
                            value={toAccount}
                            onChange={(e) => setToAccount(e.target.value)}
                            required
                            placeholder="Enter Destination Account ID"
                        />
                    </div>
                    
                    <div className="mb-3">
                        <label className="form-label">Amount (€)</label>
                        <input 
                            type="number" 
                            className="form-control" 
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            min="0.01"
                            step="0.01"
                            required
                            placeholder="0.00"
                        />
                    </div>
                    
                    <button type="submit" className="btn btn-success w-100" disabled={loading}>
                        {loading ? 'Processing...' : 'Transfer Funds'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default TransferForm;
