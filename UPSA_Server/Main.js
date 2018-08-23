"use strict";
let http = require('http');
let https = require('https');


let Dummyapp = require('./Dummyapp');
let app = require('./app');

//if just want using Dummy, set this true.
let startDummyServer = false;

let port = 8899;
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
    app.set('port', port);
    server = http.createServer(app);
    server.listen(app.get('port'));
    server.on('error', onError);
    server.on('listening', onListening);


}
let sigint_func = function() {
    process.exit();
};
process.on("SIGINT", sigint_func);


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
let debug = require('debug');
function onListening() {
    let addr = server.address();
    let bind = typeof addr === 'string'
        ? 'pipe ' + addr
        : 'port ' + addr.port;
    debug('Listening on ' + bind);
}
