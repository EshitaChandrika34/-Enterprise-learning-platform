package com.enterpriselearning.ai;

import lombok.Data;

import java.util.List;

@Data
public class CareerAiRequest {

    private String message;

    private Long employeeId;

    private String targetRole;

    private List<String> skills;

    private List<String> missingSkills;
}