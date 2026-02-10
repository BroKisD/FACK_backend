package com.app.util;

import java.util.ArrayList;
import java.util.List;

public class CSVParser {

    /**
     * Parse CSV content into list of string arrays
     * Handles quoted fields and commas within quotes
     */
    public static List<String[]> parseCSV(String csvContent) {
        List<String[]> records = new ArrayList<>();
        String[] lines = csvContent.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] fields = parseCSVLine(line);
            records.add(fields);
        }

        return records;
    }

    /**
     * Parse a single CSV line handling quoted fields
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }

    /**
     * Extract student data from parsed CSV
     */
    public static List<StudentData> extractStudentData(List<String[]> records, 
                                                        int nameColumnIndex, 
                                                        int emailColumnIndex) {
        List<StudentData> students = new ArrayList<>();

        for (String[] record : records) {
            if (record.length <= Math.max(nameColumnIndex, emailColumnIndex)) {
                continue; // Skip records with insufficient columns
            }

            String name = record[nameColumnIndex].trim();
            String email = record[emailColumnIndex].trim();

            if (!name.isEmpty() && !email.isEmpty() && isValidEmail(email)) {
                students.add(new StudentData(name, email));
            }
        }

        return students;
    }

    /**
     * Basic email validation
     */
    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Data class for student information
     */
    public static class StudentData {
        public String name;
        public String email;

        public StudentData(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
