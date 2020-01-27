var express = require('express');
var router = express.Router();
var https = require('https');

function getDailyWeatherLocation(req, res) {
    console.log("getDailyWeatherLocation request receieved!");

    var timestamp = req.query.timestamp;
    var lon = req.query.lon;
    var lat = req.query.lat;

    const darkSkyKey = 'your_key';
    const darkSkyURL = 'https://api.darksky.net/forecast/' +
                        darkSkyKey + '/' + lat + ',' + lon + ',' + timestamp;
    console.log('daily URL : ' + darkSkyURL);
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

/* GET daily weather data*/
router.get('/', function(req, res) {
    getDailyWeatherLocation(req, res);
});

module.exports = router;