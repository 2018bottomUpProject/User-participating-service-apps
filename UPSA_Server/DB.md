# DB 구조
mariadb 사용
## 0. DB init
```sql
create user UPSA@'%' identified by 'newscrap123';
create database UPSA;
grant all privileges on UPSA.* to UPSA@'%' identified by 'newscrap123';
```
## 1. DB : UPSA
### A. Table : Location
{ lat:(double), lng:(double), WiFiList:(string), BuildingName:(string), PlaceType:(string), PlaceName:(string), PlaceId:(int, \_\_id)(문서,후기를 찾을 때 필요)(index) }
```sql
create table Location(
_id INT NOT NULL AUTO_INCREMENT,
location POINT NOT NULL,
wifi_list VARCHAR(5000),
building_name VARCHAR(100),
place_name VARCHAR(80),
place_type CHAR(40),

PRIMARY KEY(_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 
```

문서 : id를 파일명으로 한 파일로 저장.  
WiFiList : json array 방식으로 저장. 크기 관리 필요.

### B. Table : Review
{ PlaceId:(int), Article:(string), ArticleId:(int, \_\_id), Timestamp:(string) }  
```sql
create table Review(
_id INT PRIMARY KEY AUTO_INCREMENT,
place_id INT NOT NULL,
article_id VARCHAR(1000),
timestamp DATETIME,

CONSTRAINT fk_placeid FOREIGN KEY ( place_id ) REFERENCES Location ( _id ) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```
참고 : [외래키 설정하기](http://kb.globalsoft.co.kr/web/web_view.php?notice_no=315)
  
### C. Table : PlaceType
{ PlaceType:(string), WaitingTime:(int) }
```sql
create table PlaceType(
place_type CHAR(40),
waiting_time INT NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8; 
```

### D. Table : USER
{ UserId:(int), UserName:(string)(DeviceId), Password:(OPTION)(string) }
```sql
create table User(
_id CHAR(50) PRIMARY KEY,
Password CHAR(130)
)ENGINE=INNODB DEFAULT CHARSET=utf8; 
```
패스워드 : SHA512 암호화 사용

### E. Table : Permission
{ UserId:(int), PlaceId:(int), StayedTime:(int), VisitedTimes:(int), Permission:(int) }
```sql
create table Permission(
user_id CHAR(50) NOT NULL,
place_id INT NOT NULL,
stay_time INT,
visited INT,
permission INT,

CONSTRAINT fk_userid FOREIGN KEY ( user_id ) REFERENCES User ( _id ) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=INNODB DEFAULT CHARSET=utf8;
```
