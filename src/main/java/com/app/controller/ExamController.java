package com.app.controller;

import com.app.dto.ExamDTO;
import com.app.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /**
     * Create exam
     * POST /api/exams
     */
    @PostMapping
    public ResponseEntity<ExamDTO> createExam(@RequestBody ExamDTO examDTO) {
        ExamDTO createdExam = examService.createExam(examDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExam);
    }

    /**
     * Get all exams
     * GET /api/exams
     */
    @GetMapping
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        List<ExamDTO> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
    }

    /**
     * Get exam by ID
     * GET /api/exams/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> getExamById(@PathVariable String id) {
        return examService.getExamById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get exams by course
     * GET /api/exams/by-course/{courseId}
     */
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<ExamDTO>> getExamsByCourse(@PathVariable String courseId) {
        List<ExamDTO> exams = examService.getExamsByCourse(courseId);
        return ResponseEntity.ok(exams);
    }

    // @GetMapping("/by-course-code/{courseCode}")
    // public ResponseEntity<List<ExamDTO>> getExamsByCourseCode(@PathVariable String courseCode) {
    //     List<ExamDTO> exams = examService.getExamsByCourseCode(courseCode);
    //     return ResponseEntity.ok(exams);
    // }

    /**
     * Get exams by professor
     * GET /api/exams/by-professor/{professorId}
     */
    @GetMapping("/by-professor/{professorId}")
    public ResponseEntity<List<ExamDTO>> getExamsByProfessor(@PathVariable String professorId) {
        List<ExamDTO> exams = examService.getExamsByProfessor(professorId);
        return ResponseEntity.ok(exams);
    }

    /**
     * Update exam
     * PUT /api/exams/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExamDTO> updateExam(
            @PathVariable String id,
            @RequestBody ExamDTO examDTO) {
        return examService.updateExam(id, examDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete exam
     * DELETE /api/exams/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        if (examService.deleteExam(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
