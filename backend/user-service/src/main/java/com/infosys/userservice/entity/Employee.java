package com.infosys.userservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;


    @Column(
            nullable = false,
            unique = true,
            length = 20
    )
    private String employeeCode;


    @Column(
            nullable = false,
            length = 50
    )
    private String firstName;


    @Column(
            nullable = false,
            length = 50
    )
    private String lastName;


    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String email;


    @Column(length = 15)
    private String phone;


    @Column(length = 100)
    private String department;


    @Column(length = 100)
    private String designation;


    private Integer experience;


    @Column(length = 20)
    private String status;


    @Column(length = 30)
    private String role;


    @Column(length = 100)
    private String password;


    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    // ==========================================
    // EMPTY CONSTRUCTOR
    // ==========================================

    public Employee() {
    }


    // ==========================================
    // EMPLOYEE ID
    // ==========================================

    public Long getEmployeeId() {
        return employeeId;
    }


    public void setEmployeeId(
            Long employeeId) {

        this.employeeId = employeeId;
    }


    // ==========================================
    // EMPLOYEE CODE
    // ==========================================

    public String getEmployeeCode() {
        return employeeCode;
    }


    public void setEmployeeCode(
            String employeeCode) {

        this.employeeCode = employeeCode;
    }


    // ==========================================
    // FIRST NAME
    // ==========================================

    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(
            String firstName) {

        this.firstName = firstName;
    }


    // ==========================================
    // LAST NAME
    // ==========================================

    public String getLastName() {
        return lastName;
    }


    public void setLastName(
            String lastName) {

        this.lastName = lastName;
    }


    // ==========================================
    // EMAIL
    // ==========================================

    public String getEmail() {
        return email;
    }


    public void setEmail(
            String email) {

        this.email = email;
    }


    // ==========================================
    // PHONE
    // ==========================================

    public String getPhone() {
        return phone;
    }


    public void setPhone(
            String phone) {

        this.phone = phone;
    }


    // ==========================================
    // DEPARTMENT
    // ==========================================

    public String getDepartment() {
        return department;
    }


    public void setDepartment(
            String department) {

        this.department = department;
    }


    // ==========================================
    // DESIGNATION
    // ==========================================

    public String getDesignation() {
        return designation;
    }


    public void setDesignation(
            String designation) {

        this.designation = designation;
    }


    // ==========================================
    // EXPERIENCE
    // ==========================================

    public Integer getExperience() {
        return experience;
    }


    public void setExperience(
            Integer experience) {

        this.experience = experience;
    }


    // ==========================================
    // STATUS
    // ==========================================

    public String getStatus() {
        return status;
    }


    public void setStatus(
            String status) {

        this.status = status;
    }


    // ==========================================
    // ROLE
    // ==========================================

    public String getRole() {
        return role;
    }


    public void setRole(
            String role) {

        this.role = role;
    }


    // ==========================================
    // PASSWORD
    // ==========================================

    public String getPassword() {
        return password;
    }


    public void setPassword(
            String password) {

        this.password = password;
    }


    // ==========================================
    // CREATED AT
    // ==========================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    // ==========================================
    // UPDATED AT
    // ==========================================

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }


    // ==========================================
    // CREATE DATE
    // ==========================================

    @PrePersist
    public void onCreate() {

        createdAt =
                LocalDateTime.now();

        updatedAt =
                LocalDateTime.now();
    }


    // ==========================================
    // UPDATE DATE
    // ==========================================

    @PreUpdate
    public void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}