var express = require('express');
var router = express.Router();
var https = require('https');


function getWeatherCurrentLocation(req, res) {
    console.log("getWeatherCurrentLoc request receieved!");

    const lon = req.query.lon;
    const lat = req.query.lat;

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
}


/* GET weather data*/
router.get('/', function(req, res) {
    getWeatherCurrentLocation(req, res);
});

module.exports = router;