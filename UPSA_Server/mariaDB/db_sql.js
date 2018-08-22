let pool = require('./db_connect');
let query_function = function(sql, callback){
    console.log(sql);
    pool.getConnection(function(err, con){
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
let testselect = function(callback){
    let  sql = 'select * from test where ST_DISTANCE(location,POINT(2,2))<= 1';
    query_function(sql,callback);
};
let getLocation = function(X, Y, category, radius, callback){
    let sql = "select * from Location where ST_DISTANCE(location, POINT("+X+","+Y+"))<="+radius;
    if(category !== "\"ALL\""){
        sql = sql + " AND place_type=" + category;
    }
    query_function(sql,callback);
};
let newLocation = function(X,Y,WifiList,BuildingName,PlaceName,PlaceType){//자료 처리가 필요함.

};
let getDocument = function(article){//파일 관련

};
let delDocument = function(article){//파일 관련

};
let getReview = function(place_id, index_start, index_end, callback){
    let sql = "select * from Review where place_id=="+place_id+" ORDER BY timestamp DESK";//n개의 리뷰만을 가져오도록 수정해야 함.
    query_function(sql,callback);
};
let newReview = function(place_id, article, callback){
    let sql = "insert into Review values(0,"+place_id+","+article+", NOW())";
    query_function(sql,callback);
};
let editReview = function(id, place_id, article, callback){
    let sql = "update Review set place_id="+place_id+" article="+article+" where id="+id;
    query_function(sql,callback);
};
let delReview = function(id, callback){
    let sql = "delete from Review where id="+id;
    query_function(sql,callback);
};
let getLog = function(){

};
let getCategory = function(){

};
let getPermission = function(place_id, user_id, callback){
    let sql = "select * from Permission where place_id=="+place_id+" AND user_id="+user_id;//n개의 리뷰만을 가져오도록 수정해야 함.
    query_function(sql,callback);
};
let getUser = function(device_id, callback){
    let sql = "select * from User where device_id=="+device_id;//n개의 리뷰만을 가져오도록 수정해야 함.
    query_function(sql,callback);
};
let newUser = function(device_id, password, callback){
    let sql = "insert into Review values("+device_id+","+ password+")";
    query_function(sql,callback);
};
let delUser = function(){

};

module.exports = function () {
    return {
        testselect: testselect,
        getLocation:getLocation,
        newLocation:newLocation,
        getDocument:getDocument,
        delDocument:delDocument,
        getReview:getReview,
        newReview:newReview,
        editReview:editReview,
        delReview:delReview,
        getLog:getLog,
        getCategory:getCategory,
        getPermission:getPermission,
        getUser:getUser,
        newUser:newUser,
        delUser:delUser,
        pool: pool
    }
};