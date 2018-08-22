"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log(req.query.PlaceId);
    console.log(req.query.index_start);
    console.log(req.query.index_end);
    sql.getReview(req.query.PlaceId, req.query.index_start, req.query.index_end, function(err, result){
        res.send(result);
    });
});
router.post('/', function(req, res, next){
    res.render('API/PLACE/review', {  
        data: "OK"
    });
});
module.exports = router;