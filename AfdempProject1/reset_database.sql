-- Adminer 4.3.1 MySQL dump
DROP DATABASE `afdemp_java_1`;

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 1;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

CREATE DATABASE `afdemp_java_1` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `afdemp_java_1`;

CREATE TABLE `users` (
  `id` int(100) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(30) UNIQUE NOT NULL,
  `password` blob NOT NULL,
  `salt`    blob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TRIGGER encrypt_password
	BEFORE INSERT ON users
    FOR EACH ROW SET NEW.password = UNHEX(CONCAT(NEW.password, 'some random salt here'),512);

INSERT INTO `users` (`id`, `username`, `password`) VALUES
(1,	'admin',	'admin'),
(2,	'user1',	'password1'),
(3,	'user2',	'password2');

CREATE TABLE `accounts` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `user_id` int(100) unsigned NOT NULL,
  `transaction_date` datetime(3) NOT NULL,
  `amount` decimal(20,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `accounts` (`id`, `user_id`, `transaction_date`, `amount`) VALUES
(1,	1,	'2017-11-13 19:28:47',	100000),
(2,	2,	'2017-11-13 19:29:06',	1000),
(3,	3,	'2017-11-13 19:29:15',	1000);

CREATE OR REPLACE VIEW accounts_of_users AS
	SELECT user_id, transaction_date, amount, username 
    FROM accounts JOIN users ON(accounts.user_id = users.id);

DELIMITER $$
CREATE FUNCTION user_exists (_username text, _password text) RETURNS boolean
    DETERMINISTIC
BEGIN
	RETURN EXISTS (SELECT * FROM users WHERE username = _username AND password = UNHEX(CONCAT(_password, 'some random salt here'),512));
END$$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION balance_has_not_changed (_user_id int, _amount decimal(20,2), _transaction_date datetime(3)) RETURNS boolean
    DETERMINISTIC
BEGIN
	RETURN EXISTS (SELECT * FROM accounts_of_users 
                        WHERE _user_id = user_id AND amount = _amount AND _transaction_date = transaction_date);
END$$
DELIMITER ;
    
-- 2017-11-13 17:30:28
GRANT ALL PRIVILEGES ON `afdemp_java_1`.* TO 'afdemp'@'%' IDENTIFIED BY 'afdemp';