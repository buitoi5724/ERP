package com.example.erp.service;

import com.example.erp.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account create(Account account);

    List<Account> getAll();

    Optional<Account> getById(Long id);

    Account checkEmail(String email);

    Account checkUsername(String username);

    Account checkName(String name);

    Account update(Long id, Account newAccount);
    
    Optional<Account> getBySupplierId(Long supplierId);
    

    void delete(Long id);
}
