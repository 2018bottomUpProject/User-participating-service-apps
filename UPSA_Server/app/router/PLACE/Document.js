"use strict";
let express = require('express');
let router = express.Router();
let sql = require('../../../mariaDB/db_sql')();

/* GET home page. */
router.get('/:PlaceId', function(req, res, next) {//PlaceId 받음
    console.log("DOCUMENT:GET -> PLACE_ID : ",req.params.PlaceId);
    sql.getDocument(req.params.PlaceId, req.query.Article,function(err, doc){
        if(err){
            console.error("DOCUMENT:GET FAILED : ", err);
            res.render('error.hbs', {
                message: "get document failed(no such document)",
                error:{
                    status:404,
                    stack:err,
                    shortstack:err
                }
            });
        }
        else{
            console.log("DOCUMENT:GET : ", doc);
            res.send(doc);
        }
    });
});
router.post('/:PlaceId', function(req, res, next){
    console.log("DOCUMENT:POST -> PLACE_ID : ",req.params.PlaceId);
    console.log("DOCUMENT:POST -> ARTICLE : ", req.query.Article);
    sql.newDocument(req.params.PlaceId, req.query.Article, function(err){
        if(err){
            console.error("DOCUMENT:GET FAILED : ", err);
            res.render('error.hbs', {
                message: "get document failed(no such document)",
                error:{
                    status:404,
                    stack:err,
                    shortstack:err
                }
            });
        }
        else{
            res.send([{"result":"OK"}]);
        }
    });
});
router.put('/:PlaceId',function(req,res,next) {
    console.log("DOCUMENT:PUT -> PLACE_ID : ",req.params.PlaceId);
    console.log("DOCUMENT:PUT -> ARTICLE : ", req.query.Article);
    sql.editDocument(req.params.PlaceId, req.query.Article, function(err, doc){
        if(err){
            console.error("DOCUMENT:PUT FAILED : ", err);
            res.render('error.hbs', {
                message: "put document failed(no such document)",
                error:{
                    status:404,
                    stack:err,
                    shortstack:err
                }
            });
        }
        else{
            res.send(doc);
        }
    });
});
router.delete('/:PlaceId',function(req,res,next){
    console.log("DOCUMENT:DELETE -> PLACE_ID : ",req.params.PlaceId);
    sql.delDocument(req.params.PlaceId, function(err){
        if(err){
            console.error("DOCUMENT : DELETE FAILED : ", err);
            res.render('error.hbs', {
                message: "delete document failed(no such document)",
                error:{
                    status:404,
                    stack:err,
                    shortstack:err
                }
            });
        }
        else{
            res.send([{"result":"OK"}]);
        }
    });
});
module.exports = router;