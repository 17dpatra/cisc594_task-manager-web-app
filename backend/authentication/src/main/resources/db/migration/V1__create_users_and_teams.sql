-- =========================
-- Users table
-- =========================
CREATE TABLE users (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   username VARCHAR(50) NOT NULL UNIQUE,
   display_name VARCHAR(100),
   password_hash VARCHAR(255) NOT NULL,
   enabled BOOLEAN NOT NULL DEFAULT TRUE,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
       ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);


-- =========================
-- Teams table
-- =========================
CREATE TABLE teams (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   name VARCHAR(100) NOT NULL,
   created_by BIGINT NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_teams_created_by
       FOREIGN KEY (created_by) REFERENCES users(id)
);

-- =========================
-- User ↔ Team membership
-- =========================
CREATE TABLE user_teams (
    user_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, team_id),
    CONSTRAINT fk_user_teams_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_teams_team
        FOREIGN KEY (team_id) REFERENCES teams(id)
);


INSERT INTO users (username, password_hash, enabled)
VALUES (
   'admin',
   '$2a$14$GMsM2X9ytQF48accw.YO6.zalssv95Ka15.28D8AkIqeKVXVCmlCS',
   true
);

INSERT INTO user_roles (user_id, role)
VALUES
    (LAST_INSERT_ID(), 'GLOBAL_ADMIN'),
    (LAST_INSERT_ID(), 'USER');
