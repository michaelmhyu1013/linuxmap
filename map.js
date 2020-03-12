var initMap = () => {
    var vancouver = { lat: 49.2487419, lng: -123.003096 };
    var map = new google.maps.Map(document.getElementById("map"), {
        zoom: 11,
        center: vancouver,
    });
    var marker = new google.maps.Marker({
        position: vancouver,
        map: map,
    });
    var userPath = new google.maps.Polyline({
        path: userCoordinates,
        strokeColor: "#FF0000",
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });

    userPath.setMap(map);
    // updateMap();
};

var userCoordinates = [
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
];

const updateMap = () => {
    console.log("updating map");
    fetch("https://locationtracer1.herokuapp.com/locations")
        .then(res => {
            if (res.status !== 200) {
                console.log("Error. Status code: " + res.status);
                return;
            } else {
                res.json().then(data => {
                    console.log(data);
                    // update map with newly fetched latlngs. should return an array of locations

                    // paths = segregateDataForEachUser(data);
                    // paths.forEach(path => {
                    //     addUserPathToMap(path);
                    // });

                    setTimeout(updateMap, 1000);
                });
            }
        })
        .catch(err => {
            console.log(err);
        });
};

const segregateDataForEachUser = data => {
    // map the data into an array of arrays. each inner array containing the locations for one user [userID: [loc1, loc2], userID2: [loc1, loc2...]]
};

const addUserPathToMap = path => {
    var userPath = new google.maps.Polyline({
        path: path,
        strokeColor: "#FF0000",
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });

    userPath.setMap(map);
};
