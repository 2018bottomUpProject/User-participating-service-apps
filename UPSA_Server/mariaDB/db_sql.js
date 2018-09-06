"use strict";
let pool = require('./db_connect');
const fs = require('fs');
const childprocess = require("child_process");
require("date-utils");
console.log("sql starts on "+(new Date(Date.now())).toISOString());
let query_function = function(sql, callback){
    console.log("DB_SQL : ",sql);
    pool.getConnection(function(err, con){
        if(err){
            console.error(err);
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
    let  sql = 'select * from Location where _id=1';
    query_function(sql,callback);
};
let getLocation = function(X, Y, category, radius, callback){
    let sql = "select * from Location where ST_DISTANCE(location, POINT("+X+","+Y+"))<="+radius;
    if(category !== "\"ALL\"" || category !== "ALL"){
        sql = sql + " AND place_type=" + category;
    }
    query_function(sql,callback);
};
let newLocation = function(X,Y,WifiList,BuildingName,PlaceName,PlaceType){//자료 처리가 필요함.

};
let newDocument = function(place_id, article, callback){
    try {
        fs.mkdirSync('Documents/' + place_id);
    }
    catch(err){
        console.error(err);
        callback(err);
        return;
    }
    childprocess.execSync("git init",{"cwd":'Documents/' + place_id });
    let dir = 'Documents/' + place_id + "/doc";
    try {
        fs.writeFileSync(dir, article, 'utf8');
        console.log("DB_SQL : new Document ",place_id, " created. commit : "+(new Date(Date.now())).toISOString());
        childprocess.exec('git add --all;git commit -m "'+(new Date(Date.now())).toISOString()+'"',{"cwd":'Documents/' + place_id },function(){});
        callback();
    }
    catch(err){
        console.error(err);
        callback(err);
        return;
    }
};
let editDocument = function(place_id, article, callback){
    let dir = 'Documents/' + place_id + "/doc";
    try{
        let file_edit = fs.existsSync(dir, 'utf8');
        if(!file_edit) callback("editDocument : no such document");
        fs.writeFileSync(dir, article, 'utf8');
        console.log("DB_SQL : new Change on ",place_id, " created. commit : "+(new Date(Date.now())).toISOString());
        childprocess.exec('git add --all;git commit -m "'+(new Date(Date.now())).toISOString()+'"',{"cwd":'Documents/' + place_id },function(){});
        callback(null, fs.readFileSync(dir, 'utf8'));
    }
    catch(err){
        console.error(err);
        callback(err);
        return;
    }
};
let getDocument = function(place_id, article, callback){//파일 관련
    let dir = 'Documents/' + place_id + "/doc";
    try{
        callback(null, fs.readFileSync(dir, 'utf8'));
    }
    catch(err){
        console.error(err);
        callback(err);
    }
};
let delDocument = function(place_id, callback){//파일 관련
    let dir = 'Documents/' + place_id;
    try{
        let recurem = function(path) {
            if (fs.existsSync(path)) {
                fs.readdirSync(path).forEach(function(file, index){
                    var curPath = path + "/" + file;
                    if (fs.lstatSync(curPath).isDirectory()) { // recurse
                        recurem(curPath);
                    } else { // delete file
                        fs.unlinkSync(curPath);
                    }
                });
                fs.rmdirSync(path);
            }
        };
        recurem(dir);
        callback(null);
    }
    catch(err){
        console.error(err);
        callback(err);
    }
};
let getReview = function(place_id, index_start, index_end, callback){
    let sql = "select * from Review where place_id="+place_id+" ORDER BY timestamp DESK";//n개의 리뷰만을 가져오도록 수정해야 함.

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
let getPermission = function(place_id, user_id, callback){
    let sql = "select * from Permission where place_id="+place_id+" AND user_id="+user_id;//n개의 리뷰만을 가져오도록 수정해야 함.
    query_function(sql,callback);
};
let getUser = function(device_id, callback){
    let sql = "select * from User where device_id="+device_id;//n개의 리뷰만을 가져오도록 수정해야 함.
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
        newDocument:newDocument,
        editDocument:editDocument,
        getDocument:getDocument,
        delDocument:delDocument,
        getReview:getReview,
        newReview:newReview,
        editReview:editReview,
        delReview:delReview,
        getLog:getLog,
        getPermission:getPermission,
        getUser:getUser,
        newUser:newUser,
        delUser:delUser,
        pool: pool
    }
};