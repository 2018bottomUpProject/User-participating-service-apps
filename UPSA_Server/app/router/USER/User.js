"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("REVIEW:GET -> Id : ",req.query.Id);
    console.log("REVIEW:GET -> Password : ", req.query.Password);
    sql.getUser(req.query.Id, req.query.password, function(err, result){
        res.send(result);
    });
});
router.post('/', function(req, res, next){
    console.log("REVIEW:POST -> Id : ",req.query.Id);
    console.log("REVIEW:POST -> Password : ", req.query.Password);
    sql.newUser(req.query.Id, req.query.Password, function(){
        res.send(result);
    });
});
module.exports = router;