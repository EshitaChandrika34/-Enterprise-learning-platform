package com.enterpriselearning.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {

    private Long userId; // Optional for authenticated user (extracted from JWT if omitted)

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
