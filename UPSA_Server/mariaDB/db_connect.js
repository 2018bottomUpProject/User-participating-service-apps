let mysql = require('mysql');

module.exports = function () {
    let config = require('./config');    // ./는 현재 디렉토리를 나타냅니다
    let pool = mysql.createPool(config);

    return {
        getConnection: function (callback) {    // connection pool을 생성하여 리턴합니다
            pool.getConnection(callback);
        },
        end: function(callback){
            pool.end(callback);
        }
    }
}();