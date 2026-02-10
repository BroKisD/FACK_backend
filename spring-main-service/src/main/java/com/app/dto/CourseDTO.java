package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String id;
    private String code;
    private String name;
    private String description;
    private String professorId;
    private String semester;
    private String status;
    private LocalDateTime createdAt;
}
