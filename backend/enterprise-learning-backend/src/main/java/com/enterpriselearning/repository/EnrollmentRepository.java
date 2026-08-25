package com.enterpriselearning.repository;

import com.enterpriselearning.entity.Enrollment;
import com.enterpriselearning.entity.EnrollmentStatus;
import com.enterpriselearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    List<Enrollment> findByUserId(Long userId);
    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    long countByStatus(EnrollmentStatus status);
    
    @Query("SELECT AVG(e.progressPercentage) FROM Enrollment e")
    Double getAverageProgressPercentage();

    @Query("SELECT e.course.title, COUNT(e) FROM Enrollment e GROUP BY e.course.title ORDER BY COUNT(e) DESC")
    List<Object[]> countEnrollmentsByCourse();
}
