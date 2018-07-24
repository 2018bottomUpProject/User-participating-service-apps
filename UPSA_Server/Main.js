"use strict";
let http = require('http');
let https = require('https');

let DB = require("./DBConnector.js");
let Dummyapp = require('./Dummyapp');
let app;

//if just want using Dummy, set this true.
let startDummyServer = true;

let port = 8080;

DB.init();
process.on("SIGINT", async function () {
    await DB.close();
    process.exit();
});

let server;
if(startDummyServer) {
    /* Dummy HTTP server open                      *
    *  if do not want, just set it with annotation*/

    Dummyapp.set('port', port);
    server = http.createServer(Dummyapp);
    server.listen(Dummyapp.get('port'));
    server.on('error', onError);
    server.on('listening', onListening);
}
else{

}


function onError(error) {
    if (error.syscall !== 'listen') {
        throw error;
    }

    let bind = typeof port === 'string'
        ? 'Pipe ' + port
        : 'Port ' + port;

    // handle specific listen errors with friendly messages
    switch (error.code) {
        case 'EACCES':
            console.error(bind + ' requires elevated privileges');
            process.exit(1);
            break;
        case 'EADDRINUSE':
            console.error(bind + ' is already in use');
            process.exit(1);
            break;
        default:
            throw error;
    }
}

/**
 * Event listener for HTTP server "listening" event.
 */

function onListening() {
    let addr = server.address();
    let bind = typeof addr === 'string'
        ? 'pipe ' + addr
        : 'port ' + addr.port;
    debug('Listening on ' + bind);
}