const resource = "/locations";

var initMap = () => {
    var vancouver = { lat: 49.2487419, lng: -123.003096 };
    var map = new google.maps.Map(document.getElementById("map"), {
        zoom: 11,
        center: vancouver,
    });
    updateMap(map);
};

var testData = new Map();
testData.set(0, {
    userID: 1,
    locations: [
        { lat: 40.759011, lng: -73.984472 },
        { lat: 40.916765, lng: -74.171811 },
        { lat: 41.320043, lng: -73.979287 },
        { lat: 41.503427, lng: -74.010418 },
        { lat: 40.4406, lng: -79.9959 },
        { lat: 41.878114, lng: -87.629798 },
        { lat: 41.515776, lng: -88.089981 },
        { lat: 41.603121, lng: -93.615418 },
        { lat: 41.614432, lng: -94.017453 },
        { lat: 41.506006, lng: -94.318428 },
        { lat: 41.264905, lng: -95.995216 },
        { lat: 40.922593, lng: -98.342114 },
        { lat: 41.134193, lng: -104.816437 },
        { lat: 39.755373, lng: -104.984837 },
    ],
});
testData.set(1, {
    userID: 1,
    locations: [
        { lat: 37.772, lng: -122.214 },
        { lat: 21.291, lng: -157.821 },
        { lat: -18.142, lng: 178.431 },
        { lat: -27.467, lng: 153.027 },
    ],
});

const updateMap = map => {
    fetch(resource)
        .then(res => {
            if (res.status !== 200) {
                console.log("Error. Status code: " + res.status);
                return;
            } else {
                res.json().then(data => {
                    paths = segregateDataForEachUser(data);
                    testData.forEach(path => {
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
        if (!pathMaps.has(locationInfo.userID)) {
            pathMaps.set(locationInfo.userID, []);
        }
        pathMaps.get(locationInfo.userID).push({
            lat: locationInfo.latitude,
            lng: locationInfo.longitude,
        });
    });
    return pathMaps;
};

const addUserPathToMap = (map, path) => {
    var userPath = new google.maps.Polyline({
        path: path.locations,
        strokeColor: "#" + ((Math.random() * 0xffffff) << 0).toString(16), // generate random hex color
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });

    userPath.setMap(map);
};
