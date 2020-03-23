let Location = require('../DB/user');
const fs = require('fs');
const dBFilePath1 = 'DB/locationData.json';

async function addLocation(e) {
    let locationModel = new Location(e);

    await locationModel.save();
}

function getLocations() {
    let rawdata = fs.readFileSync(dBFilePath1);
    let locationObj = JSON.parse(rawdata);
    let locationList = locationObj.locations;

    return locationList;

    // return Location.find({}, function(err, data){
    //     if (err) return console.error(err);
    // });
}

module.exports = {
    add : addLocation,
    get : getLocations
}