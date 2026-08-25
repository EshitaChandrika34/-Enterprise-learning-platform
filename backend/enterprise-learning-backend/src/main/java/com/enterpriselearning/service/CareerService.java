package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.CareerGoalRequest;
import com.enterpriselearning.dto.request.CareerPathRequest;
import com.enterpriselearning.dto.response.*;

import java.util.List;

public interface CareerService {
    CareerGoalResponse createCareerGoal(CareerGoalRequest request, String currentUserEmail);
    List<CareerGoalResponse> getGoalsByEmployee(Long userId);
    List<CareerGoalResponse> getMyGoals(String currentUserEmail);
    CareerGoalResponse updateCareerGoal(Long id, CareerGoalRequest request);
    void deleteCareerGoal(Long id);

    CareerPathResponse createCareerPath(CareerPathRequest request);
    List<CareerPathResponse> getAllCareerPaths();
    CareerPathResponse getCareerPathById(Long id);

    SkillRecommendationResponse getRecommendationsForEmployee(Long userId, String targetRole);
    SkillRecommendationResponse getMyRecommendations(String currentUserEmail, String targetRole);

    // Career Progress & Readiness Percentage
    CareerProgressResponse calculateCareerProgress(Long userId, Long pathId);
    CareerProgressResponse calculateMyCareerProgress(String currentUserEmail, Long pathId);
    List<CareerProgressResponse> getAllCareerProgressForEmployee(Long userId);
    List<CareerProgressResponse> getMyCareerProgressForAllGoals(String currentUserEmail);
}
