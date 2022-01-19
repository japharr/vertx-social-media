--
-- Take this script with a grain of salt and adapt it to your RDBMS
--
CREATE TABLE users (
 username VARCHAR(255) NOT NULL,
 password VARCHAR(255) NOT NULL
 email VARCHAR(500) NOT NULL,
 first_name VARCHAR(100),
 last_name VARCHAR(100),
 created_date TIMESTAMP,
 last_modified_date TIMESTAMP
);

CREATE TABLE users_roles (
 username VARCHAR(255) NOT NULL,
 role VARCHAR(255) NOT NULL
);

CREATE TABLE roles_perms (
 role VARCHAR(255) NOT NULL,
 perm VARCHAR(255) NOT NULL
);

ALTER TABLE users ADD CONSTRAINT pk_username PRIMARY KEY (username);
ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email);
ALTER TABLE users_roles ADD CONSTRAINT pk_users_roles PRIMARY KEY (username, role);
ALTER TABLE roles_perms ADD CONSTRAINT pk_roles_perms PRIMARY KEY (role, perm);

ALTER TABLE users_roles ADD CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users(username);
