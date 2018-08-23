"use strict";
let sql = require('./db_sql')();
let init_sql = require('./db_init_sql')();

console.log('test.js started');
sql.testselect(function(err, data){
    if (err) console.log(err);
    else console.log(data);

    sql.pool.end(function(err){
        if (err) console.log(err);
        else {
            console.log('Connection pool has closed');
            console.log('test.js finished');
        }
    });
});