package com.infosys.learningservice.dto;

public class LearningCourseDTO {

    private Long courseId;
    private String courseName;
    private String description;
    private String trainerName;
    private Integer duration;
    private String level;

    public LearningCourseDTO() {
    }

    public LearningCourseDTO(Long courseId, String courseName, String description,
                             String trainerName, Integer duration, String level) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.trainerName = trainerName;
        this.duration = duration;
        this.level = level;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}