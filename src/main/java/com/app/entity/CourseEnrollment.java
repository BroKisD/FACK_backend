package com.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "student_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollment {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "course_id", length = 36, nullable = false)
    private String courseId;

    @Column(name = "student_id", length = 36, nullable = false)
    private String studentId;

    @Column(name = "enrolled_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime enrolledAt;

    @Column(length = 50)
    private String status; // enrolled | dropped | completed

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
}
