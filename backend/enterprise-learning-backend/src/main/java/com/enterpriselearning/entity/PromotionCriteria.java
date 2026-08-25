package com.enterpriselearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion_criteria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer minimumScore; // e.g. 75

    private String targetRole; // e.g. "Senior Software Engineer"

    private String requiredDepartment; // e.g. "Engineering"

    private Integer minExperienceYears; // e.g. 3

    @Column(length = 1000)
    private String requiredSkills; // e.g. "Java, Spring Boot, SQL & Database Design"

    private Integer minCompletedCourses; // e.g. 2

    private Integer minCertifications; // e.g. 1

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}
