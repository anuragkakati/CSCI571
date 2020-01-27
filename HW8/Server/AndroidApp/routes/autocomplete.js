var express = require('express');
var router = express.Router();
var https = require('https');
var request = require('request');

function getAutocomplete(req, res) {
    console.log("Request for autoComplete received!");

    const input = req.query.input;
    const placesKey = 'your_key';
    const autocompleteURL = 'https://maps.googleapis.com/maps/api/place/autocomplete/json?input='
        + input + '&types=(cities)&language=en&key='
        + placesKey;

    https.get(autocompleteURL, function (data) {
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
    getAutocomplete(req, res);
});

module.exports = router;