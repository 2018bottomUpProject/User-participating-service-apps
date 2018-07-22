let DB = require("./DBConnector.js");
process.on("SIGINT", function () {

    process.exit();
});
setInterval(function(){
    console.log("asdf");
},1000);