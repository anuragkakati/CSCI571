var express = require('express');
var router = express.Router();
var https = require('https');


function getStateSeal(req, res) {
    console.log("Request for stateSeal received!");

    const state = req.query.state;
    const searchEngineID = '011782248725805539749:9pp0oqo5gib';
    const placesKey = 'AIzaSyBfjNSPYzkff1Bk0Lqej2LIpI3fLCwULEk';
    const stateSealURL = 'https://www.googleapis.com/customsearch/v1?q='
        + state
        +'%20State%20Seal&cx='
        + searchEngineID
        + '&imgSize=large&imgType=news&num=1&searchType=image&key='
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
    getStateSeal(req, res);
});

module.exports = router;