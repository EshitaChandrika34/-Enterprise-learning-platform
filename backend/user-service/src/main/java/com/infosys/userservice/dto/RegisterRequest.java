package com.infosys.userservice.dto;

public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String department;

    private String designation;

    private Integer experience;

    private String role;

    private String password;


    public RegisterRequest() {
    }


    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String phone,
            String department,
            String designation,
            Integer experience,
            String role,
            String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.experience = experience;
        this.role = role;
        this.password = password;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(
            String firstName) {

        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(
            String lastName) {

        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(
            String email) {

        this.email = email;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(
            String phone) {

        this.phone = phone;
    }


    public String getDepartment() {
        return department;
    }


    public void setDepartment(
            String department) {

        this.department = department;
    }


    public String getDesignation() {
        return designation;
    }


    public void setDesignation(
            String designation) {

        this.designation = designation;
    }


    public Integer getExperience() {
        return experience;
    }


    public void setExperience(
            Integer experience) {

        this.experience = experience;
    }


    public String getRole() {
        return role;
    }


    public void setRole(
            String role) {

        this.role = role;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(
            String password) {

        this.password = password;
    }
}