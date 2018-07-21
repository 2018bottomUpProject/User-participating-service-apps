const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';

const PlaceDBName = "BOTTOMUP_PLACE";
const UserDBName = "BOTTOMUP_USER";


const insert = function(db, collection, insertlist, callback) {//[{key:value, key:value...}, {...}, {...}...]
  const collection = db.collection(collection);
  // Insert some documents
  collection.insertMany(insertlist, function(err, result) {
    assert.equal(err, null);
    assert.equal(insertlist.length, result.result.n);
    assert.equal(insertlist.length, result.ops.length);
    console.log("Inserted 3 documents into the collection");
    callback(result);
  });
}
const update = function(db, collection, find, update, callback) {//find : {key:value}(찾는 대상), update : {}
  const collection = db.collection('Location');
  // Update document where a is 2, set b equal to 1
  collection.updateOne(find, 
    , { $set: { b : 1 } }, function(err, result) {
    assert.equal(err, null);
    assert.equal(1, result.result.n);
    console.log("Updated the document with the field a equal to 2");
    callback(result);
  });  
}


var DBConnector = function(){
    
};
DBConnector.prototype.

module.exports = DBConnector;