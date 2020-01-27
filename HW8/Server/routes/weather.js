var express = require('express');
var router = express.Router();
var https = require('https');
// var request = require('request');

function getWeather(req, res) {

    console.log("getWeather request receieved!");

    // Extract user given inputs form request
    var street = req.query.street;
    var city = req.query.city;
    var state = req.query.state;

    const geocodeKey = 'AIzaSyC38RRupwg9zVFMWjhsTi3REcKXxHG5YfY';
    const geocodeURL = 'https://maps.googleapis.com/maps/api/geocode/json?address=' + street + ','+ city +
        ',' + state + '&key='+geocodeKey;

    // Gets lat and long of user address
    https.get(geocodeURL, function (data) {
        let jsonData = '';

        // A chunk of data has been received
        data.on('data', function (chunk) {
            jsonData += chunk;
        });

        // Whole response is read
        data.on('end', function () {
            jsonData = JSON.parse(jsonData);

            // Extract user lon and lat
            const lat = jsonData.results[0].geometry.location.lat;
            const lon = jsonData.results[0].geometry.location.lng;

            console.log(lon + "\t" + lat);

            // Call DarkSky API using user lon and lat
            const darkSkyKey = '2219b4b59d97a1bd374872870bde5a90';
            const darkSkyURL = 'https://api.darksky.net/forecast/' + darkSkyKey + '/' + lat + ',' + lon;

            console.log('darksky url : ' + darkSkyURL);
            // Make another HTTP.get call to get weather data
            https.get(darkSkyURL, function(weatherData) {
                let weatherJson = '';

                // A chunk of data has been received
                weatherData.on('data', function(weatherChunk) {
                    weatherJson += weatherChunk;
                });

                // Whole response is read
                weatherData.on('end', function () {
                    weatherJson = JSON.parse(weatherJson);

                    // Send whole weather Data to the client
                    res.send(weatherJson);
                });
            }).on('error', function (err) {
                console.log(err.message);
            });
        });
    }).on('error', function (err) {
        console.log(err.message);
    });
}

/* GET weather data*/
router.get('/', function(req, res) {
    getWeather(req, res);

    // request(url, {json:true}, function (err, resp, body) {
    //     if (err) {
    //         return console.log(err);
    //     }
    //
    //     const lat = body.results[0].geometry.location.lat;
    //     const lon = body.results[0].geometry.location.lng;
    //
    //     // console.log("User location : lon : " + lon + "\tlat : " + lat);
    //
    //     // resp.render("Weather data : " + lon + "\t" + lat);
    //     resp.send({"lon" : lon, "lat" : lat});
    // });
});

module.exports = router;
