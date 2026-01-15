package com.example.erp.service.impl;

import com.example.erp.entity.Account;
import com.example.erp.repository.AccountRepository;
import com.example.erp.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account create(Account account) {
        if (accountRepository.findByEmail(account.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại: " + account.getEmail());
        }
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new RuntimeException("Username đã tồn tại: " + account.getUsername());
        }
        if (accountRepository.findByName(account.getName()) != null) {
            throw new RuntimeException("Name đã tồn tại: " + account.getName());
        }

        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Account checkEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account checkUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account checkName(String name) {
        return accountRepository.findByName(name);
    }

    @Override
    @Transactional
    public Account update(Long id, Account newAccount) {
        return accountRepository.findById(id)
                .map(account -> {
                    account.setEmail(newAccount.getEmail());
                    account.setName(newAccount.getName());
                    account.setPassword(newAccount.getPassword());
                    account.setUsername(newAccount.getUsername());
                    return accountRepository.save(account);
                }).orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public Optional<Account> getBySupplierId(Long supplierId) {
        return accountRepository.findBySupplierId(supplierId);
    }
    
}
