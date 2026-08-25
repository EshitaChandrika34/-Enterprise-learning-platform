package com.infosys.skillmicroservice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.infosys.skillmicroservice.entity.Skill;
import com.infosys.skillmicroservice.entity.UserSkill;
import com.infosys.skillmicroservice.repository.SkillRepository;
import com.infosys.skillmicroservice.repository.UserSkillRepository;
import com.infosys.skillmicroservice.service.UserSkillService;

@Service
public class UserSkillServiceImpl implements UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final SkillRepository skillRepository;

    public UserSkillServiceImpl(
            UserSkillRepository userSkillRepository,
            SkillRepository skillRepository) {

        this.userSkillRepository = userSkillRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public UserSkill addUserSkill(UserSkill userSkill) {

        UUID skillId = userSkill.getSkill().getSkillId();

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Skill not found with ID: " + skillId));

        userSkill.setSkill(skill);

        return userSkillRepository.save(userSkill);
    }

    @Override
    public List<UserSkill> getAllUserSkills() {
        return userSkillRepository.findAll();
    }

    @Override
    public UserSkill getUserSkillById(UUID userSkillId) {

        return userSkillRepository.findById(userSkillId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "UserSkill not found with ID: "
                                        + userSkillId));
    }

    @Override
    public List<UserSkill> getSkillsByUserId(UUID userId) {
        return userSkillRepository.findByUserId(userId);
    }

    @Override
    public UserSkill updateUserSkill(
            UUID userSkillId,
            UserSkill newUserSkill) {

        UserSkill existingUserSkill =
                getUserSkillById(userSkillId);

        if (newUserSkill.getSkill() != null
                && newUserSkill.getSkill().getSkillId() != null) {

            UUID skillId =
                    newUserSkill.getSkill().getSkillId();

            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Skill not found with ID: "
                                            + skillId));

            existingUserSkill.setSkill(skill);
        }

        existingUserSkill.setUserId(
                newUserSkill.getUserId());

        existingUserSkill.setSkillLevel(
                newUserSkill.getSkillLevel());

        existingUserSkill.setYearsOfExperience(
                newUserSkill.getYearsOfExperience());

        existingUserSkill.setCompetencyLevel(
                newUserSkill.getCompetencyLevel());

        existingUserSkill.setVerified(
                newUserSkill.getVerified());

        return userSkillRepository.save(existingUserSkill);
    }

    @Override
    public void deleteUserSkill(UUID userSkillId) {

        UserSkill userSkill =
                getUserSkillById(userSkillId);

        userSkillRepository.delete(userSkill);
    }
}