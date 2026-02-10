package com.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "course_id", length = 36, nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "professor_id", length = 36, nullable = false)
    private String professorId;

    @Column(name = "exam_file_url")
    private String examFileUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "start_available_at")
    private LocalDateTime startAvailableAt;

    @Column(name = "end_available_at")
    private LocalDateTime endAvailableAt;

    @Column(name = "recording_required", columnDefinition = "TINYINT DEFAULT 1")
    private Boolean recordingRequired;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordingRequired == null) {
            recordingRequired = true;
        }
    }
}
