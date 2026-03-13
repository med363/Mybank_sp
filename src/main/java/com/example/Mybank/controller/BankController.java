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

    // Create a new bank account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> payload) {
        String ownerName = (String) payload.get("ownerName");
        BigDecimal initialDeposit = new BigDecimal(payload.get("initialDeposit").toString());
        
        Account account = bankService.createAccount(ownerName, initialDeposit);
        return ResponseEntity.ok(account);
    }

    // Get account details
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return bankService.getAccount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get account transactions
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(bankService.getTransactionHistory(id));
    }

    // Deposit money
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(bankService.deposit(id, amount));
    }

    // Withdraw money
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        return ResponseEntity.ok(bankService.withdraw(id, amount));
    }

    // Transfer money
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> payload) {
        Long fromId = Long.valueOf(payload.get("fromAccountId").toString());
        Long toId = Long.valueOf(payload.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        
        bankService.transfer(fromId, toId, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
