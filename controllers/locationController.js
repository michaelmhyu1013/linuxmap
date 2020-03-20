let mod = require('../models/locationProcessor');

exports.getLocations = function(req,res,next) {
    let Locations = mod.get();
    // Locations.then((datas) => {
    //     // Create context Object with 'usersData' key to protext security info
    //     const context = {
    //         usersData: datas.map(data => {
    //         return {
    //             latitude: data.latitude,
    //             longitude: data.longitude,
    //             timeStamp: data.timeStamp
    //         }
    //         })
    //     }
    //     res.send(context.usersData);
    // });
    res.send(Locations);
    console.log("Get location...");
}

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
        res.send("Location saved to database");
      })
      .catch(err => {
        res.status(400).send("unable to save to database");
      });
}
