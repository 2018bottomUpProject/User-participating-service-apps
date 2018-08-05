"use strict";
let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
    res.send('test(get)');
});
router.post('/', function(req, res, next){
    res.send('test(post)');
});
module.exports = router;