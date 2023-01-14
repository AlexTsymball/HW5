DROP DATABASE IF EXISTS db;
CREATE DATABASE db;

USE db;

DROP TABLE IF EXISTS genre;
CREATE TABLE genre (
                     id INT AUTO_INCREMENT PRIMARY KEY,
                     name VARCHAR(255) NOT NULL UNIQUE)
    ENGINE=INNODB;

DROP TABLE IF EXISTS book;
CREATE TABLE book (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         author VARCHAR(255) NOT NULL,
                         id_genre INT NOT NULL,
                         FOREIGN KEY (id_genre) REFERENCES genre(id) ON DELETE CASCADE )
    ENGINE=INNODB;

insert into genre (name) values ('romance');
insert into genre (name) values ('historical');
insert into genre (name) values ('detective');
insert into genre (name) values ('fantasy');
insert into genre (name) values ('novel');
insert into genre (name) values ('novel');