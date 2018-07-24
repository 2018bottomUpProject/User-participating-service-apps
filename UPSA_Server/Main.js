let http = require('http');
let https = require('https');

let DB = require("./DBConnector.js");
let Dummyapp = require('./Dummyapp');
let app;

let PORT_HTTP = 80;

DB.init();
process.on("SIGINT", async function () {
    await DB.close();
    process.exit();
});

/* Dummy HTTP server open
*  if do not want, just set it with annotation*/

Dummyapp.set('port', PORT_HTTP);
let server = http.createServer(Dummyapp);
