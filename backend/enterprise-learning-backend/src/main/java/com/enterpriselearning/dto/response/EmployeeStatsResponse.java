package com.enterpriselearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatsResponse {
    private long totalEmployees;
    private long totalManagers;
    private long totalAdmins;
    private Map<String, Long> employeesByDepartment;
}
