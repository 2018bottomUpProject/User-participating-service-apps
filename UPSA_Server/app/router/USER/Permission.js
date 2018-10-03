"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("PERMISSION:GET -> DeviceId : ",req.query.DeviceId);
    console.log("PERMISSION:GET -> PlaceId : ",req.query.PlaceId);
    sql.getPermission(req.query.PlaceId, req.query.DeviceId, function(err, result){
        if(err){
            console.error(err);
        }
        res.send(result);
    });
});
module.exports = router;