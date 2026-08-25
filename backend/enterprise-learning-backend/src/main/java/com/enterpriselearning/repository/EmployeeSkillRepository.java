package com.enterpriselearning.repository;

import com.enterpriselearning.entity.EmployeeSkill;
import com.enterpriselearning.entity.ProficiencyLevel;
import com.enterpriselearning.entity.Skill;
import com.enterpriselearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {
    List<EmployeeSkill> findByUser(User user);
    List<EmployeeSkill> findByUserId(Long userId);
    List<EmployeeSkill> findBySkill(Skill skill);
    List<EmployeeSkill> findBySkillId(Long skillId);
    Optional<EmployeeSkill> findByUserAndSkill(User user, Skill skill);
    Optional<EmployeeSkill> findByUserIdAndSkillId(Long userId, Long skillId);
    boolean existsByUserIdAndSkillId(Long userId, Long skillId);
    long countBySkillId(Long skillId);
    long countByProficiencyLevel(ProficiencyLevel level);

    @Query("SELECT es.skill.name, COUNT(es) FROM EmployeeSkill es GROUP BY es.skill.name ORDER BY COUNT(es) DESC")
    List<Object[]> countEmployeesBySkill();
}
