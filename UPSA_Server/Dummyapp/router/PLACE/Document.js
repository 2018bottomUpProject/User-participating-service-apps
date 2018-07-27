"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log(req.query.X);
    res.render('API/PLACE/document', {
        data: JSON.stringify({ASDF:"ASDF"})
    });
});
router.post('/', function(req, res, next){
    res.send('test(post)');
});
module.exports = router;