package com.app.service;

import com.app.dto.BatchEnrollmentResultDTO;
import com.app.dto.CSVUploadRequest;
import com.app.dto.CourseDTO;
import com.app.dto.CourseEnrollmentDTO;
import com.app.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Batch Enrollment Service Tests")
class BatchEnrollmentServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private CourseEnrollmentService enrollmentService;
    @Mock
    private CourseService courseService;

    private BatchEnrollmentService batchEnrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        batchEnrollmentService = new BatchEnrollmentService(userService, enrollmentService, courseService);
    }

    @Test
    @DisplayName("Should process valid CSV and enroll students")
    void testProcessValidCSV() {
        String courseId = "course-123";
        
        CourseDTO course = new CourseDTO();
        course.setId(courseId);
        course.setName("CS101");
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));
        
        UserDTO user1 = new UserDTO();
        user1.setId("user-1");
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        
        UserDTO user2 = new UserDTO();
        user2.setId("user-2");
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        
        when(userService.upsertUser("John Doe", "john@example.com")).thenReturn(user1);
        when(userService.upsertUser("Jane Smith", "jane@example.com")).thenReturn(user2);
        
        when(enrollmentService.isStudentEnrolled(courseId, "user-1")).thenReturn(false);
        when(enrollmentService.isStudentEnrolled(courseId, "user-2")).thenReturn(false);
        
        CourseEnrollmentDTO enrollment1 = new CourseEnrollmentDTO();
        enrollment1.setId("enrollment-1");
        enrollment1.setCourseId(courseId);
        enrollment1.setStudentId("user-1");
        
        CourseEnrollmentDTO enrollment2 = new CourseEnrollmentDTO();
        enrollment2.setId("enrollment-2");
        enrollment2.setCourseId(courseId);
        enrollment2.setStudentId("user-2");
        
        when(enrollmentService.enrollStudent(any())).thenReturn(enrollment1, enrollment2);
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com\nJane Smith,jane@example.com");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getSkippedCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(2, result.getResults().size());
    }

    @Test
    @DisplayName("Should skip already enrolled students")
    void testSkipAlreadyEnrolledStudents() {
        String courseId = "course-123";
        
        CourseDTO course = new CourseDTO();
        course.setId(courseId);
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));
        
        UserDTO user = new UserDTO();
        user.setId("user-1");
        user.setEmail("john@example.com");
        
        when(userService.upsertUser(anyString(), anyString())).thenReturn(user);
        when(enrollmentService.isStudentEnrolled(courseId, "user-1")).thenReturn(true);
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getSkippedCount());
        assertEquals("SKIPPED", result.getResults().get(0).getStatus());
    }

    @Test
    @DisplayName("Should handle course not found error")
    void testHandleCourseNotFound() {
        String courseId = "invalid-course";
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.empty());
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertEquals(0, result.getTotalRecords());
        assertEquals(1, result.getErrorCount());
        assertEquals("ERROR", result.getResults().get(0).getStatus());
        assertTrue(result.getResults().get(0).getMessage().contains("Course not found"));
    }

    @Test
    @DisplayName("Should handle empty CSV")
    void testHandleEmptyCSV() {
        String courseId = "course-123";
        
        CourseDTO course = new CourseDTO();
        course.setId(courseId);
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertEquals(0, result.getTotalRecords());
        assertEquals(1, result.getErrorCount());
        assertEquals("ERROR", result.getResults().get(0).getStatus());
        assertTrue(result.getResults().get(0).getMessage().contains("empty"));
    }

    @Test
    @DisplayName("Should handle mixed success and skip results")
    void testMixedSuccessAndSkipResults() {
        String courseId = "course-123";
        
        CourseDTO course = new CourseDTO();
        course.setId(courseId);
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));
        
        UserDTO user1 = new UserDTO();
        user1.setId("user-1");
        user1.setEmail("john@example.com");
        
        UserDTO user2 = new UserDTO();
        user2.setId("user-2");
        user2.setEmail("jane@example.com");
        
        when(userService.upsertUser(anyString(), anyString())).thenReturn(user1, user2);
        
        when(enrollmentService.isStudentEnrolled(courseId, "user-1")).thenReturn(false);
        when(enrollmentService.isStudentEnrolled(courseId, "user-2")).thenReturn(true);
        
        CourseEnrollmentDTO enrollment = new CourseEnrollmentDTO();
        enrollment.setId("enrollment-1");
        
        when(enrollmentService.enrollStudent(any())).thenReturn(enrollment);
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("John Doe,john@example.com\nJane Smith,jane@example.com");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertEquals(2, result.getTotalRecords());
        assertEquals(1, result.getSuccessCount());
        assertEquals(1, result.getSkippedCount());
        assertEquals(0, result.getErrorCount());
    }

    @Test
    @DisplayName("Should include timestamp in result")
    void testResultIncludesTimestamp() {
        String courseId = "course-123";
        
        CourseDTO course = new CourseDTO();
        course.setId(courseId);
        
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(course));
        
        CSVUploadRequest request = new CSVUploadRequest();
        request.setCourseId(courseId);
        request.setNameColumnIndex(0);
        request.setEmailColumnIndex(1);
        request.setCsvContent("");
        
        BatchEnrollmentResultDTO result = batchEnrollmentService.processCSVEnrollment(request);
        
        assertNotNull(result.getTimestamp());
        assertFalse(result.getTimestamp().isEmpty());
    }
}
