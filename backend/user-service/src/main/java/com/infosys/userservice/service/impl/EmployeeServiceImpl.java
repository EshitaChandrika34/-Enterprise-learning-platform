package com.infosys.userservice.service.impl;

import com.infosys.userservice.dto.EmployeeDTO;
import com.infosys.userservice.entity.Employee;
import com.infosys.userservice.exception.ResourceNotFoundException;
import com.infosys.userservice.repo.EmployeeRepository;
import com.infosys.userservice.service.EmployeeService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDTO createEmployee(
            EmployeeDTO employeeDTO) {

        Employee employee =
                mapToEntity(employeeDTO);

        Employee savedEmployee =
                employeeRepository.save(employee);

        return mapToDTO(savedEmployee);
    }

    @Override
    public EmployeeDTO getEmployeeById(
            Long employeeId) {

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId
                                )
                        );

        return mapToDTO(employee);
    }

    @Override
    public EmployeeDTO getEmployeeByEmployeeCode(
            String employeeCode) {

        Employee employee =
                employeeRepository
                        .findByEmployeeCode(employeeCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with code: "
                                                + employeeCode
                                )
                        );

        return mapToDTO(employee);
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(
            String email) {

        Employee employee =
                employeeRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with email: "
                                                + email
                                )
                        );

        return mapToDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {

        return employeeRepository
                .findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(
            String department) {

        return employeeRepository
                .findByDepartment(department)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByDesignation(
            String designation) {

        return employeeRepository
                .findByDesignation(designation)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDTO> getEmployeesByStatus(
            String status) {

        return employeeRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDTO updateEmployee(
            Long employeeId,
            EmployeeDTO employeeDTO) {

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId
                                )
                        );

        employee.setEmployeeCode(
                employeeDTO.getEmployeeCode()
        );

        employee.setFirstName(
                employeeDTO.getFirstName()
        );

        employee.setLastName(
                employeeDTO.getLastName()
        );

        employee.setEmail(
                employeeDTO.getEmail()
        );

        employee.setPhone(
                employeeDTO.getPhone()
        );

        employee.setDepartment(
                employeeDTO.getDepartment()
        );

        employee.setDesignation(
                employeeDTO.getDesignation()
        );

        employee.setExperience(
                employeeDTO.getExperience()
        );

        employee.setStatus(
                employeeDTO.getStatus()
        );

        employee.setRole(
                employeeDTO.getRole()
        );

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return mapToDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId
                                )
                        );

        employeeRepository.delete(employee);
    }

    private EmployeeDTO mapToDTO(
            Employee employee) {

        EmployeeDTO dto =
                new EmployeeDTO();

        dto.setEmployeeId(
                employee.getEmployeeId()
        );

        dto.setEmployeeCode(
                employee.getEmployeeCode()
        );

        dto.setFirstName(
                employee.getFirstName()
        );

        dto.setLastName(
                employee.getLastName()
        );

        dto.setEmail(
                employee.getEmail()
        );

        dto.setPhone(
                employee.getPhone()
        );

        dto.setDepartment(
                employee.getDepartment()
        );

        dto.setDesignation(
                employee.getDesignation()
        );

        dto.setExperience(
                employee.getExperience()
        );

        dto.setStatus(
                employee.getStatus()
        );

        dto.setRole(
                employee.getRole()
        );

        return dto;
    }

    private Employee mapToEntity(
            EmployeeDTO dto) {

        Employee employee =
                new Employee();

        employee.setEmployeeCode(
                dto.getEmployeeCode()
        );

        employee.setFirstName(
                dto.getFirstName()
        );

        employee.setLastName(
                dto.getLastName()
        );

        employee.setEmail(
                dto.getEmail()
        );

        employee.setPhone(
                dto.getPhone()
        );

        employee.setDepartment(
                dto.getDepartment()
        );

        employee.setDesignation(
                dto.getDesignation()
        );

        employee.setExperience(
                dto.getExperience()
        );

        employee.setStatus(
                dto.getStatus()
        );

        employee.setRole(
                dto.getRole()
        );

        return employee;
    }
}