package com.infosys.learningservice.dto;

import java.time.LocalDate;

public class EnrollmentDTO {

    private Long enrollmentId;

    private Long employeeId;

    private Long courseId;

    private LocalDate enrollmentDate;

    private String status;


    public EnrollmentDTO() {
    }


    public EnrollmentDTO(
            Long enrollmentId,
            Long employeeId,
            Long courseId,
            LocalDate enrollmentDate,
            String status) {

        this.enrollmentId = enrollmentId;
        this.employeeId = employeeId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }


    public Long getEnrollmentId() {
        return enrollmentId;
    }


    public void setEnrollmentId(
            Long enrollmentId) {

        this.enrollmentId = enrollmentId;
    }


    public Long getEmployeeId() {
        return employeeId;
    }


    public void setEmployeeId(
            Long employeeId) {

        this.employeeId = employeeId;
    }


    public Long getCourseId() {
        return courseId;
    }


    public void setCourseId(
            Long courseId) {

        this.courseId = courseId;
    }


    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }


    public void setEnrollmentDate(
            LocalDate enrollmentDate) {

        this.enrollmentDate =
                enrollmentDate;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(
            String status) {

        this.status = status;
    }
}