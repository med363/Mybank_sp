package com.example.Mybank.controller;

import com.example.Mybank.model.Account;
import com.example.Mybank.model.Transaction;
import com.example.Mybank.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    /**
     * Endpoint to create a new bank account.
     * Expects a JSON payload with "ownerName" and "initialDeposit".
     * Example: { "ownerName": "John", "initialDeposit": 100.0 }
     */
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> payload) {
        String ownerName = (String) payload.get("ownerName");
        // Safely partial parsing for initialDeposit, handling both Integer and Double from JSON
        BigDecimal initialDeposit = new BigDecimal(payload.get("initialDeposit").toString());
        
        // Handle User Association
        Long userId = null;
        if (payload.containsKey("userId")) {
            userId = Long.valueOf(payload.get("userId").toString());
        }
        
        if (userId != null) {
             return ResponseEntity.ok(bankService.createAccount(userId, ownerName, initialDeposit));
        } else {
             // Fallback for logic without user ID (though for this requirement, user ID is crucial)
             // We can allow anonymous account creation or throw error.
             // Given the requirement "register two user... own accounts", user ID is mandatory.
             throw new RuntimeException("User ID is required to create an account.");
        }
    }

    /**
     * Endpoint to retrieve all accounts.
     * Supports filtering by userId via query parameter.
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(bankService.getAccountsByUser(userId));
        }
        // Admin or global view (optional: restriction)
        return ResponseEntity.ok(bankService.getAllAccounts());
    }

    /**
     * Endpoint to retrieve account details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return bankService.getAccount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint to retrieve transaction history for a specific account.
     */
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(bankService.getTransactionHistory(id));
    }

    /**
     * Endpoint to deposit money into an account.
     * Expects JSON: { "amount": 50.0 }
     */
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(bankService.deposit(id, amount));
    }

    /**
     * Endpoint to withdraw money from an account.
     * Expects JSON: { "amount": 20.0 }
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(bankService.withdraw(id, amount));
    }

    /**
     * Endpoint to transfer money between two accounts.
     * Expects JSON: { "fromAccountId": 1, "toAccountId": 2, "amount": 30.0 }
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> payload) {
        // Validate input presence to avoid NullPointerException (which causes 500)
        if (!payload.containsKey("fromAccountId") || !payload.containsKey("toAccountId") || !payload.containsKey("amount")) {
            throw new RuntimeException("Missing required fields: fromAccountId, toAccountId, or amount");
        }

        Long fromId = Long.valueOf(payload.get("fromAccountId").toString());
        Long toId = Long.valueOf(payload.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        
        bankService.transfer(fromId, toId, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
