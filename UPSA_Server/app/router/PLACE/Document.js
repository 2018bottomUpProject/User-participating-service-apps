"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {//PlaceId 받음
    console.log(req.query.PlaceId);
});
router.post('/', function(req, res, next){
    console.log("DOCUMENT -> PLACE_ID : ",req.query.PlaceId);
    console.log("DOCUMENT -> ARTICLE : ", req.query.Article);
    sql.newDocument()
});
module.exports = router;