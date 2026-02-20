package com.app.service;

import com.app.dto.ExamDTO;
import com.app.entity.Exam;
import com.app.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    // CREATE
    public ExamDTO createExam(ExamDTO examDTO) {
        if (examDTO.getId() == null || examDTO.getId().isEmpty()) {
            examDTO.setId(UUID.randomUUID().toString());
        }
        Exam exam = convertToEntity(examDTO);
        Exam savedExam = examRepository.save(exam);
        return convertToDTO(savedExam);
    }

    // READ - Get by ID
    public Optional<ExamDTO> getExamById(String id) {
        return examRepository.findById(id)
                .map(this::convertToDTO);
    }

    // READ - Get exams by course code
    // public List<ExamDTO> getExamsByCourseCode(String courseCode) {
    //     return examRepository.findByCourseCode(courseCode)
    //             .stream()
    //             .map(this::convertToDTO)
    //             .collect(Collectors.toList());
    // }

    // READ - Get all exams
    public List<ExamDTO> getAllExams() {
        return examRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get exams by course
    public List<ExamDTO> getExamsByCourse(String courseId) {
        return examRepository.findByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ - Get exams by professor
    public List<ExamDTO> getExamsByProfessor(String professorId) {
        return examRepository.findByProfessorId(professorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATE
    public Optional<ExamDTO> updateExam(String id, ExamDTO examDTO) {
        return examRepository.findById(id)
                .map(exam -> {
                    if (examDTO.getCourseId() != null) exam.setCourseId(examDTO.getCourseId());
                    if (examDTO.getTitle() != null) exam.setTitle(examDTO.getTitle());
                    if (examDTO.getDescription() != null) exam.setDescription(examDTO.getDescription());
                    if (examDTO.getProfessorId() != null) exam.setProfessorId(examDTO.getProfessorId());
                    if (examDTO.getExamFileUrl() != null) exam.setExamFileUrl(examDTO.getExamFileUrl());
                    if (examDTO.getDurationMinutes() != null) exam.setDurationMinutes(examDTO.getDurationMinutes());
                    if (examDTO.getStartAvailableAt() != null) exam.setStartAvailableAt(examDTO.getStartAvailableAt());
                    if (examDTO.getEndAvailableAt() != null) exam.setEndAvailableAt(examDTO.getEndAvailableAt());
                    if (examDTO.getRecordingRequired() != null) exam.setRecordingRequired(examDTO.getRecordingRequired());
                    Exam updatedExam = examRepository.save(exam);
                    return convertToDTO(updatedExam);
                });
    }

    // DELETE
    public boolean deleteExam(String id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper methods
    private ExamDTO convertToDTO(Exam exam) {
        return new ExamDTO(
                exam.getId(),
                exam.getCourseId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getProfessorId(),
                exam.getExamFileUrl(),
                exam.getDurationMinutes(),
                exam.getStartAvailableAt(),
                exam.getEndAvailableAt(),
                exam.getRecordingRequired(),
                exam.getCreatedAt()
        );
    }

    private Exam convertToEntity(ExamDTO examDTO) {
        Exam exam = new Exam();
        exam.setId(examDTO.getId());
        exam.setCourseId(examDTO.getCourseId());
        exam.setTitle(examDTO.getTitle());
        exam.setDescription(examDTO.getDescription());
        exam.setProfessorId(examDTO.getProfessorId());
        exam.setExamFileUrl(examDTO.getExamFileUrl());
        exam.setDurationMinutes(examDTO.getDurationMinutes());
        exam.setStartAvailableAt(examDTO.getStartAvailableAt());
        exam.setEndAvailableAt(examDTO.getEndAvailableAt());
        exam.setRecordingRequired(examDTO.getRecordingRequired() != null ? examDTO.getRecordingRequired() : true);
        return exam;
    }
}
