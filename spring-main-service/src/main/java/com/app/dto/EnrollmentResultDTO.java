package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResultDTO {
    private String studentId;
    private String name;
    private String email;
    private String enrollmentId;
    private String status;  // SUCCESS | SKIPPED | ERROR
    private String message;
}
