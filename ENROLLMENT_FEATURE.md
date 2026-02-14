# Dynamic Student-Course Enrollment Feature

## Overview
This feature provides a **Batch Data Processing Engine** that bridges external CSV data with a structured MySQL database to enroll students in courses.

### Core Functionality:

1. **Agnostic CSV Parsing** - Accepts any CSV format; users specify which columns contain name and email
2. **Identity Synchronization** - Implements Upsert logic (Update or Insert); reuses existing student profiles
3. **Atomic Enrollment** - Creates many-to-many relationships between students and courses

---

## Architecture

### Components

#### 1. **CSV Parser** (`CSVParser.java`)
- Parses CSV content with support for quoted fields
- Extracts student data (name, email) based on column indices
- Validates email format
- Handles edge cases (empty lines, malformed data)

#### 2. **Upsert Service** (`UserService.upsertUser()`)
- Checks if student email already exists
- Returns existing user ID if found
- Creates new student profile if not found
- Automatically sets role as "student" and status as "active"

#### 3. **Batch Enrollment Engine** (`BatchEnrollmentService.java`)
- Orchestrates the entire enrollment process
- Validates course exists
- Processes each student record
- Prevents duplicate enrollments
- Generates detailed enrollment report

#### 4. **REST Controllers**
- `EnrollmentController` - Batch CSV and individual enrollment endpoints
- `CourseEnrollmentController` - Traditional CRUD operations

---

## API Endpoints

### **Batch CSV Enrollment** 🚀

#### POST `/api/enrollments/batch-csv`
Process bulk student enrollment from CSV file.

**Request Body:**
```json
{
  "courseId": "550e8400-e29b-41d4-a716-446655440000",
  "nameColumnIndex": 0,
  "emailColumnIndex": 1,
  "csvContent": "John Doe,john@example.com\nJane Smith,jane@example.com\nBob Wilson,bob@example.com"
}
```

**Response (200 OK):**
```json
{
  "courseId": "550e8400-e29b-41d4-a716-446655440000",
  "totalRecords": 3,
  "successCount": 3,
  "skippedCount": 0,
  "errorCount": 0,
  "timestamp": "2026-02-10T10:45:30.123456",
  "results": [
    {
      "studentId": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john@example.com",
      "enrollmentId": "223e4567-e89b-12d3-a456-426614174001",
      "status": "SUCCESS",
      "message": "Successfully enrolled"
    },
    {
      "studentId": "323e4567-e89b-12d3-a456-426614174002",
      "name": "Jane Smith",
      "email": "jane@example.com",
      "enrollmentId": "323e4567-e89b-12d3-a456-426614174003",
      "status": "SUCCESS",
      "message": "Successfully enrolled"
    },
    {
      "studentId": "423e4567-e89b-12d3-a456-426614174004",
      "name": "Bob Wilson",
      "email": "bob@example.com",
      "enrollmentId": "423e4567-e89b-12d3-a456-426614174005",
      "status": "SUCCESS",
      "message": "Successfully enrolled"
    }
  ]
}
```

### **Individual Enrollment Endpoints**

#### POST `/api/enrollments`
Enroll a single student in a course.

**Request Body:**
```json
{
  "courseId": "550e8400-e29b-41d4-a716-446655440000",
  "studentId": "650e8400-e29b-41d4-a716-446655440000",
  "status": "enrolled"
}
```

#### GET `/api/enrollments/course/{courseId}`
Get all students enrolled in a specific course.

**Response (200 OK):**
```json
[
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "courseId": "550e8400-e29b-41d4-a716-446655440000",
    "studentId": "123e4567-e89b-12d3-a456-426614174000",
    "enrolledAt": "2026-02-10T10:45:30",
    "status": "enrolled"
  }
]
```

#### GET `/api/enrollments/student/{studentId}`
Get all courses a student is enrolled in.

#### GET `/api/enrollments/check?courseId=xxx&studentId=yyy`
Check if a student is enrolled in a specific course.

**Response (200 OK):**
```json
true
```

#### PUT `/api/enrollments/{id}`
Update enrollment status (e.g., change from "enrolled" to "completed").

#### DELETE `/api/enrollments/course/{courseId}/student/{studentId}`
Remove a student from a course.

---

## Usage Examples

### Example 1: Batch Enroll from CSV

```bash
curl -X POST http://localhost:8081/api/enrollments/batch-csv \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "550e8400-e29b-41d4-a716-446655440000",
    "nameColumnIndex": 0,
    "emailColumnIndex": 1,
    "csvContent": "Alice Johnson,alice@university.edu\nCharles Brown,charles@university.edu\nDiana Prince,diana@university.edu"
  }'
```

### Example 2: CSV with Additional Columns

If your CSV has more columns (e.g., ID, Name, Email, Phone), specify which ones to use:

**CSV Content:**
```
STU001,John Smith,john.smith@example.com,555-0001
STU002,Sarah Johnson,sarah.johnson@example.com,555-0002
STU003,Michael Davis,michael.davis@example.com,555-0003
```

