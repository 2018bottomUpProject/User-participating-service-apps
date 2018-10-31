"use strict";
let express = require("express");
let app = express();
let hbs = require("express-hbs");
let path = require('path');

let document_router = require("./app/router/PLACE/Document");
let locationfg_router = require("./app/router/PLACE/LocationFG");
let locationbg_router = require("./app/router/PLACE/LocationBG");
let log_router = require("./app/router/PLACE/Log");
let review_router = require("./app/router/PLACE/Review");
let category_router = require("./app/router/PLACE/Category");

let permission_router = require("./app/router/USER/Permission");
let user_router = require("./app/router/USER/User");

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'hbs');

app.use('/document', document_router);
app.use('/locationfg', locationfg_router);
app.use('/locationbg', locationbg_router);
app.use('/log', log_router);
app.use('/review', review_router);
app.use('/category', category_router);

app.use('/permission', permission_router);
app.use('/user', user_router);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    err.status = 403;
    res.render('error');
});
module.exports = app;