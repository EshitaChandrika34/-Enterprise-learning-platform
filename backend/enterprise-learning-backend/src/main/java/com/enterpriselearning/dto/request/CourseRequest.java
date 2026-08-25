package com.enterpriselearning.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Course title is required")
    private String title;

    private String description;

    private String category;

    private String level; // Beginner, Intermediate, Advanced

    private Integer durationHours;

    private String instructor;
}
