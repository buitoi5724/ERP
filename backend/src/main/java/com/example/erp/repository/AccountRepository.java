package com.example.erp.repository;

import com.example.erp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByEmail(String email);
	
    Account findByUsername (String username);
     Account findByName (String name);
  
     
	//findByUsername
};