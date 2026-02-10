package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentDTO {
    private String id;
    private String courseId;
    private String studentId;
    private LocalDateTime enrolledAt;
    private String status;
}
