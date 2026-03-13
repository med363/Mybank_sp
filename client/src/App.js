// Import React hooks for state management
import React, { useState, useEffect } from 'react';
// Import our API service (which we updated to include auth)
import BankService from './services/api';
// Import our components
import AccountList from './components/AccountList';
import CreateAccount from './components/CreateAccount';
import TransferForm from './components/TransferForm';
import Login from './components/Login';
import Register from './components/Register';

// Import Bootstrap CSS for styling
import 'bootstrap/dist/css/bootstrap.min.css';

/**
 * Main Application Component
 * 
 * Updated to include Authentication Flow:
 * 1. Checks if a user is logged in.
 * 2. If NOT logged in, shows Login or Register screens.
 * 3. If logged in, shows the Dashboard.
 */
function App() {
    // --- State Management ---
    // User state: Stores the logged-in user object (null if not logged in)
    const [user, setUser] = useState(null);
    // View state: Controls which screen is shown ('login', 'register', 'dashboard')
    const [currentView, setCurrentView] = useState('login');
    
    // Dashboard Data States
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // --- Authentication Handlers ---

    // Called when Login component successfully authenticates
    const handleLoginSuccess = (userData) => {
        console.log("Logged in as:", userData.username);
        setUser(userData);
        setCurrentView('dashboard');
        // Retrieve data immediately after login
        fetchAccounts();
    };

    // Called when the user clicks 'Logout'
    const handleLogout = () => {
        setUser(null);
        setAccounts([]); // Clear sensitive data
        setCurrentView('login');
    };

    // Called when Register component successfully registers a user
    const handleRegisterSuccess = () => {
        // Switch to login screen so they can log in with new credentials
        setCurrentView('login');
    };

    // --- Data Fetching ---

    // Function to fetch accounts from the backend (Only called when logged in)
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

    // --- Render Logic ---

    // 1. If no user is logged in, show Auth screens
    if (!user) {
        return (
            <div className="App bg-light min-vh-100 d-flex flex-column align-items-center justify-content-center">
                <div className="text-center mb-4">
                    <h1 className="fw-bold text-primary">🏦 MyBank</h1>
                    <p className="text-muted">Secure Banking Portal</p>
                </div>

                {/* Toggle between Login and Register views */}
                {currentView === 'login' ? (
                    <>
                        <Login onLogin={handleLoginSuccess} />
                        <div className="mt-3">
                            <span className="text-muted">Don't have an account? </span>
                            <button 
                                className="btn btn-link p-0" 
                                onClick={() => setCurrentView('register')}
                            >
                                Register here
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <Register onRegisterSuccess={handleRegisterSuccess} />
                        <div className="mt-3">
                            <span className="text-muted">Already have an account? </span>
                            <button 
                                className="btn btn-link p-0" 
                                onClick={() => setCurrentView('login')}
                            >
                                Login here
                            </button>
                        </div>
                    </>
                )}
            </div>
        );
    }

    // 2. If user IS logged in, show the Dashboard
    return (
        <div className="App bg-light min-vh-100">
            {/* Navbar with Logout */}
            <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4">
                <div className="container">
                    <span className="navbar-brand fw-bold">
                        🏦 MyBank Dashboard
                    </span>
                    <div className="d-flex align-items-center text-white">
                        <span className="me-3">Welcome, <strong>{user.username}</strong>!</span>
                        <button 
                            className="btn btn-outline-light btn-sm" 
                            onClick={handleLogout}
                        >
                            Logout
                        </button>
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
                        <CreateAccount onAccountCreated={fetchAccounts} />
                        <TransferForm 
                            accounts={accounts} 
                            onTransferCompleted={fetchAccounts} 
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
                            <strong>Note:</strong> You are viewing the protected dashboard. 
                            In a real full-stack app, your session would be maintained by a JWT token or Cookies.
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
