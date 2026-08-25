package com.infosys.learningservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.infosys.learningservice.dto.CourseProgressDTO;
import com.infosys.learningservice.entity.CourseProgress;
import com.infosys.learningservice.repository.CourseProgressRepository;
import com.infosys.learningservice.service.CourseProgressService;

@Service
public class CourseProgressServiceImpl implements CourseProgressService {

    private final CourseProgressRepository courseProgressRepository;

    public CourseProgressServiceImpl(CourseProgressRepository courseProgressRepository) {
        this.courseProgressRepository = courseProgressRepository;
    }

    @Override
    public CourseProgressDTO addProgress(CourseProgressDTO progressDTO) {

        CourseProgress progress = new CourseProgress();

        progress.setEnrollmentId(progressDTO.getEnrollmentId());
        progress.setProgressPercentage(progressDTO.getProgressPercentage());
        progress.setCompletionStatus(progressDTO.getCompletionStatus());
        progress.setLastUpdatedDate(progressDTO.getLastUpdatedDate());

        CourseProgress savedProgress = courseProgressRepository.save(progress);

        return convertToDTO(savedProgress);
    }

    @Override
    public List<CourseProgressDTO> getAllProgress() {

        return courseProgressRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseProgressDTO getProgressById(Long id) {

        CourseProgress progress = courseProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Progress not found"));

        return convertToDTO(progress);
    }

    @Override
    public List<CourseProgressDTO> getProgressByEnrollmentId(Long enrollmentId) {

        return courseProgressRepository.findByEnrollmentId(enrollmentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseProgressDTO updateProgress(Long id, CourseProgressDTO progressDTO) {

        CourseProgress progress = courseProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Progress not found"));

        progress.setEnrollmentId(progressDTO.getEnrollmentId());
        progress.setProgressPercentage(progressDTO.getProgressPercentage());
        progress.setCompletionStatus(progressDTO.getCompletionStatus());
        progress.setLastUpdatedDate(progressDTO.getLastUpdatedDate());

        CourseProgress updatedProgress = courseProgressRepository.save(progress);

        return convertToDTO(updatedProgress);
    }

    @Override
    public void deleteProgress(Long id) {

        CourseProgress progress = courseProgressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course Progress not found"));

        courseProgressRepository.delete(progress);
    }

    private CourseProgressDTO convertToDTO(CourseProgress progress) {

        CourseProgressDTO dto = new CourseProgressDTO();

        dto.setProgressId(progress.getProgressId());
        dto.setEnrollmentId(progress.getEnrollmentId());
        dto.setProgressPercentage(progress.getProgressPercentage());
        dto.setCompletionStatus(progress.getCompletionStatus());
        dto.setLastUpdatedDate(progress.getLastUpdatedDate());

        return dto;
    }
}