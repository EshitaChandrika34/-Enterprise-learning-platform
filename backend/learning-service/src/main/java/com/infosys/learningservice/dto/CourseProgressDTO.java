package com.infosys.learningservice.dto;

import java.time.LocalDate;

public class CourseProgressDTO {

    private Long progressId;
    private Long enrollmentId;
    private Integer progressPercentage;
    private String completionStatus;
    private LocalDate lastUpdatedDate;

    public CourseProgressDTO() {
    }

    public CourseProgressDTO(Long progressId, Long enrollmentId,
            Integer progressPercentage, String completionStatus,
            LocalDate lastUpdatedDate) {

        this.progressId = progressId;
        this.enrollmentId = enrollmentId;
        this.progressPercentage = progressPercentage;
        this.completionStatus = completionStatus;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}