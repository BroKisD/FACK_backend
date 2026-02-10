package com.app.controller;

import com.app.dto.BatchEnrollmentResultDTO;
import com.app.dto.CSVUploadRequest;
import com.app.dto.EnrollmentResultDTO;
import com.app.service.BatchEnrollmentService;
import com.app.service.CourseEnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@DisplayName("Enrollment Controller Tests")
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseEnrollmentService enrollmentService;

    @MockBean
    private BatchEnrollmentService batchEnrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully process batch CSV enrollment")
    void testProcessBatchCSVEnrollment() throws Exception {
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId("course-123");
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com\nJane Smith,jane@example.com");

        List<EnrollmentResultDTO> results = new ArrayList<>();
        
        EnrollmentResultDTO result1 = new EnrollmentResultDTO();
        result1.setStudentId("user-1");
        result1.setName("John Doe");
        result1.setEmail("john@example.com");
        result1.setEnrollmentId("enrollment-1");
        result1.setStatus("SUCCESS");
        result1.setMessage("Successfully enrolled");
        results.add(result1);
        
        EnrollmentResultDTO result2 = new EnrollmentResultDTO();
        result2.setStudentId("user-2");
        result2.setName("Jane Smith");
        result2.setEmail("jane@example.com");
        result2.setEnrollmentId("enrollment-2");
        result2.setStatus("SUCCESS");
        result2.setMessage("Successfully enrolled");
        results.add(result2);

        BatchEnrollmentResultDTO batchResult = new BatchEnrollmentResultDTO();
        batchResult.setCourseId("course-123");
        batchResult.setTotalRecords(2);
        batchResult.setSuccessCount(2);
        batchResult.setSkippedCount(0);
        batchResult.setErrorCount(0);
        batchResult.setResults(results);
        batchResult.setTimestamp("2026-02-10T10:45:30");

        when(batchEnrollmentService.processCSVEnrollment(any())).thenReturn(batchResult);

        mockMvc.perform(post("/api/enrollments/batch-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("course-123"))
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.skippedCount").value(0))
                .andExpect(jsonPath("$.errorCount").value(0))
                .andExpect(jsonPath("$.results", hasSize(2)));
    }

    @Test
    @DisplayName("Should return error when course not found")
    void testProcessBatchCSVWithInvalidCourse() throws Exception {
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId("invalid-course");
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com");

        EnrollmentResultDTO errorResult = new EnrollmentResultDTO();
        errorResult.setStatus("ERROR");
        errorResult.setMessage("Course not found: invalid-course");

        BatchEnrollmentResultDTO batchResult = new BatchEnrollmentResultDTO();
        batchResult.setCourseId("invalid-course");
        batchResult.setTotalRecords(0);
        batchResult.setSuccessCount(0);
        batchResult.setSkippedCount(0);
        batchResult.setErrorCount(1);
        batchResult.setResults(List.of(errorResult));

        when(batchEnrollmentService.processCSVEnrollment(any())).thenReturn(batchResult);

        mockMvc.perform(post("/api/enrollments/batch-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCount").value(1))
                .andExpect(jsonPath("$.results[0].status").value("ERROR"));
    }

    @Test
    @DisplayName("Should skip already enrolled students")
    void testProcessBatchCSVWithDuplicates() throws Exception {
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId("course-123");
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com");

        EnrollmentResultDTO skipResult = new EnrollmentResultDTO();
        skipResult.setStudentId("user-1");
        skipResult.setName("John Doe");
        skipResult.setEmail("john@example.com");
        skipResult.setStatus("SKIPPED");
        skipResult.setMessage("Student already enrolled in this course");

        BatchEnrollmentResultDTO batchResult = new BatchEnrollmentResultDTO();
        batchResult.setCourseId("course-123");
        batchResult.setTotalRecords(1);
        batchResult.setSuccessCount(0);
        batchResult.setSkippedCount(1);
        batchResult.setErrorCount(0);
        batchResult.setResults(List.of(skipResult));

        when(batchEnrollmentService.processCSVEnrollment(any())).thenReturn(batchResult);

        mockMvc.perform(post("/api/enrollments/batch-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skippedCount").value(1))
                .andExpect(jsonPath("$.results[0].status").value("SKIPPED"));
    }

    @Test
    @DisplayName("Should handle empty CSV")
    void testProcessEmptyCSV() throws Exception {
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId("course-123");
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("");

        EnrollmentResultDTO errorResult = new EnrollmentResultDTO();
        errorResult.setStatus("ERROR");
        errorResult.setMessage("CSV is empty");

        BatchEnrollmentResultDTO batchResult = new BatchEnrollmentResultDTO();
        batchResult.setTotalRecords(0);
        batchResult.setErrorCount(1);
        batchResult.setResults(List.of(errorResult));

        when(batchEnrollmentService.processCSVEnrollment(any())).thenReturn(batchResult);

        mockMvc.perform(post("/api/enrollments/batch-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCount").value(1));
    }

    @Test
    @DisplayName("Should handle mixed success and skip results")
    void testProcessMixedResults() throws Exception {
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId("course-123");
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com\nJane Smith,jane@example.com");

        List<EnrollmentResultDTO> results = new ArrayList<>();
        
        EnrollmentResultDTO success = new EnrollmentResultDTO();
        success.setStudentId("user-1");
        success.setStatus("SUCCESS");
        results.add(success);
        
        EnrollmentResultDTO skipped = new EnrollmentResultDTO();
        skipped.setStudentId("user-2");
        skipped.setStatus("SKIPPED");
        results.add(skipped);

        BatchEnrollmentResultDTO batchResult = new BatchEnrollmentResultDTO();
        batchResult.setTotalRecords(2);
        batchResult.setSuccessCount(1);
        batchResult.setSkippedCount(1);
        batchResult.setErrorCount(0);
        batchResult.setResults(results);

        when(batchEnrollmentService.processCSVEnrollment(any())).thenReturn(batchResult);

        mockMvc.perform(post("/api/enrollments/batch-csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRecords").value(2))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.skippedCount").value(1))
                .andExpect(jsonPath("$.results", hasSize(2)));
    }
}
