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
        if(result[0]!== undefined){
            console.log(result);
        }
        res.send(result);
    });
    //res.send([{"user_id":"7856599c2aeb876f","place_id":6,"stay_time":23,"visited":10,"permission":5}]);
});
module.exports = router;