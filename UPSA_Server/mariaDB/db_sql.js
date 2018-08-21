let pool = require('./db_connect');

let testselect = function(callback){
    pool.getConnection(function(err, con){
        let  sql = 'select * from test where ST_DISTANCE(location,POINT(2,2))<= 1';
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
let getLocation = function(X, Y, WifiList){
    pool.getConnection(function(err,con){
        let sql = "select * from Location where ST_DISTANCE(location, POINT("+X+","+Y+"))<=1";

    });
};
let postLocation = function(){

};
let delDocument = function(){

};
let getReview = function(){

};
let newReview = function(){

};
let editReview = function(){

};
let delReview = function(){

};
let getLog = function(){

};
let getCategory = function(){

};
let getPermission = function(){

};
let getUser = function(){

};
let newUser = function(){

};
let delUser = function(){

};

module.exports = function () {
    return {
        testselect: testselect,
        pool: pool
    }
};