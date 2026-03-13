import axios from 'axios';

// Create an Axios instance with base URL pointing to the Spring Boot backend
// Updated base URL to point to /api so we can access both /auth and /accounts
const API_BASE_URL = "http://localhost:8080/api";

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Service methods for interacting with the backend
const BankService = {
    // 0. Authentication Methods
    
    // Register a new user
    register: (username, password) => {
        return api.post('/auth/register', { username, password });
    },

    // Login an existing user
    login: (username, password) => {
        return api.post('/auth/login', { username, password });
    },

    // 1. Create a new account
    createAccount: (userId, ownerName, initialDeposit) => {
        return api.post('/accounts', { userId, ownerName, initialDeposit });
    },

    // 2. Get details of a specific account by ID
    getAccount: (id) => {
        return api.get(`/accounts/${id}`);
    },

    // 2.5 Get ALL accounts for a user (or all if user is null/admin)
    getAllAccounts: (userId = null) => {
        // Here we always fetch all accounts to facilitate transfers between users
        // The backend supports filtering, but for the UI dropdowns we need everything.
        // We'll filter on the client side.
        return api.get('/accounts'); 
    },

    // 3. Get transaction history for an account
    getTransactions: (id) => {
        return api.get(`/accounts/${id}/transactions`);
    },

    // 4. Deposit money into an account
    deposit: (id, amount) => {
        return api.post(`/accounts/${id}/deposit`, { amount });
    },

    // 5. Withdraw money from an account
    withdraw: (id, amount) => {
        return api.post(`/accounts/${id}/withdraw`, { amount });
    },

    // 6. Transfer money between two accounts
    transfer: (fromAccountId, toAccountId, amount) => {
        return api.post('/accounts/transfer', { fromAccountId, toAccountId, amount });
    }
};

export default BankService;
