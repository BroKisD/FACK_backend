package com.app.controller;

import com.app.dto.BatchEnrollmentResultDTO;
import com.app.dto.CSVUploadRequest;
import com.app.dto.CourseEnrollmentDTO;
import com.app.service.BatchEnrollmentService;
import com.app.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final CourseEnrollmentService enrollmentService;
    private final BatchEnrollmentService batchEnrollmentService;

    // ===== BATCH CSV ENROLLMENT ENDPOINTS =====

    /**
     * Process bulk student enrollment from CSV
     * POST /api/enrollments/batch-csv
     * 
     * Request body:
     * {
     *   "courseId": "course-uuid",
     *   "nameColumnIndex": 0,
     *   "emailColumnIndex": 1,
     *   "csvContent": "John Doe,john@example.com\nJane Smith,jane@example.com"
     * }
     */
    @PostMapping("/batch-csv")
    public ResponseEntity<BatchEnrollmentResultDTO> enrollStudentsFromCSV(@RequestBody CSVUploadRequest request) {
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        return ResponseEntity.ok(result);
    }

    // ===== INDIVIDUAL ENROLLMENT ENDPOINTS =====

    /**
     * Enroll a single student in a course
     * POST /api/enrollments
     */
    @PostMapping
    public ResponseEntity<CourseEnrollmentDTO> enrollStudent(@RequestBody CourseEnrollmentDTO enrollmentDTO) {
        CourseEnrollmentDTO created = enrollmentService.enrollStudent(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get all enrollments
     * GET /api/enrollments
     */
    @GetMapping
    public ResponseEntity<List<CourseEnrollmentDTO>> getAllEnrollments() {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get enrollment by ID
     * GET /api/enrollments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseEnrollmentDTO> getEnrollmentById(@PathVariable String id) {
        return enrollmentService.getEnrollmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all students enrolled in a course
     * GET /api/enrollments/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> getEnrollmentsByCourse(@PathVariable String courseId) {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Get all courses a student is enrolled in
     * GET /api/enrollments/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseEnrollmentDTO>> getEnrollmentsByStudent(@PathVariable String studentId) {
        List<CourseEnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Check if student is enrolled in a course
     * GET /api/enrollments/check?courseId=xxx&studentId=yyy
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isStudentEnrolled(
            @RequestParam String courseId,
            @RequestParam String studentId) {
        boolean enrolled = enrollmentService.isStudentEnrolled(courseId, studentId);
        return ResponseEntity.ok(enrolled);
    }

    /**
     * Update enrollment status
     * PUT /api/enrollments/{id}
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
     * Remove student from course
     * DELETE /api/enrollments/course/{courseId}/student/{studentId}
     */
    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Void> removeStudentFromCourse(
            @PathVariable String courseId,
            @PathVariable String studentId) {
        if (enrollmentService.removeStudentFromCourse(courseId, studentId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete enrollment by ID
     * DELETE /api/enrollments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable String id) {
        if (enrollmentService.deleteEnrollment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
