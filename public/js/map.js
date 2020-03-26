"use strict";

let gmap;
const resource = "/locations";
const mapDiv = document.getElementById("map");
const vancouver = { lat: 49.2487419, lng: -123.003096 };
const options = {
    zoom: 11,
    center: vancouver,
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: initMap
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:       initMap()
--
-- RETURNS: void.
-- 
-- NOTES:
-- Creates a Google Map using the Google Maps API and updates the locations on the map using the data fetched from 
-- connected android clients.
----------------------------------------------------------------------------------------------------------------------*/
var initMap = () => {
    gmap = new google.maps.Map(mapDiv, options);
    updateMap(gmap);
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: updateMap
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:       updateMap(map)
--                      map: Google Map object that will be updated with user locations
--
-- RETURNS: void.
-- 
-- NOTES:
-- Call this function to take the specified Google Map and update it with user locations. The user locations is fetched
-- from a JSON file that contains an array of all locations that are received from any Android clients that are connected
-- to the node server. Locations that have the same username will be drawn as one polyline on the map.
----------------------------------------------------------------------------------------------------------------------*/
const updateMap = map => {
    fetch(resource)
        .then(res => {
            if (res.status !== 200) {
                console.log("Error. Status code: " + res.status);
                return;
            } else {
                res.json().then(data => {
                    const paths = segregateDataForEachUser(data);
                    paths.forEach(path => {
                        addUserPathToMap(map, path);
                    });
                    setTimeout(updateMap, 30000); // update every 30s
                });
            }
        })
        .catch(err => {
            console.log(err);
        });
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: segregateDataForEachUser
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:      segregateDataForEachUser(data)
--                      data: JSON array containing user locations
--
-- RETURNS: a Map containing all the locations goruped by the user name
-- 
-- NOTES:
-- Call this function to take the specified Google Map and update it with user locations. The user locations is fetched
-- from a JSON file that contains an array of all locations that are received from any Android clients that are connected
-- to the node server. Locations that have the same username will be drawn as one polyline on the map.
----------------------------------------------------------------------------------------------------------------------*/
const segregateDataForEachUser = data => {
    let pathMaps = new Map();
    data.forEach(locationInfo => {
        if (!pathMaps.has(locationInfo.userName)) {
            pathMaps.set(locationInfo.userName, []);
        }
        pathMaps.get(locationInfo.userName).push({
            lat: locationInfo.userLatitude,
            lng: locationInfo.userLongitude,
            time: locationInfo.timeStamp,
            user: locationInfo.userName,
            ip: locationInfo.userIP,
        });
    });
    return pathMaps;
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: addUserPathToMap
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:      addUserPathToMap(map, path)
--                      map: Google Map that will have the polylines drawn on it
--                      path: array containing the locations that will be used to draw the polyline onto the map
--
--
-- RETURNS: void.
-- 
-- NOTES:
-- Call this function to take an array of locations for a user and create a polyline for it. This polyline will then
-- be set onto the map that is passed in.
----------------------------------------------------------------------------------------------------------------------*/
const addUserPathToMap = (map, path) => {
    const strokeColor = "#" + ((Math.random() * 0xffffff) << 0).toString(16); // generate random hex color
    path.forEach(location => {
        createMarker(map, location, strokeColor);
    });
    const userPath = new google.maps.Polyline({
        path: path,
        strokeColor: strokeColor,
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });
    userPath.setMap(map);
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: createMarker
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:      createMarker(map, location)
--                      map: Google Map that will have the markers added 
--                      location: a set of lat and lng values that will be changed 
--
--
-- RETURNS: void.
-- 
-- NOTES:
-- Call this function to create a google map Marker object from the specified location object containing the latitude
-- and longitude. The marker will then be drawn to the map and an InfoWindow containing the information of the location.
-- The fields described will be the username, the latitude, the longitude, and the timestamp for the location.
----------------------------------------------------------------------------------------------------------------------*/
const createMarker = (map, location, hexColorString) => {
    const info =
        `<div><h3>User: ${location.user}</h3>` +
        `<br>IP: ${location.ip}` +
        `<br>Timestamp: ${location.time}` +
        `<br>Latitude: ${location.lat}` +
        `<br>Longitude: ${location.lng}</div>`;

    const marker = new google.maps.Marker({
        position: new google.maps.LatLng(location.lat, location.lng),
        icon: {
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: hexColorString,
            fillOpacity: 0.6,
            strokeColor: hexColorString,
            strokeOpacity: 0.9,
            strokeWeight: 1,
            scale: 2,
        },
    });
    const infowindow = new google.maps.InfoWindow({
        content: info,
    });
    marker.addListener("click", () => {
        infowindow.open(map, marker);
    });
    marker.setMap(map);
};

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: refreshMap
--
-- DATE: March 16, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Michael Yu
--
-- PROGRAMMER: Michael Yu
--
-- INTERFACE:       refreshMap()
--
-- RETURNS: void.
-- 
-- NOTES:
-- Refreshes a Goole Map instance by redrawing it.
----------------------------------------------------------------------------------------------------------------------*/
const refreshMap = () => {
    initMap();
};
