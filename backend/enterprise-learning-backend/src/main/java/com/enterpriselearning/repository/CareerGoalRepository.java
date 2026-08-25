package com.enterpriselearning.repository;

import com.enterpriselearning.entity.CareerGoal;
import com.enterpriselearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerGoalRepository extends JpaRepository<CareerGoal, Long> {
    List<CareerGoal> findByUser(User user);
    List<CareerGoal> findByUserId(Long userId);
    List<CareerGoal> findByStatus(String status);
}
