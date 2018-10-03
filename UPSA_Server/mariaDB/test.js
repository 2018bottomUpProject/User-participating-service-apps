"use strict";
let sql = require('./db_sql')();
let init_sql = require('./db_init_sql')();

console.log("\"".replace("\"","\\\"").replace("'","\\'"));
console.log('test.js started');
let sql_query = 'select * from Location where _id=19';
sql.testselect(sql_query, function(err, data){
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
//sql.newDocument("asdf", "aslkdjglkasd",function(){console.log("end")});