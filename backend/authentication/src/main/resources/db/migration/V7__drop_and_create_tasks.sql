-- ================================
-- V7: Drop and recreate tasks table
-- ================================

-- Drop first (safe even if it doesn't exist)
DROP TABLE IF EXISTS tasks;

-- Recreate tasks table
CREATE TABLE tasks (
                       id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,

                       title VARCHAR(255),
                       description TEXT,

                       status VARCHAR(50) NOT NULL,
                       priority VARCHAR(50),
                       due_date DATE,

                       created_by BIGINT NULL,
                       user_id BIGINT NULL,

                       INDEX idx_tasks_status (status),
                       INDEX idx_tasks_created_by (created_by),
                       INDEX idx_tasks_user_id (user_id),

                       CONSTRAINT fk_tasks_created_by
                           FOREIGN KEY (created_by)
                               REFERENCES users(id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE,

                       CONSTRAINT fk_tasks_assigned_to
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE SET NULL
                               ON UPDATE CASCADE
);