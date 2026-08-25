package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.EmployeeCreateRequest;
import com.enterpriselearning.dto.request.EmployeeUpdateRequest;
import com.enterpriselearning.dto.response.UserResponse;

import java.util.List;

public interface EmployeeService {
    UserResponse createEmployee(EmployeeCreateRequest request);
    List<UserResponse> getAllEmployees();
    UserResponse getEmployeeById(Long id);
    UserResponse updateEmployee(Long id, EmployeeUpdateRequest request);
    void deleteEmployee(Long id);
    List<UserResponse> getEmployeesByDepartment(String department);
}
