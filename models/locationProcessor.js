let Location = require('../DB/user');
const fs = require('fs');
const dBFilePath1 = 'DB/locationData.json';

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: addLocation
--
-- DATE: March 23, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Peter Xiong
--
-- PROGRAMMER: Peter Xiong
--
-- INTERFACE:       addLocation()
--
-- RETURNS: void.
-- 
-- NOTES:
-- This is used to save location data. Use can use POST method to save data into database (). 
-- This function is deprecated now. We write data into json file in TCP server instead of using database
----------------------------------------------------------------------------------------------------------------------*/
async function addLocation(e) {
    let locationModel = new Location(e);

    await locationModel.save();
}

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: getLocations
--
-- DATE: March 23, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Peter Xiong
--
-- PROGRAMMER: Peter Xiong
--
-- INTERFACE:       getLocations()
--
-- RETURNS: void.
-- 
-- NOTES:
-- Get locations information from json file. 
----------------------------------------------------------------------------------------------------------------------*/
function getLocations() {
    let rawdata = fs.readFileSync(dBFilePath1);
    let locationObj = JSON.parse(rawdata);
    let locationList = locationObj.locations;

    return locationList;
}

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: resetLocations
--
-- DATE: March 23, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Peter Xiong
--
-- PROGRAMMER: Peter Xiong
--
-- INTERFACE:       resetLocations()
--
-- RETURNS: void.
-- 
-- NOTES:
-- Reset locations information from json file. 
----------------------------------------------------------------------------------------------------------------------*/
function resetLocations() {
    let output={"locations": []};
    fs.writeFileSync(dBFilePath1,JSON.stringify(output));
    
    let rawdata = fs.readFileSync(dBFilePath1);
    let locationObj = JSON.parse(rawdata);
    let locationList = locationObj.locations;

    return locationList;
}

module.exports = {
    add : addLocation,
    get : getLocations,
    reset: resetLocations
}