import axios from 'axios';

// Create an Axios instance with base URL pointing to the Spring Boot backend
const API_BASE_URL = "http://localhost:8080/api/accounts";

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Service methods for interacting with the backend
const BankService = {
    // 1. Create a new account
    createAccount: (ownerName, initialDeposit) => {
        return api.post('', { ownerName, initialDeposit });
    },

    // 2. Get details of a specific account by ID
    getAccount: (id) => {
        return api.get(`/${id}`);
    },

    // 2.5 Get ALL accounts
    getAllAccounts: () => {
        return api.get('');
    },

    // 3. Get transaction history for an account
    getTransactions: (id) => {
        return api.get(`/${id}/transactions`);
    },

    // 4. Deposit money into an account
    deposit: (id, amount) => {
        return api.post(`/${id}/deposit`, { amount });
    },

    // 5. Withdraw money from an account
    withdraw: (id, amount) => {
        return api.post(`/${id}/withdraw`, { amount });
    },

    // 6. Transfer money between two accounts
    transfer: (fromAccountId, toAccountId, amount) => {
        return api.post('/transfer', { fromAccountId, toAccountId, amount });
    }
};

export default BankService;
