"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {//해당 위치에 일정 시간 이상 머물렀을 경우
    console.log("LOCATIONFG:GET -> X : ",req.query.X);
    console.log("LOCATIONFG:GET -> Y : ",req.query.Y);
    console.log("LOCATIONFG:GET -> WifiList : ",req.query.WifiList);
    console.log("LOCATIONFG:GET -> Category : ", req.query.Category);
    console.log("LOCATIONFG:GET -> Radius : ", req.query.Radius);
    console.log("LOCATIONFG:GET -> Auth : ",req.query.Auth);
    //그냥 위치에 맞는 시설 중 첫 번째를 보냄
    sql.getLocation("*", req.query.X, req.query.Y, (req.query.Category+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"), req.query.Radius, function(err, result){
        console.log("LOCATIONFG:GET -> result length : ", result.length);
        res.send(result);
    });
    //http://localhost:8080/locationfg?X=36.3619378&Y=127.35299439999994&WifiList=[]&Category=%22RESTAURANT%22&Radius=0.0001
});
router.post('/', function(req, res, next){//해당 위치를 임시 등록(글 등록은 안함.)
    console.log("LOCATIONFG:POST -> X : ",req.query.X);
    console.log("LOCATIONFG:POST -> Y : ",req.query.Y);
    console.log("LOCATIONFG:POST -> WifiList : ",req.query.WifiList);
    console.log("LOCATIONFG:POST -> BuildingName : ",req.query.BuildingName);
    console.log("LOCATIONFG:POST -> PlaceName : ",req.query.PlaceName);
    console.log("LOCATIONFG:POST -> Category : ",req.query.Category);
    console.log("LOCATIONFG:POST -> Auth : ",req.query.Auth);
    sql.newLocation(
        req.query.X,
        req.query.Y,
        (req.query.WifiList+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"),
        (req.query.BuildingName+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"),
        (req.query.PlaceName+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"),
        (req.query.Category+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"),
        function (err, result) {
        if(err){
            console.error(err);
            res.send(err);
            return;
        }
        else{
            res.send(result);
        }
    });
});
router.delete('/', function(req, res, next){
    res.send('test(post)');
});
module.exports = router;