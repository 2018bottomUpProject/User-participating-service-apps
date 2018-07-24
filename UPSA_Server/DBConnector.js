"use strict";
const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';

//DB_Place
const PlaceDBName = "BOTTOMUP_PLACE";

const LocationName = "Location";
const DocumentName = "Document";
const ReviewPrefix = "Review_";
const LogName = "Log";

//DB_User
const UserDBName = "BOTTOMUP_USER";

const UserName = "User";
const PermissionPrefix = "Permission_";

//All DB, MongoClient
let client = null;
let PlaceDB = null;
let UserDB = null;

const insert = function(db, collectionName, insertlist, callback) {//[{key:value, key:value...}, {...}, {...}...]
  const collection = db.collection(collectionName);
  // Insert some documents
  collection.insertMany(insertlist, (err, result)=>{
    assert.equal(err, null);
    assert.equal(insertlist.length, result.result.n);
    assert.equal(insertlist.length, result.ops.length);
    console.log("Inserted");
    callback(result);
  });
};
const update = function(db, collectionName, filter, update, callback) {//find : {key:value}(찾는 대상), update : {}
  const collection = db.collection(collectionName);
  // Update document where a is 2, set b equal to 1
  collection.updateOne(filter, filter, { $set: update }, (err, result)=>{
    assert.equal(err, null);
    assert.equal(1, result.result.n);
    console.log("Updated");
    callback(result);
  });  
};
const find = function(db, collectionName, filter, callback) {//{key:value, ...}
    // Get the documents collection
    const collection = db.collection(collectionName);
    // Find some documents
    collection.find(filter).toArray((err, result)=>{
        assert.equal(err, null);
        console.log("Found the following records");
        console.log(result);
        callback(result);
    });
};
const remove = function(db, filter, callback) {
    // Get the documents collection
    const collection = db.collection('documents');
    // Delete document where a is 3
    collection.deleteMany(filter, (err, result)=>{
        assert.equal(err, null);
        console.log("Removed the document with the field a equal to ", filter, "deleted ", result.result.n);
        callback(result);
    });
};



let DBConnector = function(){

};
DBConnector.prototype.init = async function(){
    try {
        // Use connect method to connect to the Server
        client = await MongoClient.connect(url);
        PlaceDB = client.db(PlaceDBName);
        UserDB = client.db(UserDBName);
    } catch (err) {
        console.log(err.stack);
    }
};
DBConnector.prototype.close = async function(){
    if(PlaceDB) await PlaceDB.close();
    if(UserDB) await UserDB.close();
    if (client) await client.close();
};
DBConnector.prototype.setLocation = function(x, y, wifi_dic, building_type, building_name, building_id){
    insert(PlaceDB, LocationName, {"X":x, "Y":y, "WiFiDic":wifi_dic, "BuildingType":building_type, "BuildingName":building_name, "BuildingId":building_id}, (result)=>{
        console.log(result);
    });
};
DBConnector.prototype.getLocation = function(){

};

module.exports = DBConnector;