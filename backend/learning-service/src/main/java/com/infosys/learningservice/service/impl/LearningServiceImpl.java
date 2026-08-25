package com.infosys.learningservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.LearningCourseDTO;
import com.infosys.learningservice.entity.LearningCourse;
import com.infosys.learningservice.repository.LearningRepository;
import com.infosys.learningservice.service.LearningService;

@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    private LearningRepository repository;

    @Override
    public LearningCourseDTO addCourse(LearningCourseDTO dto) {

        LearningCourse course = new LearningCourse();

        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setTrainerName(dto.getTrainerName());
        course.setDuration(dto.getDuration());
        course.setLevel(dto.getLevel());

        LearningCourse saved = repository.save(course);

        dto.setCourseId(saved.getCourseId());

        return dto;
    }

    @Override
    public List<LearningCourseDTO> getAllCourses() {

        return repository.findAll().stream().map(course -> {

            LearningCourseDTO dto = new LearningCourseDTO();

            dto.setCourseId(course.getCourseId());
            dto.setCourseName(course.getCourseName());
            dto.setDescription(course.getDescription());
            dto.setTrainerName(course.getTrainerName());
            dto.setDuration(course.getDuration());
            dto.setLevel(course.getLevel());

            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public LearningCourseDTO getCourseById(Long id) {

        LearningCourse course = repository.findById(id).orElseThrow();

        LearningCourseDTO dto = new LearningCourseDTO();

        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setTrainerName(course.getTrainerName());
        dto.setDuration(course.getDuration());
        dto.setLevel(course.getLevel());

        return dto;
    }

    @Override
    public LearningCourseDTO updateCourse(Long id, LearningCourseDTO dto) {

        LearningCourse course = repository.findById(id).orElseThrow();

        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setTrainerName(dto.getTrainerName());
        course.setDuration(dto.getDuration());
        course.setLevel(dto.getLevel());

        repository.save(course);

        dto.setCourseId(id);

        return dto;
    }

    @Override
    public void deleteCourse(Long id) {

        repository.deleteById(id);

    }
}