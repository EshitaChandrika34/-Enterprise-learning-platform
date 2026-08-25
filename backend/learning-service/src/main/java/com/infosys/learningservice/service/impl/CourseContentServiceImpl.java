package com.infosys.learningservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.CourseContentDTO;
import com.infosys.learningservice.entity.CourseContent;
import com.infosys.learningservice.exception.ResourceNotFoundException;
import com.infosys.learningservice.repository.CourseContentRepository;
import com.infosys.learningservice.service.CourseContentService;

@Service
public class CourseContentServiceImpl implements CourseContentService {

    private final CourseContentRepository courseContentRepository;

    public CourseContentServiceImpl(
            CourseContentRepository courseContentRepository) {

        this.courseContentRepository = courseContentRepository;
    }

    @Override
    public CourseContentDTO addCourseContent(
            CourseContentDTO dto) {

        CourseContent content = new CourseContent();

        content.setCourseId(dto.getCourseId());
        content.setTitle(dto.getTitle());
        content.setContentType(dto.getContentType());
        content.setContentUrl(dto.getContentUrl());
        content.setContentOrder(dto.getContentOrder());

        CourseContent savedContent =
                courseContentRepository.save(content);

        return convertToDTO(savedContent);
    }

    @Override
    public List<CourseContentDTO> getAllCourseContents() {

        return courseContentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseContentDTO getCourseContentById(Long id) {

        CourseContent content =
                courseContentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course content not found with id: "
                                                + id));

        return convertToDTO(content);
    }

    @Override
    public List<CourseContentDTO> getCourseContentsByCourseId(
            Long courseId) {

        return courseContentRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseContentDTO updateCourseContent(
            Long id,
            CourseContentDTO dto) {

        CourseContent content =
                courseContentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course content not found with id: "
                                                + id));

        content.setCourseId(dto.getCourseId());
        content.setTitle(dto.getTitle());
        content.setContentType(dto.getContentType());
        content.setContentUrl(dto.getContentUrl());
        content.setContentOrder(dto.getContentOrder());

        CourseContent updatedContent =
                courseContentRepository.save(content);

        return convertToDTO(updatedContent);
    }

    @Override
    public void deleteCourseContent(Long id) {

        CourseContent content =
                courseContentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course content not found with id: "
                                                + id));

        courseContentRepository.delete(content);
    }

    private CourseContentDTO convertToDTO(
            CourseContent content) {

        CourseContentDTO dto = new CourseContentDTO();

        dto.setContentId(content.getContentId());
        dto.setCourseId(content.getCourseId());
        dto.setTitle(content.getTitle());
        dto.setContentType(content.getContentType());
        dto.setContentUrl(content.getContentUrl());
        dto.setContentOrder(content.getContentOrder());

        return dto;
    }
}