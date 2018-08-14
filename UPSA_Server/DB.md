# DB 구조
mariadb 사용
## 1. DB : UPSA
### A. Table : Location
{ lat:(double), lon:(double), WiFiList:(string), BuildingName:(string), PlaceType:(string), PlaceId:(int, \_\_id)(문서,후기를 찾을 때 필요)(index) }
```sql
create table Location(  
_id INT PRIMARY KEY AUTO_INCREMENT,  
location POINT NOT NULL,  
wifi_list VARCHAR(5000),  
building_name VARCHAR(100),  
place_type CHAR(40),  
)ENGINE=INNODB;
```

문서 : id를 파일명으로 한 파일로 저장.  
WiFiList : json array 방식으로 저장. 크기 관리 필요.

### B. Table : Review
{ PlaceId:(bigint), Article:(string), ArticleId:(int, \_\_id), Timestamp:(string) }  
```sql
create table Review(  
_id INT PRIMARY KEY AUTO_INCREMENT,  
place_id INT NOT NULL,  
constraint fk_placeid foreign key ( place_id ) refrences Location ( _id ) on delete cascade  
article_id VARCHAR(1000),  
timestamp DATETIME,  
)ENGINE=INNODB;
```
참고 : [외래키 설정하기](http://kb.globalsoft.co.kr/web/web_view.php?notice_no=315)

### E. Table : Log_\[BuildingID\]
{ ArticleId:(int), timestamp:(string), Deleted:{startline:(int), endline:(int), deletedText:(int)}, Added:{startline:(int), endline:(int), addedText:(int)} }

### F. Table : WaitingTime
{ BuildingType:(string), WaitingTime:(int) }

### G. Table : USER
{ UserId:(int), UserName:(string)(DeviceId), Password:(OPTION)(string) }

### H. Table : Permission_\[UserID\]
{ UserId:(int), PlaceId:(int), StayedTime:(int), VisitedTimes:(int), Permission:(int) }
