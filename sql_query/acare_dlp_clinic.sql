CREATE DATABASE acare_dev
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
Drop database acare_dev;
CREATE USER 'acare'@'localhost'
IDENTIFIED BY '123456';

GRANT ALL PRIVILEGES ON acare_dev.* TO 'acare'@'localhost';
FLUSH PRIVILEGES;