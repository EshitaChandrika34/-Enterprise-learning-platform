package com.enterpriselearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String targetRole;

    private String department;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String requiredSkills; // Comma-separated or JSON list of required skills

    @Column(length = 1000)
    private String recommendedCourses; // Comma-separated list of recommended course names/IDs

    private Integer minExperienceYears;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
