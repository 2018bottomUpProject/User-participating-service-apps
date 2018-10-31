"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("PERMISSION:GET -> DeviceId : ",req.query.DeviceId);
    console.log("PERMISSION:GET -> PlaceId : ",req.query.PlaceId);
    sql.getPermission(req.query.PlaceId, req.query.DeviceId, function(err, result){
        if(err){
            console.error(err);
        }
        console.log("PERMISSION:GET -> result : ",result);
        res.send(result);
    });
});
module.exports = router;