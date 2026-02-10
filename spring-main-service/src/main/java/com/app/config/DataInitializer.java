package com.app.config;

import com.app.dto.UserDTO;
import com.app.dto.CourseDTO;
import com.app.service.UserService;
import com.app.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserService userService;
    private final CourseService courseService;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            try {
                // Check if data already exists
                if (userService.getAllUsers().isEmpty()) {
                    System.out.println("🔄 Initializing sample data...");

                    // Create sample users
                    String professorId1 = UUID.randomUUID().toString();
                    String professorId2 = UUID.randomUUID().toString();
                    String studentId1 = UUID.randomUUID().toString();
                    String studentId2 = UUID.randomUUID().toString();
                    String studentId3 = UUID.randomUUID().toString();

                    // Professor 1
                    UserDTO professor1 = new UserDTO();
                    professor1.setId(professorId1);
                    professor1.setName("Dr. John Smith");
                    professor1.setEmail("john.smith@university.edu");
                    professor1.setPasswordHash("hashed_password_123");
                    professor1.setRole("professor");
                    professor1.setStatus("active");
                    userService.createUser(professor1);

                    // Professor 2
                    UserDTO professor2 = new UserDTO();
                    professor2.setId(professorId2);
                    professor2.setName("Dr. Sarah Johnson");
                    professor2.setEmail("sarah.johnson@university.edu");
                    professor2.setPasswordHash("hashed_password_456");
                    professor2.setRole("professor");
                    professor2.setStatus("active");
                    userService.createUser(professor2);

                    // Student 1
                    UserDTO student1 = new UserDTO();
                    student1.setId(studentId1);
                    student1.setName("Alice Brown");
                    student1.setEmail("alice.brown@student.edu");
                    student1.setPasswordHash("hashed_password_789");
                    student1.setRole("student");
                    student1.setStatus("active");
                    userService.createUser(student1);

                    // Student 2
                    UserDTO student2 = new UserDTO();
                    student2.setId(studentId2);
                    student2.setName("Bob Wilson");
                    student2.setEmail("bob.wilson@student.edu");
                    student2.setPasswordHash("hashed_password_101");
                    student2.setRole("student");
                    student2.setStatus("active");
                    userService.createUser(student2);

                    // Student 3
                    UserDTO student3 = new UserDTO();
                    student3.setId(studentId3);
                    student3.setName("Carol Davis");
                    student3.setEmail("carol.davis@student.edu");
                    student3.setPasswordHash("hashed_password_102");
                    student3.setRole("student");
                    student3.setStatus("active");
                    userService.createUser(student3);

                    // Admin user
                    UserDTO admin = new UserDTO();
                    admin.setId(UUID.randomUUID().toString());
                    admin.setName("Admin User");
                    admin.setEmail("admin@university.edu");
                    admin.setPasswordHash("hashed_password_admin");
                    admin.setRole("admin");
                    admin.setStatus("active");
                    userService.createUser(admin);

                    System.out.println("✅ Created 6 users (2 professors, 3 students, 1 admin)");

                    // Create sample courses
                    CourseDTO course1 = new CourseDTO();
                    course1.setId(UUID.randomUUID().toString());
                    course1.setCode("CS101");
                    course1.setName("Introduction to Computer Science");
                    course1.setDescription("Fundamental concepts of programming and algorithms");
                    course1.setProfessorId(professorId1);
                    course1.setSemester("Fall 2024");
                    course1.setStatus("active");
                    courseService.createCourse(course1);

                    CourseDTO course2 = new CourseDTO();
                    course2.setId(UUID.randomUUID().toString());
                    course2.setCode("CS201");
                    course2.setName("Data Structures");
                    course2.setDescription("Advanced data structures and algorithms");
                    course2.setProfessorId(professorId1);
                    course2.setSemester("Fall 2024");
                    course2.setStatus("active");
                    courseService.createCourse(course2);

                    CourseDTO course3 = new CourseDTO();
                    course3.setId(UUID.randomUUID().toString());
                    course3.setCode("MATH101");
                    course3.setName("Calculus I");
                    course3.setDescription("Differential calculus and applications");
                    course3.setProfessorId(professorId2);
                    course3.setSemester("Fall 2024");
                    course3.setStatus("active");
                    courseService.createCourse(course3);

                    CourseDTO course4 = new CourseDTO();
                    course4.setId(UUID.randomUUID().toString());
                    course4.setCode("PHYS101");
                    course4.setName("Physics I");
                    course4.setDescription("Mechanics and thermodynamics");
                    course4.setProfessorId(professorId2);
                    course4.setSemester("Fall 2024");
                    course4.setStatus("active");
                    courseService.createCourse(course4);

                    System.out.println("✅ Created 4 courses");
                    System.out.println("\n📚 Sample data initialization completed successfully!\n");
                } else {
                    System.out.println("✓ Database already has data, skipping initialization");
                }
            } catch (Exception e) {
                System.out.println("⚠️  Could not initialize sample data: " + e.getMessage());
                // Don't fail the application startup
            }
        };
    }
}
