package com.app.service;

import com.app.dto.CourseEnrollmentDTO;
import com.app.entity.CourseEnrollment;
import com.app.repository.CourseEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepository;

    // CREATE
    public CourseEnrollmentDTO enrollStudent(CourseEnrollmentDTO enrollmentDTO) {
        if (enrollmentDTO.getId() == null || enrollmentDTO.getId().isEmpty()) {
            enrollmentDTO.setId(UUID.randomUUID().toString());
        }
        CourseEnrollment enrollment = convertToEntity(enrollmentDTO);
        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    // READ - Get by ID
    public Optional<CourseEnrollmentDTO> getEnrollmentById(String id) {
        return enrollmentRepository.findById(id)
                .map(this::convertToDTO);
    }

    // READ - Get all enrollments
    public List<CourseEnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get enrollments by course
    public List<CourseEnrollmentDTO> getEnrollmentsByCourse(String courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get enrollments by student
    public List<CourseEnrollmentDTO> getEnrollmentsByStudent(String studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Check if student is enrolled in course
    public boolean isStudentEnrolled(String courseId, String studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId).isPresent();
    }

    // READ - Get enrollment by course and student
    public Optional<CourseEnrollmentDTO> getEnrollmentByCourseAndStudent(String courseId, String studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .map(this::convertToDTO);
    }

    // UPDATE
    public Optional<CourseEnrollmentDTO> updateEnrollment(String id, CourseEnrollmentDTO enrollmentDTO) {
        return enrollmentRepository.findById(id)
                .map(enrollment -> {
                    if (enrollmentDTO.getStatus() != null) {
                        enrollment.setStatus(enrollmentDTO.getStatus());
                    }
                    CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
                    return convertToDTO(updatedEnrollment);
                });
    }

    // DELETE
    public boolean deleteEnrollment(String id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // DELETE - Remove student from course
    public boolean removeStudentFromCourse(String courseId, String studentId) {
        return enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .map(enrollment -> {
                    enrollmentRepository.delete(enrollment);
                    return true;
                })
                .orElse(false);
    }

    // Helper methods
    private CourseEnrollmentDTO convertToDTO(CourseEnrollment enrollment) {
        CourseEnrollmentDTO dto = new CourseEnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setCourseId(enrollment.getCourseId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setStatus(enrollment.getStatus());
        return dto;
    }

    private CourseEnrollment convertToEntity(CourseEnrollmentDTO enrollmentDTO) {
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(enrollmentDTO.getId());
        enrollment.setCourseId(enrollmentDTO.getCourseId());
        enrollment.setStudentId(enrollmentDTO.getStudentId());
        enrollment.setStatus(enrollmentDTO.getStatus() != null ? enrollmentDTO.getStatus() : "enrolled");
        return enrollment;
    }
}
