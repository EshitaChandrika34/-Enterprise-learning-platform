package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.CourseRequest;
import com.enterpriselearning.dto.request.EnrollmentRequest;
import com.enterpriselearning.dto.request.ProgressUpdateRequest;
import com.enterpriselearning.dto.response.CourseResponse;
import com.enterpriselearning.dto.response.EnrollmentResponse;
import com.enterpriselearning.entity.Course;
import com.enterpriselearning.entity.Enrollment;
import com.enterpriselearning.entity.EnrollmentStatus;
import com.enterpriselearning.entity.User;
import com.enterpriselearning.exception.BadRequestException;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.CourseRepository;
import com.enterpriselearning.repository.EnrollmentRepository;
import com.enterpriselearning.repository.UserRepository;
import com.enterpriselearning.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .level(request.getLevel())
                .durationHours(request.getDurationHours())
                .instructor(request.getInstructor())
                .build();

        Course saved = courseRepository.save(course);
        return mapToCourseResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses(String category, String level, String search) {
        List<Course> courses;

        if (search != null && !search.trim().isEmpty()) {
            courses = courseRepository.findByTitleContainingIgnoreCase(search.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            courses = courseRepository.findByCategory(category.trim());
        } else if (level != null && !level.trim().isEmpty()) {
            courses = courseRepository.findByLevel(level.trim());
        } else {
            courses = courseRepository.findAll();
        }

        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToCourseResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDurationHours(request.getDurationHours());
        course.setInstructor(request.getInstructor());

        Course updated = courseRepository.save(course);
        return mapToCourseResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        enrollmentRepository.deleteAll(enrollments);
        courseRepository.delete(course);
    }

    @Override
    @Transactional
    public EnrollmentResponse enrollInCourse(EnrollmentRequest request, String currentUserEmail) {
        User user;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        } else {
            user = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new BadRequestException("User is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .progressPercentage(0)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(saved);
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Long enrollmentId, ProgressUpdateRequest request, String currentUserEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        int progress = request.getProgressPercentage();
        enrollment.setProgressPercentage(progress);

        if (progress == 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            if (enrollment.getCompletedDate() == null) {
                enrollment.setCompletedDate(LocalDateTime.now());
            }
        } else if (progress > 0) {
            enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
            enrollment.setCompletedDate(null);
        } else {
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            enrollment.setCompletedDate(null);
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByEmployee(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + userId);
        }
        return enrollmentRepository.findByUserId(userId).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));
        return enrollmentRepository.findByUserId(user.getId()).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        return mapToEnrollmentResponse(enrollment);
    }

    private CourseResponse mapToCourseResponse(Course course) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .level(course.getLevel())
                .durationHours(course.getDurationHours())
                .instructor(course.getInstructor())
                .enrollmentCount(enrollments.size())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .employeeName(e.getUser().getFirstName() + " " + e.getUser().getLastName())
                .employeeId(e.getUser().getEmployeeId())
                .courseId(e.getCourse().getId())
                .courseTitle(e.getCourse().getTitle())
                .courseCategory(e.getCourse().getCategory())
                .courseLevel(e.getCourse().getLevel())
                .durationHours(e.getCourse().getDurationHours())
                .status(e.getStatus())
                .progressPercentage(e.getProgressPercentage())
                .enrolledDate(e.getEnrolledDate())
                .completedDate(e.getCompletedDate())
                .lastAccessedDate(e.getLastAccessedDate())
                .build();
    }
}
