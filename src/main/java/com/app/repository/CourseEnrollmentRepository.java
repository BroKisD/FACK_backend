package com.app.repository;

import com.app.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, String> {
    List<CourseEnrollment> findByCourseId(String courseId);
    List<CourseEnrollment> findByStudentId(String studentId);
    Optional<CourseEnrollment> findByCourseIdAndStudentId(String courseId, String studentId);
    List<CourseEnrollment> findByStatus(String status);
}
