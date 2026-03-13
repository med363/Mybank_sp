package com.example.Mybank.service;

import com.example.Mybank.model.Account;
import com.example.Mybank.model.Transaction;
import com.example.Mybank.model.TransactionType;
import com.example.Mybank.repository.AccountRepository;
import com.example.Mybank.repository.TransactionRepository;
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

    /**
     * Create a new bank account for a given owner.
     * Generates account number, RIB, and IBAN automatically.
     */
    @Transactional
    public Account createAccount(String ownerName, BigDecimal initialDeposit) {
        Account account = new Account();
        account.setOwnerName(ownerName);
        account.setBalance(initialDeposit);

        // Generate comprehensive banking details
        String bankCode = "12345";
        String branchCode = "00001";
        String accountNumber = generateAccountNumber();
        
        // Calculate RIB Key
        String ribKey = calculateRibKey(bankCode, branchCode, accountNumber);
        
        // Construct RIB and IBAN
        String rib = bankCode + branchCode + accountNumber + ribKey;
        String iban = generateIban("FR", bankCode, branchCode, accountNumber, ribKey);

        account.setAccountNumber(accountNumber);
        account.setBankCode(bankCode);
        account.setBranchCode(branchCode);
        account.setRibKey(ribKey);
        account.setItemsRIB(rib);
        account.setIban(iban); // Assuming French accounts for this example

        // Save the account
        Account savedAccount = accountRepository.save(account);

        // Record initial deposit as a transaction
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            recordTransaction(savedAccount, initialDeposit, TransactionType.DEPOSIT);
        }

        return savedAccount;
    }

    /**
     * Helper to retrieve an account by ID
     */
    public Optional<Account> getAccount(Long id) {
        return accountRepository.findById(id);
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
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
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
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        recordTransaction(account, amount, TransactionType.WITHDRAWAL);
        
        return accountRepository.save(account);
    }

    /**
     * Transfer money between two accounts
     */
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Withdraw from source
        withdraw(fromAccountId, amount);
        
        // Deposit to destination
        deposit(toAccountId, amount);

        // Ideally, we might want to link these transactions or note them specially, 
        // but for now, individual records suffice.
        Account fromAccount = accountRepository.findById(fromAccountId).get();
        recordTransaction(fromAccount, amount, TransactionType.TRANSFER); // Add transfer record
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
        // Generate a random 11-digit string
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    // RIB Key Calculation: 97 - ((89 * BankCode + 15 * BranchCode + 3 * AccountNumber) % 97)
    private String calculateRibKey(String bankCode, String branchCode, String accountNumber) {
        // Extended RIB Key Calculation using BigInteger for safety
        // Formula: 97 - ((89 * Bank + 15 * Branch + 3 * Account) % 97) is approximation
        // Better: Concatenate Bank Code + Branch Code + Account Number + "00"
        // Convert to BigInteger and calculate Modulo 97
        // Key = 97 - Remainder
        
        // Ensure inputs are numeric only
        String b = bankCode + branchCode + accountNumber + "00";
        BigInteger bigInt = new BigInteger(b);
        int remainder = bigInt.mod(BigInteger.valueOf(97)).intValue();
        int key = 97 - remainder;
        
        return String.format("%02d", key == 97 ? 0 : key);
    }
    
    // IBAN generation (simplified for FR)
    // Formula: CheckDigits = 98 - (Numeric representation of (BBAN + CountryCode + 00)) MOD 97
    private String generateIban(String countryCode, String bankCode, String branchCode, String accountNumber, String ribKey) {
        // BBAN for France is BankCode (5) + BranchCode (5) + AccountNumber (11) + Key (2)
        String bban = bankCode + branchCode + accountNumber + ribKey;
        
        // Country Code to numeric (A=10, B=11... F=15... R=27)
        // FR -> 1527
        // We append '00' at the end for the check digit calculation
        // But for IBAN Check Digit calculation, the country code and check digits are moved to the END.
        // So we take (BBAN + CountryCodeNumerics + "00")
        
        String countryNumeric = "1527"; // FR
        String checkString = bban + countryNumeric + "00";
        
        BigInteger ibanNumber = new BigInteger(checkString);
        int remainder = ibanNumber.mod(BigInteger.valueOf(97)).intValue();
        int checkDigits = 98 - remainder;
        
        return countryCode + String.format("%02d", checkDigits) + bban;
    }
}
