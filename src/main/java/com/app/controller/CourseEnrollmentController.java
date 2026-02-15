package com.app.controller;

import com.app.dto.CourseEnrollmentDTO;
import com.app.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-enrollments")
@RequiredArgsConstructor
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    /**
     * Create enrollment
     * POST /api/course-enrollments
     */
    @PostMapping
    public ResponseEntity<CourseEnrollmentDTO> createEnrollment(@RequestBody CourseEnrollmentDTO enrollmentDTO) {
        CourseEnrollmentDTO created = enrollmentService.enrollStudent(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all enrollments
     * GET /api/course-enrollments
     */
    @GetMapping
    public ResponseEntity<List<CourseEnrollmentDTO>> getAllEnrollments() {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollment by ID
     * GET /api/course-enrollments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseEnrollmentDTO> getEnrollmentById(@PathVariable String id) {
        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get enrollments by course
     * GET /api/course-enrollments/by-course/{courseId}
     */
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> getEnrollmentsByCourse(@PathVariable String courseId) {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollments by student
     * GET /api/course-enrollments/by-student/{studentId}
     */
    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> getEnrollmentsByStudent(@PathVariable String studentId) {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Update enrollment
     * PUT /api/course-enrollments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseEnrollmentDTO> updateEnrollment(
            @PathVariable String id,
            @RequestBody CourseEnrollmentDTO enrollmentDTO) {
        return enrollmentService.updateEnrollment(id, enrollmentDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete enrollment
     * DELETE /api/course-enrollments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable String id) {
        if (enrollmentService.deleteEnrollment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
