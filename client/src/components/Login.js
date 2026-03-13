import React, { useState } from 'react';
import BankService from '../services/api';

/**
 * Login Component
 * Allows users to authenticate with their username and password.
 */
const Login = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        
        try {
            // Call the login API
            const response = await BankService.login(username, password);
            // If successful, pass the user data up to the parent component
            onLogin(response.data);
        } catch (err) {
            // Handle login failure
            setError('Invalid username or password');
            console.error(err);
        }
    };

    return (
        <div className="card shadow-sm mx-auto" style={{ maxWidth: '400px' }}>
            <div className="card-header bg-primary text-white">
                <h5 className="mb-0">Login</h5>
            </div>
            <div className="card-body">
                {error && <div className="alert alert-danger">{error}</div>}
                
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
                    <div className="d-grid">
                        <button type="submit" className="btn btn-primary">Login</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
