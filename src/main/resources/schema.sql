DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
  id int(11) NOT NULL AUTO_INCREMENT,
  email varchar(200),
  first_name VARCHAR(100),
  last_name VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (id)
);
