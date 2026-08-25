package com.infosys.userservice.service;

import com.infosys.userservice.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO getEmployeeById(Long employeeId);

    EmployeeDTO getEmployeeByEmployeeCode(String employeeCode);

    EmployeeDTO getEmployeeByEmail(String email);

    List<EmployeeDTO> getAllEmployees();

    List<EmployeeDTO> getEmployeesByDepartment(String department);

    List<EmployeeDTO> getEmployeesByDesignation(String designation);

    List<EmployeeDTO> getEmployeesByStatus(String status);

    EmployeeDTO updateEmployee(
            Long employeeId,
            EmployeeDTO employeeDTO
    );

    void deleteEmployee(Long employeeId);
}