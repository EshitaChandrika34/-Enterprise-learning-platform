package com.enterpriselearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerGoalRequest {

    private Long userId; // Optional for authenticated user

    @NotBlank(message = "Target role is required")
    private String targetRole;

    private LocalDate targetDate;

    @NotBlank(message = "Status is required")
    private String status; // IN_PROGRESS, ACHIEVED, DEFERRED

    private String notes;
}
