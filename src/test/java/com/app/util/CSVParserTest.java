package com.app.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CSV Parser Tests")
class CSVParserTest {

    @Test
    @DisplayName("Should parse simple CSV without quotes")
    void testParseSimpleCSV() {
        String csv = "John Doe,john@example.com\nJane Smith,jane@example.com";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(2, records.size());
        assertEquals("John Doe", records.get(0)[0]);
        assertEquals("john@example.com", records.get(0)[1]);
        assertEquals("Jane Smith", records.get(1)[0]);
        assertEquals("jane@example.com", records.get(1)[1]);
    }

    @Test
    @DisplayName("Should parse CSV with quoted fields")
    void testParseCSVWithQuotes() {
        String csv = "\"Doe, John\",john@example.com\n\"Smith, Jane\",jane@example.com";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(2, records.size());
        assertEquals("Doe, John", records.get(0)[0]);
        assertEquals("john@example.com", records.get(0)[1]);
    }

    @Test
    @DisplayName("Should skip empty lines")
    void testParseCSVWithEmptyLines() {
        String csv = "John Doe,john@example.com\n\n\nJane Smith,jane@example.com\n";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(2, records.size());
    }

    @Test
    @DisplayName("Should trim whitespace")
    void testParseCSVWithWhitespace() {
        String csv = "  John Doe  ,  john@example.com  \n Jane Smith , jane@example.com ";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals("John Doe", records.get(0)[0]);
        assertEquals("john@example.com", records.get(0)[1]);
        assertEquals("Jane Smith", records.get(1)[0]);
        assertEquals("jane@example.com", records.get(1)[1]);
    }

    @Test
    @DisplayName("Should handle CSV with multiple columns")
    void testParseCSVWithMultipleColumns() {
        String csv = "STU001,John Doe,john@example.com,555-0001\nSTU002,Jane Smith,jane@example.com,555-0002";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(2, records.size());
        assertEquals(4, records.get(0).length);
        assertEquals("STU001", records.get(0)[0]);
        assertEquals("John Doe", records.get(0)[1]);
        assertEquals("john@example.com", records.get(0)[2]);
        assertEquals("555-0001", records.get(0)[3]);
    }

    @Test
    @DisplayName("Should extract student data with correct column indices")
    void testExtractStudentData() {
        List<String[]> records = List.of(
            new String[]{"John Doe", "john@example.com"},
            new String[]{"Jane Smith", "jane@example.com"},
            new String[]{"Bob Wilson", "bob@example.com"}
        );
        
        List<CSVParser.StudentData> students = CSVParser.extractStudentData(records, 0, 1);
        
        assertEquals(3, students.size());
        assertEquals("John Doe", students.get(0).name);
        assertEquals("john@example.com", students.get(0).email);
    }

    @Test
    @DisplayName("Should extract student data with different column indices")
    void testExtractStudentDataWithDifferentIndices() {
        List<String[]> records = List.of(
            new String[]{"STU001", "John Doe", "john@example.com", "555-0001"},
            new String[]{"STU002", "Jane Smith", "jane@example.com", "555-0002"}
        );
        
        List<CSVParser.StudentData> students = CSVParser.extractStudentData(records, 1, 2);
        
        assertEquals(2, students.size());
        assertEquals("John Doe", students.get(0).name);
        assertEquals("john@example.com", students.get(0).email);
    }

    @Test
    @DisplayName("Should validate email format")
    void testExtractStudentDataWithInvalidEmail() {
        List<String[]> records = List.of(
            new String[]{"John Doe", "invalid-email"},
            new String[]{"Jane Smith", "jane@example.com"}
        );
        
        List<CSVParser.StudentData> students = CSVParser.extractStudentData(records, 0, 1);
        
        // Only valid email should be extracted
        assertEquals(1, students.size());
        assertEquals("Jane Smith", students.get(0).name);
    }

    @Test
    @DisplayName("Should skip records with empty fields")
    void testExtractStudentDataWithEmptyFields() {
        List<String[]> records = List.of(
            new String[]{"John Doe", "john@example.com"},
            new String[]{"", "jane@example.com"},
            new String[]{"Bob Wilson", ""},
            new String[]{"Carol Davis", "carol@example.com"}
        );
        
        List<CSVParser.StudentData> students = CSVParser.extractStudentData(records, 0, 1);
        
        assertEquals(2, students.size());
        assertEquals("John Doe", students.get(0).name);
        assertEquals("Carol Davis", students.get(1).name);
    }

    @Test
    @DisplayName("Should handle empty CSV")
    void testParseEmptyCSV() {
        String csv = "";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(0, records.size());
    }

    @Test
    @DisplayName("Should handle CSV with only whitespace")
    void testParseCSVWithOnlyWhitespace() {
        String csv = "   \n  \n   ";
        
        List<String[]> records = CSVParser.parseCSV(csv);
        
        assertEquals(0, records.size());
    }
}
