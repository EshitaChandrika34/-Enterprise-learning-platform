package com.infosys.userservice.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.userservice.dto.LoginRequest;
import com.infosys.userservice.dto.LoginResponse;
import com.infosys.userservice.dto.RegisterRequest;
import com.infosys.userservice.entity.Employee;
import com.infosys.userservice.repo.EmployeeRepository;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final EmployeeRepository
        employeeRepository;

    private final PasswordEncoder
        passwordEncoder;


    public AuthController(
        EmployeeRepository employeeRepository,
        PasswordEncoder passwordEncoder
    ) {

        this.employeeRepository =
            employeeRepository;

        this.passwordEncoder =
            passwordEncoder;
    }


    // ==========================================
    // REGISTER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?>
    register(
        @RequestBody RegisterRequest request
    ) {

        if (
            request.getFirstName() == null
            ||
            request.getFirstName().isBlank()
            ||
            request.getLastName() == null
            ||
            request.getLastName().isBlank()
            ||
            request.getEmail() == null
            ||
            request.getEmail().isBlank()
            ||
            request.getPassword() == null
            ||
            request.getPassword().isBlank()
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    "Required fields are missing."
                );
        }


        String email =
            request
                .getEmail()
                .trim()
                .toLowerCase();


        if (
            employeeRepository
                .existsByEmail(email)
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    "An account already exists with this email."
                );
        }


        // Generate a unique code automatically

        String employeeCode =
            "EMP-"
            +
            UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();


        Employee employee =
            new Employee();


        employee.setEmployeeCode(
            employeeCode
        );

        employee.setFirstName(
            request.getFirstName().trim()
        );

        employee.setLastName(
            request.getLastName().trim()
        );

        employee.setEmail(
            email
        );

        employee.setPhone(
            request.getPhone()
        );

        employee.setDepartment(
            request.getDepartment()
        );

        employee.setDesignation(
            request.getDesignation() != null
            &&
            !request.getDesignation().isBlank()
                ? request.getDesignation()
                : "Employee"
        );

        employee.setExperience(
            request.getExperience() != null
                ? request.getExperience()
                : 0
        );


        /*
         * For a real application:
         * public registration should normally
         * create EMPLOYEE only.
         *
         * Admin/HR should be created by an
         * administrator.
         */

        employee.setRole(
            "EMPLOYEE"
        );


        employee.setStatus(
            "Active"
        );


        employee.setPassword(
            passwordEncoder.encode(
                request.getPassword()
            )
        );


        Employee saved =
            employeeRepository.save(
                employee
            );


        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                createLoginResponse(saved)
            );
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?>
    login(
        @RequestBody LoginRequest request
    ) {

        if (
            request.getEmail() == null
            ||
            request.getPassword() == null
        ) {

            return ResponseEntity
                .badRequest()
                .body(
                    "Email and password are required."
                );
        }


        String email =
            request
                .getEmail()
                .trim()
                .toLowerCase();


        Employee employee =
            employeeRepository
                .findByEmail(email)
                .orElse(null);


        if (employee == null) {

            return ResponseEntity
                .status(
                    HttpStatus.UNAUTHORIZED
                )
                .body(
                    "Invalid email or password."
                );
        }


        if (
            !passwordEncoder.matches(
                request.getPassword(),
                employee.getPassword()
            )
        ) {

            return ResponseEntity
                .status(
                    HttpStatus.UNAUTHORIZED
                )
                .body(
                    "Invalid email or password."
                );
        }


        if (
            employee.getStatus() != null
            &&
            !employee
                .getStatus()
                .equalsIgnoreCase(
                    "Active"
                )
        ) {

            return ResponseEntity
                .status(
                    HttpStatus.FORBIDDEN
                )
                .body(
                    "This account is inactive."
                );
        }


        return ResponseEntity.ok(
            createLoginResponse(
                employee
            )
        );
    }


    // ==========================================
    // RESPONSE
    // ==========================================

    private LoginResponse createLoginResponse(
            Employee employee) {

        return new LoginResponse(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getExperience(),
                employee.getStatus(),
                employee.getRole()
        );
    }
}