"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/:id', function(req, res, next) {
    console.log(req.params.id);
    res.render('API/PLACE/log', {
        data: JSON.stringify({ArticleId:req.params.id,
                              timestamp:"2018.07.31 16:09:22",
                              Deleted:[{startline:4, endline:4, deletedText:"삭제된 내용 1"},
                                       {startline:10, endline:11, deletedText:"삭제된 내용 2\n두줄짜리임"}],
                              Added:[{startline:2, endline:2, addedText:"새로 추가된 내용"}]})
    });
});
router.post('/', function(req, res, next){
    res.render('API/PLACE/log', {
        data: "OK"
    });
});
module.exports = router;