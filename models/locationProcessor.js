let Location = require('../DB/user');

async function addLocation(e) {
    let locationModel = new Location(e);

    await locationModel.save();
}

function getLocations() {
    return Location.find({}, function(err, data){
        if (err) return console.error(err);
    });
}

module.exports = {
    add : addLocation,
    get : getLocations
}