--
-- Take this script with a grain of salt and adapt it to your RDBMS
--
CREATE TABLE users (
 userid VARCHAR(255) NOT NULL,
 username VARCHAR(255) NOT NULL,
 password VARCHAR(255) NOT NULL,
 email VARCHAR(500) NOT NULL,
 name VARCHAR(255),
 email_verify BOOLEAN NOT NULL,
 created_date TIMESTAMP,
 last_modified_date TIMESTAMP
);

CREATE TABLE users_roles (
 userid VARCHAR(255) NOT NULL,
 role VARCHAR(255) NOT NULL
);

CREATE TABLE roles_perms (
 role VARCHAR(255) NOT NULL,
 perm VARCHAR(255) NOT NULL
);

ALTER TABLE users ADD CONSTRAINT pk_userid PRIMARY KEY (userid);
ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email);

ALTER TABLE users_roles ADD CONSTRAINT pk_users_roles PRIMARY KEY (userid, role);
ALTER TABLE roles_perms ADD CONSTRAINT pk_roles_perms PRIMARY KEY (role, perm);

ALTER TABLE users_roles ADD CONSTRAINT fk_userid FOREIGN KEY (userid) REFERENCES users(userid);
