package com.example.Mybank.service;

import com.example.Mybank.model.Account;
import com.example.Mybank.model.Transaction;
import com.example.Mybank.model.TransactionType;
import com.example.Mybank.model.User;
import com.example.Mybank.repository.AccountRepository;
import com.example.Mybank.repository.TransactionRepository;
import com.example.Mybank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BankService {

    private final AccountRepository accountRepository; // Injecting Account Repository
    private final TransactionRepository transactionRepository; // Injecting Transaction Repository
    private final UserRepository userRepository; // Injecting User Repository

    /**
     * Create a new bank account for a given user.
     * Generates account number, RIB, and IBAN automatically (Tunisian Standard).
     */
    @Transactional
    public Account createAccount(Long userId, String ownerName, BigDecimal initialDeposit) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setUser(user);
        account.setOwnerName(ownerName);
        account.setBalance(initialDeposit);

        // Generate comprehensive banking details (Tunisian format)
        // Bank Code: 2 digits (e.g., 10 for STB or similar)
        String bankCode = "10"; 
        // Branch Code: 3 digits
        String branchCode = "001";
        // Account Number: 13 digits
        String accountNumber = generateAccountNumber();
        
        // Calculate RIB Key
        String ribKey = calculateRibKey(bankCode, branchCode, accountNumber);
        
        // Construct RIB and IBAN
        // RIB: Bank(2) + Branch(3) + Account(13) + Key(2) = 20 digits
        String rib = bankCode + branchCode + accountNumber + ribKey;
        String iban = generateIban("TN", bankCode, branchCode, accountNumber, ribKey);

        account.setAccountNumber(accountNumber);
        account.setBankCode(bankCode);
        account.setBranchCode(branchCode);
        account.setRibKey(ribKey);
        account.setItemsRIB(rib);
        account.setIban(iban); 

        // Save the account
        Account savedAccount = accountRepository.save(account);

        // Record initial deposit as a transaction
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            recordTransaction(savedAccount, initialDeposit, TransactionType.DEPOSIT);
        }

        return savedAccount;
    }

    /**
     * Retrieve all accounts for a specific user
     */
    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Helper to retrieve an account by ID
     */
    public Optional<Account> getAccount(Long id) {
        return accountRepository.findById(id);
    }

    /**
     * Retrieve all accounts
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    /**
     * Helper to retrieve an account by Account Number
     */
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Deposit money into an account
     */
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account with ID " + accountId + " not found"));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance().add(amount));
        recordTransaction(account, amount, TransactionType.DEPOSIT);
        
        return accountRepository.save(account);
    }

    /**
     * Withdraw money from an account
     */
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account with ID " + accountId + " not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in account " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));
        recordTransaction(account, amount, TransactionType.WITHDRAWAL);
        
        return accountRepository.save(account);
    }

    /**
     * Transfer money between two accounts
     * Applies 5% commission if accounts belong to different users.
     */
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Validate amounts
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new RuntimeException("Source account (ID " + fromAccountId + ") not found"));
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new RuntimeException("Destination account (ID " + toAccountId + ") not found"));

        // Determine Commission Logic
        BigDecimal commission = BigDecimal.ZERO;
        
        // Check if both accounts have users linked and if they are different
        boolean fromHasUser = fromAccount.getUser() != null;
        boolean toHasUser = toAccount.getUser() != null;
        
        if (fromHasUser && toHasUser && !fromAccount.getUser().getId().equals(toAccount.getUser().getId())) {
             // Different users -> Apply 5% Commission
             commission = amount.multiply(new BigDecimal("0.05"));
        }
        
        BigDecimal totalDeduction = amount.add(commission);

        // Check Balance
        if (fromAccount.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Insufficient balance including commission of " + commission + " TND");
        }

        // Perform Transfer
        // 1. Deduct from source
        fromAccount.setBalance(fromAccount.getBalance().subtract(totalDeduction));
        accountRepository.save(fromAccount);
        recordTransaction(fromAccount, totalDeduction, TransactionType.WITHDRAWAL); // Record full deduction
        
        // 2. Add to destination (original amount)
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);
        recordTransaction(toAccount, amount, TransactionType.DEPOSIT);

        // 3. Record transfer log if needed specifically
        recordTransaction(fromAccount, amount, TransactionType.TRANSFER); 
    }
    
    /**
     * Get transaction history for an account
     */
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    // --- Private Helper Methods ---

    private void recordTransaction(Account account, BigDecimal amount, TransactionType type) {
        Transaction text = new Transaction();
        text.setAccount(account);
        text.setAmount(amount);
        text.setType(type);
        text.setTimestamp(java.time.LocalDateTime.now());
        transactionRepository.save(text);
    }

    private String generateAccountNumber() {
        // Generate a random 13-digit string (Tunisian Standard)
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    // RIB Key Calculation: 97 - (Numeric representation of (BankCode + BranchCode + AccountNumber + "00") MOD 97)
    private String calculateRibKey(String bankCode, String branchCode, String accountNumber) {
        // Concatenate Bank Code + Branch Code + Account Number + "00"
        String b = bankCode + branchCode + accountNumber + "00";
        BigInteger bigInt = new BigInteger(b);
        int remainder = bigInt.mod(BigInteger.valueOf(97)).intValue();
        int key = 97 - remainder;
        
        // If remainder is 0, key is 97. Key ranges from 01 to 97.
        return String.format("%02d", key);
    }
    
    // IBAN generation (Tunisian Standard)
    // Formula: CheckDigits = 98 - (Numeric representation of (BBAN + CountryCode + 00)) MOD 97
    private String generateIban(String countryCode, String bankCode, String branchCode, String accountNumber, String ribKey) {
        // BBAN for Tunisia is BankCode(2) + BranchCode(3) + AccountNumber(13) + Key(2)
        String bban = bankCode + branchCode + accountNumber + ribKey;
        
        // Country Code to numeric (A=10, ... T=29, N=23)
        // TN -> 2923
        String countryNumeric;
        if ("TN".equals(countryCode)) {
            countryNumeric = "2923"; 
        } else {
             // Fallback to FR behavior or throw error, but for this task we assume TN
             countryNumeric = "1527"; // FR
        }

        String checkString = bban + countryNumeric + "00";
        
        BigInteger ibanNumber = new BigInteger(checkString);
        int remainder = ibanNumber.mod(BigInteger.valueOf(97)).intValue();
        int checkDigits = 98 - remainder;
        
        return countryCode + String.format("%02d", checkDigits) + bban;
    }
}
