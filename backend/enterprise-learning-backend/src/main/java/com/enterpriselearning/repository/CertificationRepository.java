package com.enterpriselearning.repository;

import com.enterpriselearning.entity.Certification;
import com.enterpriselearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByUser(User user);
    List<Certification> findByUserId(Long userId);
    List<Certification> findByIssuingOrganization(String organization);

    @Query("SELECT c.issuingOrganization, COUNT(c) FROM Certification c GROUP BY c.issuingOrganization ORDER BY COUNT(c) DESC")
    List<Object[]> countCertificationsByOrganization();
}
