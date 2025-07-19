package com.example.erp.service;

import com.example.erp.entity.Account;
import com.example.erp.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account create(Account account) {
        Account existingByEmail = accountRepository.findByEmail(account.getEmail());
        if (existingByEmail != null) {
            throw new RuntimeException("Email đã tồn tại: " + account.getEmail());
        }

        Account existingByUsername = accountRepository.findByUsername(account.getUsername());
        if (existingByUsername != null) {
            throw new RuntimeException("Username đã tồn tại: " + account.getUsername());
        }

        Account existingByName = accountRepository.findByName(account.getName());
        if (existingByName != null) {
            throw new RuntimeException("Name đã tồn tại: " + account.getName());
        }
        
       
        
        return accountRepository.save(account);
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    public Account checkEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Account checkUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    public Account checkName(String name) {
        return accountRepository.findByName(name);
    }
    
    @Transactional
    public Account update(Long id, Account newAccount) {
        return accountRepository.findById(id)
                .map(account -> {
                    account.setEmail(newAccount.getEmail());
                    account.setName(newAccount.getName());
                    account.setPassword(newAccount.getPassword());
                    account.setUsername(newAccount.getUsername());
                    return accountRepository.save(account);
                })
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public void delete(Long id) {
        accountRepository.deleteById(id);
    }
}
