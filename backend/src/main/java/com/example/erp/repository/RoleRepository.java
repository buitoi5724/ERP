package com.example.erp.repository;

import com.example.erp.entity.Role;
import com.example.erp.util.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}