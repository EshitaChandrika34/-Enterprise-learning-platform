package com.infosys.userservice.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.userservice.entity.Employee;


@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(
            String employeeCode
    );

    Optional<Employee> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

    List<Employee> findByDepartment(
            String department
    );

    List<Employee> findByDesignation(
            String designation
    );

    List<Employee> findByStatus(
            String status
    );
}