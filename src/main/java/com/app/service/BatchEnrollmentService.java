package com.app.service;

import com.app.dto.BatchEnrollmentResultDTO;
import com.app.dto.CSVUploadRequest;
import com.app.dto.CourseEnrollmentDTO;
import com.app.dto.EnrollmentResultDTO;
import com.app.dto.UserDTO;
import com.app.util.CSVParser;
import com.app.util.CSVParser.StudentData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchEnrollmentService {

    private final UserService userService;
    private final CourseEnrollmentService enrollmentService;
    private final CourseService courseService;

    /**
     * Process CSV and enroll students in a course
     * Returns detailed enrollment results
     */
    public BatchEnrollmentResultDTO processCSVEnrollment(CSVUploadRequest request) {
        BatchEnrollmentResultDTO result = new BatchEnrollmentResultDTO();
        result.setCourseId(request.getCourseId());
        result.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        
        // Validate course exists
        if (courseService.getCourseById(request.getCourseId()).isEmpty()) {
            result.setTotalRecords(0);
            result.setSuccessCount(0);
            result.setSkippedCount(0);
            result.setErrorCount(1);
            result.setResults(new ArrayList<>());
            
            EnrollmentResultDTO error = new EnrollmentResultDTO();
            error.setStatus("ERROR");
            error.setMessage("Course not found: " + request.getCourseId());
            result.getResults().add(error);
            
            return result;
        }

        // Parse CSV
        List<String[]> records = CSVParser.parseCSV(request.getCsvContent());
        
        // Validate column indices
        if (records.isEmpty()) {
            result.setTotalRecords(0);
            result.setSuccessCount(0);
            result.setSkippedCount(0);
            result.setErrorCount(1);
            result.setResults(new ArrayList<>());
            
            EnrollmentResultDTO error = new EnrollmentResultDTO();
            error.setStatus("ERROR");
            error.setMessage("CSV is empty");
            result.getResults().add(error);
            
            return result;
        }

        // Extract student data
        List<StudentData> students = CSVParser.extractStudentData(
                records,
                request.getNameColumnIndex(),
                request.getEmailColumnIndex()
        );

        result.setTotalRecords(students.size());
        List<EnrollmentResultDTO> enrollmentResults = new ArrayList<>();
        int successCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        // Process each student
        for (StudentData student : students) {
            try {
                // Upsert user (create if not exists, or get existing)
                UserDTO userDTO = userService.upsertUser(student.name, student.email);

                // Check if already enrolled
                if (enrollmentService.isStudentEnrolled(request.getCourseId(), userDTO.getId())) {
                    skippedCount++;
                    
                    EnrollmentResultDTO skipResult = new EnrollmentResultDTO();
                    skipResult.setStudentId(userDTO.getId());
                    skipResult.setName(student.name);
                    skipResult.setEmail(student.email);
                    skipResult.setStatus("SKIPPED");
                    skipResult.setMessage("Student already enrolled in this course");
                    enrollmentResults.add(skipResult);
                    
                    continue;
                }

                // Create enrollment
                CourseEnrollmentDTO enrollment = new CourseEnrollmentDTO();
                enrollment.setId(UUID.randomUUID().toString());
                enrollment.setCourseId(request.getCourseId());
                enrollment.setStudentId(userDTO.getId());
                enrollment.setStatus("enrolled");
                
                CourseEnrollmentDTO savedEnrollment = enrollmentService.enrollStudent(enrollment);
                
                successCount++;
                
                EnrollmentResultDTO successResult = new EnrollmentResultDTO();
                successResult.setStudentId(userDTO.getId());
                successResult.setName(student.name);
                successResult.setEmail(student.email);
                successResult.setEnrollmentId(savedEnrollment.getId());
                successResult.setStatus("SUCCESS");
                successResult.setMessage("Successfully enrolled");
                enrollmentResults.add(successResult);
                
            } catch (Exception e) {
                errorCount++;
                
                EnrollmentResultDTO errorResult = new EnrollmentResultDTO();
                errorResult.setName(student.name);
                errorResult.setEmail(student.email);
                errorResult.setStatus("ERROR");
                errorResult.setMessage("Error: " + e.getMessage());
                enrollmentResults.add(errorResult);
            }
        }

        result.setSuccessCount(successCount);
        result.setSkippedCount(skippedCount);
        result.setErrorCount(errorCount);
        result.setResults(enrollmentResults);

        return result;
    }
}