**Request:**
```bash
curl -X POST http://localhost:8081/api/enrollments/batch-csv \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "550e8400-e29b-41d4-a716-446655440000",
    "nameColumnIndex": 1,
    "emailColumnIndex": 2,
    "csvContent": "STU001,John Smith,john.smith@example.com,555-0001\nSTU002,Sarah Johnson,sarah.johnson@example.com,555-0002\nSTU003,Michael Davis,michael.davis@example.com,555-0003"
  }'
```

### Example 3: Enroll Single Student

```bash
curl -X POST http://localhost:8081/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": "550e8400-e29b-41d4-a716-446655440000",
    "studentId": "650e8400-e29b-41d4-a716-446655440000",
    "status": "enrolled"
  }'
```

### Example 4: Get All Students in Course CS101

```bash
curl http://localhost:8081/api/enrollments/course/550e8400-e29b-41d4-a716-446655440000
```

---

## Key Features

### ✅ Smart Upsert Logic
- **Duplicate Detection**: Before creating an enrollment, the system checks if the student is already enrolled
- **Email-Based Matching**: Uses email as the unique identifier for students
- **Graceful Handling**: Skipped records are clearly reported in the response

### ✅ Comprehensive Error Handling
- Invalid email formats are skipped
- Missing columns are handled gracefully
- Course validation prevents orphaned enrollments
- Detailed error messages for troubleshooting

### ✅ Atomic Transactions
Each enrollment operation is atomic - all or nothing:
- Create user (if needed)
- Create enrollment record
- Transaction rolls back on any failure

### ✅ Detailed Reporting
Batch enrollment returns:
- Total records processed
- Success count
- Skipped count (already enrolled)
- Error count
- Individual result for each student

---

## Database Schema Integration

The feature integrates with:

### `users` Table
- `id` (VARCHAR(36)): Student's unique identifier
- `name` (VARCHAR(255)): Student's full name
- `email` (VARCHAR(255)): Unique email address
- `role` (VARCHAR(255)): Set to "student"
- `status` (VARCHAR(255)): Set to "active"

### `course_enrollments` Table
- `id` (VARCHAR(36)): Enrollment record ID
- `course_id` (VARCHAR(36)): Reference to course
- `student_id` (VARCHAR(36)): Reference to user
- `enrolled_at` (TIMESTAMP): Enrollment timestamp
- `status` (VARCHAR(255)): Enrollment status

---

## Error Scenarios

### Scenario 1: Course Not Found
```json
{
  "courseId": "invalid-course-id",
  "totalRecords": 0,
  "successCount": 0,
  "skippedCount": 0,
  "errorCount": 1,
  "results": [
    {
      "status": "ERROR",
      "message": "Course not found: invalid-course-id"
    }
  ]
}
```

### Scenario 2: Empty CSV
```json
{
  "totalRecords": 0,
  "successCount": 0,
  "skippedCount": 0,
  "errorCount": 1,
  "results": [
    {
      "status": "ERROR",
      "message": "CSV is empty"
    }
  ]
}
```

### Scenario 3: Mixed Results
```json
{
  "courseId": "550e8400-e29b-41d4-a716-446655440000",
  "totalRecords": 3,
  "successCount": 2,
  "skippedCount": 1,
  "errorCount": 0,
  "results": [
    {
      "studentId": "123e4567-e89b-12d3-a456-426614174000",
      "name": "John Doe",
      "email": "john@example.com",
      "enrollmentId": "223e4567-e89b-12d3-a456-426614174001",
      "status": "SUCCESS",
      "message": "Successfully enrolled"
    },
    {
      "studentId": "323e4567-e89b-12d3-a456-426614174002",
      "name": "Jane Smith",
      "email": "jane@example.com",
      "status": "SKIPPED",
      "message": "Student already enrolled in this course"
    },
    {
      "studentId": "423e4567-e89b-12d3-a456-426614174004",
      "name": "Bob Wilson",
      "email": "bob@example.com",
      "enrollmentId": "423e4567-e89b-12d3-a456-426614174005",
      "status": "SUCCESS",
      "message": "Successfully enrolled"
    }
  ]
}
```

---

## Best Practices

1. **Validate CSV Before Upload**: Ensure emails are properly formatted
2. **Use Correct Column Indices**: Verify column numbering (0-based indexing)
3. **Batch Processing**: Use the batch endpoint for large enrollment lists
4. **Error Handling**: Always check the response for skipped/error records
5. **Idempotency**: Re-running the same CSV with duplicate enrollments is safe

---

## Technical Details

- **CSV Parsing**: Handles quoted fields, escaped commas, and various line endings
- **Email Validation**: Basic RFC format validation
- **UUID Generation**: All IDs are UUID v4
- **Timestamps**: ISO 8601 format with timezone
- **Error Handling**: Try-catch wrapping prevents partial failures

