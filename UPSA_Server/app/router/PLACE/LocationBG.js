"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("X : ",req.query.X);
    console.log("Y : ",req.query.Y);
    console.log("WifiList : ",req.query.WifiList);
    //그냥 위치에 맞는 시설 중 첫 번째를 보냄

    res.send('test(get)');
});
router.post('/', function(req, res, next){
    res.send('test(post)');
});
module.exports = router;