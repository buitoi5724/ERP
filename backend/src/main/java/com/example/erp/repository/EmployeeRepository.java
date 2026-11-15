package com.example.erp.repository;

import com.example.erp.entity.Employee;
import com.example.erp.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserId(Long userId);

    Optional<Employee> findByEmpCode(String empCode);
    Optional<Employee> findByUser(User user);
}