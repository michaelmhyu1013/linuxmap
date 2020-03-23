let mod = require('../models/locationProcessor');

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
-- Get locations information from json file. Use can use fetch command to get data from server and 
-- display them in application.
----------------------------------------------------------------------------------------------------------------------*/
exports.getLocations = function(req,res,next) {
    let Locations = mod.get();

    res.send(Locations);
}

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: saveLocation
--
-- DATE: March 23, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Peter Xiong
--
-- PROGRAMMER: Peter Xiong
--
-- INTERFACE:       saveLocation()
--
-- RETURNS: void.
-- 
-- NOTES:
-- This is API used to save location into json file. Use can use POST method to save data into json file.
----------------------------------------------------------------------------------------------------------------------*/
exports.saveLocation = function(req,res,next) {
    let p_lat = req.body.latitude;
    let p_long = req.body.longitude;
    let p_timeStamp = req.body.timeStamp;

    let pOject = {
        latitude: p_lat,
        longitude: p_long,
        timeStamp: p_timeStamp
    }
 
    mod.add(pOject)
      .then(item => {
        res.send("Data saved successfully");
      })
      .catch(err => {
        res.status(400).send("Error: fail to save!");
      });
}
