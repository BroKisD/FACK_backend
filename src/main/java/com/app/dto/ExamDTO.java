package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private String id;
    private String courseId;
    private String title;
    private String description;
    private String professorId;
    private String examFileUrl;
    private Integer durationMinutes;
    private LocalDateTime startAvailableAt;
    private LocalDateTime endAvailableAt;
    private Boolean recordingRequired;
    private LocalDateTime createdAt;
}
