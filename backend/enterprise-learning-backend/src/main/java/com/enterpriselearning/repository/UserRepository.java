package com.enterpriselearning.repository;

import com.enterpriselearning.entity.Role;
import com.enterpriselearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    List<User> findByDepartment(String department);
    List<User> findByRole(Role role);
    long countByRole(Role role);
    long countByDepartment(String department);
}
