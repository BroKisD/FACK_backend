CREATE TABLE `users` (
  `id` VARCHAR(36) PRIMARY KEY,
  `name` varchar(255),
  `email` varchar(255) UNIQUE NOT NULL,
  `password_hash` varchar(255),
  `role` varchar(255) COMMENT 'student | professor | admin',
  `status` varchar(255) COMMENT 'active | locked | disabled',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `last_login_at` timestamp
);

CREATE TABLE `courses` (
  `id` VARCHAR(36) PRIMARY KEY,
  `code` varchar(255) UNIQUE NOT NULL COMMENT 'e.g., CS101',
  `name` varchar(255) NOT NULL,
  `description` text,
  `professor_id` VARCHAR(36) NOT NULL,
  `semester` varchar(255) COMMENT 'e.g., Fall 2024',
  `status` varchar(255) COMMENT 'active | archived',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `course_enrollments` (
  `id` VARCHAR(36) PRIMARY KEY,
  `course_id` VARCHAR(36) NOT NULL,
  `student_id` VARCHAR(36) NOT NULL,
  `enrolled_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(255) COMMENT 'enrolled | dropped | completed'
);

CREATE TABLE `exams` (
  `id` VARCHAR(36) PRIMARY KEY,
  `course_id` VARCHAR(36) NOT NULL,
  `title` varchar(255),
  `description` text,
  `professor_id` VARCHAR(36) NOT NULL,
  `exam_file_url` varchar(255),
  `duration_minutes` integer,
  `start_available_at` timestamp,
  `end_available_at` timestamp,
  `recording_required` TINYINT DEFAULT 1,
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `test_sessions` (
  `id` VARCHAR(36) PRIMARY KEY,
  `test_id` VARCHAR(36) NOT NULL,
  `student_id` VARCHAR(36) NOT NULL,
  `start_time` timestamp,
  `end_time` timestamp,
  `status` varchar(255) COMMENT 'scheduled | running | submitted | terminated',
  `screen_recording_path` varchar(255) COMMENT 'Local file path to screen recording',
  `ip_address` varchar(255),
  `device_info` varchar(255),
  `browser_info` varchar(255),
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `refresh_token_sessions` (
  `id` VARCHAR(36) PRIMARY KEY,
  `user_id` VARCHAR(36) NOT NULL,
  `token_hash` VARCHAR(128) NOT NULL,
  `expires_at` timestamp NOT NULL,
  `revoked_at` timestamp NULL,
  `ip_address` VARCHAR(100),
  `user_agent` VARCHAR(500),
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX `course_enrollments_index_0` ON `course_enrollments` (`course_id`, `student_id`);
CREATE INDEX `refresh_token_sessions_user_idx` ON `refresh_token_sessions` (`user_id`);
CREATE INDEX `refresh_token_sessions_hash_idx` ON `refresh_token_sessions` (`token_hash`);

ALTER TABLE `courses` ADD FOREIGN KEY (`professor_id`) REFERENCES `users` (`id`);

ALTER TABLE `course_enrollments` ADD FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

ALTER TABLE `course_enrollments` ADD FOREIGN KEY (`student_id`) REFERENCES `users` (`id`);

ALTER TABLE `exams` ADD FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

ALTER TABLE `exams` ADD FOREIGN KEY (`professor_id`) REFERENCES `users` (`id`);

ALTER TABLE `test_sessions` ADD FOREIGN KEY (`test_id`) REFERENCES `exams` (`id`);

ALTER TABLE `test_sessions` ADD FOREIGN KEY (`student_id`) REFERENCES `users` (`id`);
ALTER TABLE `refresh_token_sessions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
