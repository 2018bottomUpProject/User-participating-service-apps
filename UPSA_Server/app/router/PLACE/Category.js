"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {//해당 위치에 일정 시간 이상 머물렀을 경우
    console.log("CATEGORY : ",req.query.Category);
    //그냥 위치에 맞는 시설 중 첫 번째를 보냄
    let category = {
        'CAFE' : 5*60,
        'RESTAURANT' : 30*60,
        'PARK' : 10*60
    };
    let name = req.query.Category.replace(/"/gi,"").replace(/ /gi, "");
    let returnval = category[name]+"";
    console.log("CATEGORY:GET -> CATCHED PARAMETER : ", name);
    console.log("CATEGORY:GET -> RETURN VALUE : ", returnval);
    res.send(returnval);
    //http://192.168.1.52:8080/category?Category=%22CAFE%22
});
router.post('/', function(req, res, next){//해당 위치를 임시 등록(글 등록은 안함.)
    res.send('test(post)');
});
router.delete('/', function(req, res, next){
    res.send('test(post)');
});
module.exports = router;
