package com.infosys.skillmicroservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.skillmicroservice.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

}