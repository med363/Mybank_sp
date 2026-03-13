import React, { useState } from 'react';
import BankService from '../services/api';

const CreateAccount = ({ onAccountCreated }) => {
    const [name, setName] = useState('');
    const [deposit, setDeposit] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const initialDeposit = deposit ? parseFloat(deposit) : 0;
            const response = await BankService.createAccount(name, initialDeposit);
            // Notify parent component to refresh the list
            onAccountCreated(response.data);
            setName('');
            setDeposit('');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create account');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card mb-4 shadow-sm">
            <div className="card-header bg-primary text-white">
                <h5 className="mb-0">Create New Account</h5>
            </div>
            <div className="card-body">
                {error && <div className="alert alert-danger">{error}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Owner Name</label>
                        <input 
                            type="text" 
                            className="form-control" 
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="e.g. John Doe"
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Initial Deposit (€)</label>
                        <input 
                            type="number" 
                            className="form-control" 
                            value={deposit}
                            onChange={(e) => setDeposit(e.target.value)}
                            min="0"
                            step="0.01"
                            placeholder="0.00"
                        />
                    </div>
                    <button type="submit" className="btn btn-primary" disabled={loading}>
                        {loading ? 'Creating...' : 'Create Account'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CreateAccount;
