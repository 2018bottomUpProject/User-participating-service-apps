let pool = require('./db_connect');

let init = function(callback){
    pool.getConnection(function(err, con){
        let  sql = '' +
            '' +
            'create table Location(\n' +
            '_id INT NOT NULL AUTO_INCREMENT,\n' +
            'location POINT NOT NULL,\n' +
            'wifi_list VARCHAR(5000),\n' +
            'building_name VARCHAR(100),\n' +
            'place_name VARCHAR(80),\n' +
            'place_type CHAR(40),\n' +
            '\n' +
            'PRIMARY KEY(_id)\n' +
            ')ENGINE=INNODB DEFAULT CHARSET=utf8; ' +
            '' +
            'create table Review(\n' +
            '_id INT PRIMARY KEY AUTO_INCREMENT,\n' +
            'place_id INT NOT NULL,\n' +
            'article_id VARCHAR(1000),\n' +
            'timestamp DATETIME,\n' +
            '\n' +
            'CONSTRAINT fk_placeid FOREIGN KEY ( place_id ) REFERENCES Location ( _id ) ON DELETE CASCADE ON UPDATE CASCADE\n' +
            ')ENGINE=INNODB DEFAULT CHARSET=utf8;' +
            '' +
            'create table PlaceType(\n' +
            'place_type CHAR(40),\n' +
            'waiting_time INT NOT NULL\n' +
            ') ENGINE=INNODB DEFAULT CHARSET=utf8; ' +
            '' +
            'create table User(\n' +
            '_id CHAR(50) PRIMARY KEY,\n' +
            'Password CHAR(130)\n' +
            ')ENGINE=INNODB DEFAULT CHARSET=utf8;' +
            '' +
            'create table Permission(\n' +
            'user_id CHAR(50) NOT NULL,\n' +
            'place_id INT NOT NULL,\n' +
            'stay_time INT,\n' +
            'visited INT,\n' +
            'permission INT,\n' +
            '\n' +
            'CONSTRAINT fk_userid FOREIGN KEY ( user_id ) REFERENCES User ( _id ) ON DELETE CASCADE ON UPDATE CASCADE\n' +
            ')ENGINE=INNODB DEFAULT CHARSET=utf8;';
        if(err){
            console.log(err);
            return;
        }
        con. query(sql, function (err, result, fields) {
            con.release();
            if (err) return callback(err);
            callback(null, result);
        });
    });
};

module.exports = function () {
    return {
        init: init,
        pool: pool
    }
};