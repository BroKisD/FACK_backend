package com.app.repository;

import com.app.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, String> {
    List<TestSession> findByTestId(String testId);
    List<TestSession> findByStudentId(String studentId);
    List<TestSession> findByStatus(String status);
}
