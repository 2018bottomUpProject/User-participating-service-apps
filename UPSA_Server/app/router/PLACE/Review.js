"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("REVIEW:GET -> PlaceId : ",req.query.PlaceId);
    console.log("REVIEW:GET -> index_start : ",req.query.index_start);
    console.log("REVIEW:GET -> index_end : ",req.query.index_end);
    sql.getReview(req.query.PlaceId, req.query.index_start, req.query.index_end, function(err, result){
        if(result === ""){
            res.send([{"result":"EMPTY"}]);
        }
        else {
            //console.log(result);
            res.send(result);
        }
    });
});
router.post('/', function(req, res, next){
    console.log("REVIEW:POST -> PlaceId : ",req.query.PlaceId);
    console.log("REVIEW:POST -> Article : ",req.query.Article);
    sql.newReview(req.query.PlaceId, (req.query.Article+"").replace(/\"/gi,"\\\"").replace(/'/gi,"\\'"), function(err, result){
        res.send(result);
    });
});
module.exports = router;