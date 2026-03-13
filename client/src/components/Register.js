import React, { useState } from 'react';
import BankService from '../services/api';

/**
 * Register Component
 * Allows users to create a new account.
 * This will create a User record in the database.
 */
const Register = ({ onRegisterSuccess }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Basic validation
        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }
        
        try {
            // Call the register API
            await BankService.register(username, password);
            setSuccess('Registration successful! You can now login.');
            
            // Wait 2 seconds then notify parent to switch view
            setTimeout(() => {
                onRegisterSuccess();
            }, 2000);
        } catch (err) {
            // Handle registration failure (e.g., username taken)
            setError(err.response?.data || 'Failed to register');
        }
    };

    return (
        <div className="card shadow-sm mx-auto" style={{ maxWidth: '400px' }}>
            <div className="card-header bg-success text-white">
                <h5 className="mb-0">Register</h5>
            </div>
            <div className="card-body">
                {error && <div className="alert alert-danger">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Username</label>
                        <input 
                            type="text" 
                            className="form-control" 
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required 
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Password</label>
                        <input 
                            type="password" 
                            className="form-control" 
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required 
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Confirm Password</label>
                        <input 
                            type="password" 
                            className="form-control" 
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required 
                        />
                    </div>
                    <div className="d-grid">
                        <button type="submit" className="btn btn-success">Register</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;
