package com.example.erp.service;

import com.example.erp.entity.Account;
import com.example.erp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(Account account) {
    	Account savedAccount  = checkEmail(account.getEmail());
    	if(savedAccount != null) return savedAccount;
        
    	
    	//TODO: check username
    	
    	accountRepository.save(account);
    	return null;
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

    public Account update(Long id, Account newAccount) {
        return accountRepository.findById(id)
                .map(account -> {
                    account.setUsername(newAccount.getUsername());
                    account.setEmail(newAccount.getEmail());
                    return accountRepository.save(account);
                })
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public void delete(Long id) {
        accountRepository.deleteById(id);
    }
}