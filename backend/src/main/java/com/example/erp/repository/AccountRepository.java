package com.example.erp.repository;

import com.example.erp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByName(String name);

    Optional<Account> findBySupplierId(Long supplierId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}