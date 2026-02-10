package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVUploadRequest {
    private String courseId;
    private int nameColumnIndex;      // 0-based column index for student name
    private int emailColumnIndex;     // 0-based column index for student email
    private String csvContent;        // Raw CSV content as string
}
