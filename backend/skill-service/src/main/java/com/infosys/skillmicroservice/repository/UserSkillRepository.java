package com.infosys.skillmicroservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.skillmicroservice.entity.UserSkill;

@Repository
public interface UserSkillRepository
        extends JpaRepository<UserSkill, UUID> {

    List<UserSkill> findByUserId(UUID userId);
}