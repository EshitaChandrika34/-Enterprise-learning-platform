package com.infosys.skillmicroservice.service;

import java.util.List;
import java.util.UUID;

import com.infosys.skillmicroservice.entity.UserSkill;

public interface UserSkillService {

    UserSkill addUserSkill(UserSkill userSkill);

    List<UserSkill> getAllUserSkills();

    UserSkill getUserSkillById(UUID userSkillId);

    List<UserSkill> getSkillsByUserId(UUID userId);

    UserSkill updateUserSkill(
            UUID userSkillId,
            UserSkill userSkill);

    void deleteUserSkill(UUID userSkillId);
}