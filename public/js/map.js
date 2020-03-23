"use strict";

let gmap;
const resource = "/locations";
const mapDiv = document.getElementById("map");
const vancouver = { lat: 49.2487419, lng: -123.003096 };
const options = {
    zoom: 11,
    center: vancouver,
};

var initMap = () => {
    gmap = new google.maps.Map(mapDiv, options);
    updateMap(gmap);
};

const refreshMap = () => {
    initMap();
};

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

const segregateDataForEachUser = data => {
    let pathMaps = new Map();
    data.forEach(locationInfo => {
        if (!pathMaps.has(locationInfo.userName)) {
            pathMaps.set(locationInfo.userName, []);
        }
        pathMaps.get(locationInfo.userName).push({
            lat: locationInfo.userLatitude,
            lng: locationInfo.userLongitude,
        });
    });
    return pathMaps;
};

const addUserPathToMap = (map, path) => {
    const userPath = new google.maps.Polyline({
        path: path,
        strokeColor: "#" + ((Math.random() * 0xffffff) << 0).toString(16), // generate random hex color
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });
    userPath.setMap(map);
};
