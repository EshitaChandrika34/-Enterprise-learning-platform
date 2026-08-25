package com.enterpriselearning.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String department;

    private String location;

    private String employmentType; // Full-time, Contract, Remote, Hybrid

    @Column(length = 3000, nullable = false)
    private String description;

    @Column(length = 1000)
    private String requiredSkills; // Comma-separated list of required skills

    private Integer minExperienceYears;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private JobPostingStatus status = JobPostingStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_id")
    private User postedBy;

    private LocalDate deadline;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
