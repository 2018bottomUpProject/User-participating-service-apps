create table Location(
_id INT PRIMARY KEY AUTO_INCREMENT,
location POINT NOT NULL,
wifi_list VARCHAR(5000),
building_name VARCHAR(100),
place_name VARCHAR(80),
place_type CHAR(40)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

create table Review(
_id INT PRIMARY KEY AUTO_INCREMENT,
place_id INT NOT NULL,
article VARCHAR(1000),
timestamp DATETIME,

CONSTRAINT fk_placeid FOREIGN KEY ( place_id ) REFERENCES Location ( _id ) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;

create table PlaceType(
place_type CHAR(40),
waiting_time INT NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8; 

create table User(
_id CHAR(50) PRIMARY KEY,
Password CHAR(130)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 

create table Permission(
user_id CHAR(50) NOT NULL,
place_id INT NOT NULL,
stay_time INT,
visited INT,
permission INT,

CONSTRAINT fk_userid FOREIGN KEY ( user_id ) REFERENCES User ( _id ) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;