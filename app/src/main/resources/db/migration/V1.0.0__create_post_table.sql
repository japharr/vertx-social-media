--
-- Take this script with a grain of salt and adapt it to your RDBMS
--
CREATE TABLE posts (
 post_id VARCHAR(255) NOT NULL,
 text VARCHAR(200) NOT NULL,
 userid VARCHAR(255) NOT NULL,
 created_date TIMESTAMP,
 last_modified_date TIMESTAMP
);

CREATE TABLE users (
 userid VARCHAR(255) NOT NULL,
 name VARCHAR(100) NOT NULL,
 image_url  VARCHAR(300),
 created_date TIMESTAMP,
 last_modified_date TIMESTAMP
);

ALTER TABLE posts ADD CONSTRAINT pk_posts PRIMARY KEY (post_id);
ALTER TABLE users ADD CONSTRAINT pk_users PRIMARY KEY (userid);