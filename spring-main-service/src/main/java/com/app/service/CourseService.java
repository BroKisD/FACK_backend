package com.app.service;

import com.app.dto.CourseDTO;
import com.app.entity.Course;
import com.app.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // CREATE
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseDTO.getId() == null || courseDTO.getId().isEmpty()) {
            courseDTO.setId(UUID.randomUUID().toString());
        }
        Course course = convertToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    // READ - Get by ID
    public Optional<CourseDTO> getCourseById(String id) {
        return courseRepository.findById(id)
                .map(this::convertToDTO);
    }

    // READ - Get all courses
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get by code
    public Optional<CourseDTO> getCourseByCode(String code) {
        return courseRepository.findByCode(code)
                .map(this::convertToDTO);
    }

    // READ - Get by professor
    public List<CourseDTO> getCoursesByProfessor(String professorId) {
        return courseRepository.findByProfessorId(professorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get by status
    public List<CourseDTO> getCoursesByStatus(String status) {
        return courseRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public Optional<CourseDTO> updateCourse(String id, CourseDTO courseDTO) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (courseDTO.getCode() != null) course.setCode(courseDTO.getCode());
                    if (courseDTO.getName() != null) course.setName(courseDTO.getName());
                    if (courseDTO.getDescription() != null) course.setDescription(courseDTO.getDescription());
                    if (courseDTO.getProfessorId() != null) course.setProfessorId(courseDTO.getProfessorId());
                    if (courseDTO.getSemester() != null) course.setSemester(courseDTO.getSemester());
                    if (courseDTO.getStatus() != null) course.setStatus(courseDTO.getStatus());
                    Course updatedCourse = courseRepository.save(course);
                    return convertToDTO(updatedCourse);
                });
    }

    // DELETE
    public boolean deleteCourse(String id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper methods
    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getProfessorId(),
                course.getSemester(),
                course.getStatus(),
                course.getCreatedAt()
        );
    }

    private Course convertToEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setCode(courseDTO.getCode());
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setProfessorId(courseDTO.getProfessorId());
        course.setSemester(courseDTO.getSemester());
        course.setStatus(courseDTO.getStatus());
        return course;
    }
}
