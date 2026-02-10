package com.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSession {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "test_id", length = 36, nullable = false)
    private String testId;

    @Column(name = "student_id", length = 36, nullable = false)
    private String studentId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(length = 50)
    private String status; // scheduled | running | submitted | terminated

    @Column(name = "screen_recording_path")
    private String screenRecordingPath;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "browser_info")
    private String browserInfo;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
