CREATE SCHEMA `SPRING_BATCH` DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
GRANT ALL PRIVILEGES ON SPRING_BATCH.* TO 'test'@'localhost';

CREATE TABLE user(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(30),
	password VARCHAR(20),
	age INT
);

INSERT INTO user(username, password, age) VALUES
("Kevin", "111", 11),
("Roger", "222", 21),
("Ben", "333", 31),
("Jason", "555", 51),
("Woody", "666", 61);


CREATE TABLE customer(
	id INT PRIMARY KEY AUTO_INCREMENT,
	firstName VARCHAR(50),
	lastName VARCHAR(50),
	birthday VARCHAR(50)
);
