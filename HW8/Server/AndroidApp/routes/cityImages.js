var express = require('express');
var router = express.Router();
var https = require('https');

function getCityImages(req, res) {
    console.log("Request for getCityImages received!");
    const city = req.query.city;
    const searchEngineID = 'your_key:9pp0oqo5gib';
    const placesKey = 'your_key';
    const stateSealURL = 'https://www.googleapis.com/customsearch/v1?q='
        + city
        +'&cx='
        + searchEngineID
        + '&imgType=news&num=8&searchType=image&key='
        + placesKey;

    console.log("URL : " + stateSealURL);

    https.get(stateSealURL, function (data) {
        let jsonData = '';

        // A chunk of data has been received
        data.on('data', function (chunk) {
            jsonData += chunk;
        });

        // Whole response is read
        data.on('end', function () {
            jsonData = JSON.parse(jsonData);
            res.send(jsonData);
        });
    }).on('error', function (err) {
        console.log(err.message);
    });
}

router.get('/', function(req, res) {
    getCityImages(req, res);
});

module.exports = router;