package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchEnrollmentResultDTO {
    private String courseId;
    private int totalRecords;
    private int successCount;
    private int skippedCount;
    private int errorCount;
    private List<EnrollmentResultDTO> results;
    private String timestamp;
}
