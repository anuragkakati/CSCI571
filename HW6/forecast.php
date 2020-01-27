<html>

<head>
    <meta charset="utf-8">
    <style>
        .weatherSearch {
            background-color: #0fc021;
            height: 220px;
            width: 750px;
            margin-top: 40px;
            margin-right: auto;
            margin-left: auto;
            border-radius: 10px;
        }

        .title {
            height: 40px;
            text-align: center;
            color: white;
            /*            font-family: "Times New Roman";*/
            font-size: 20px;
        }

        .form {
            color: white;
            margin-left: 50px;
            margin-top: 10px;
            font-size: 19px;
        }

        .vl {
            border-left: 5px solid white;
            height: 100px;
            margin-left: 362px;
            margin-top: -143px;
            /*            border-radius: 3px;*/
        }

        .buttons {
            margin-left: 225px;
            margin-top: 100px;
        }

        .alert {
            border: 3px solid #aaaaaa;
            width: 400px;
            height: 27px;
            text-align: center;
            margin-left: auto;
            margin-right: auto;
            margin-top: 37px;
            background-color: #f0f0f0;
        }

        .alert p {
            margin-top: 4px;
            vertical-align: center;
            font-size: 23px;
        }

        .currloc {
            color: white;
            font-size: 19px;
        }

        .checkbox {
            margin-left: 490px;
            margin-top: -72px;
        }

        .currently {
            background-color: #5cc3f3;
            width: 480px;
            height: 300px;
            margin-left: auto;
            margin-right: auto;
            margin-top: 25px;
            color: white;
            border-radius: 10px;
        }

        table,
        th,
        td {
            border: 2px solid #299eca;
            border-collapse: collapse;
            background-color: #9fc9ee;
            text-align: center;
            font-weight: bold;
        }

        .daily_table {
            margin-left: auto;
            margin-right: auto;
            color: white;
            margin-top: 25px;
            text-align: center;
        }

        .icon {
            text-align: center;
            float: left;
            width: 16.66%;
            font-weight: bold;
            font-size: 22px;
        }

        .currently2 {
            background-color: #a7d0d9;
            width: 580px;
            height: 480px;
            margin-left: auto;
            margin-right: auto;
            margin-top: -22px;
            color: white;
            border-radius: 10px;
            font-weight: bold;
        }

        .keys2 {
            font-size: 20px;
        }

        .values2 {
            font-size: 30px;
        }

        .graph {
            margin-left: auto;
            margin-right: auto;
            width: 740px;
        }

    </style>

    <?php
    //  Global variables
        global $street;
        global $city;
        global $state;
        global $lon;
        global $lat;
        global $weather_data1;
        global $weather_data2;
        global $isChecked;
        global $pageNumber;
        global $zero_results;
    
        $GapiKey = "AIzaSyC38RRupwg9zVFMWjhsTi3REcKXxHG5YfY";
        $darkskyKey = "2219b4b59d97a1bd374872870bde5a90";
    
        $pageNumber = 0;
        $lon = 0;
        $lat = 0;
        $weather_data1 = '';
        $weather_data2 = '';
        $isChecked = false;
        $darkskyAPI_URL1 = '';
        $darkskyAPI_URL2 = '';
        $zero_results = false;    
    
        if (isset($_POST["currentLocation"])) {
            // Current location checkbox checked. Get hosts longitude and latitude already set via form
            $isChecked = true;
            $city = $_POST["currentCity"];
            $lon = $_POST["longitude"];
            $lat = $_POST["latitude"];
        } else if (isset($_POST["Search"])) {    
            // User provided address. Call geocode API to get longitude and latitude
            
            $isChecked = false;
            $street = $_POST["Street"];
            $city = $_POST["City"];
            $state = $_POST["State"];
            
            $url = "https://maps.googleapis.com/maps/api/geocode/xml?address=".rawurlencode("$street, $city, $state")."&key=$GapiKey";
            
            $response = file_get_contents($url);
            $xmldata = simplexml_load_string($response);
            $json = json_encode($xmldata);
            if (json_decode($json, true)['status'] == 'ZERO_RESULTS') {
                $zero_results = true;
                $pageNumber = 1;
            } 
            else {
                $location = json_decode($json, true)['result']['geometry']['location'];
                $lon = $location['lng'];
                $lat = $location['lat'];   
            }
        }
        if (isset($_POST["Search"])) {
            // Need to display 1st page. User form submitted
            $pageNumber = 1;
            $darkskyAPI_URL1 = "https://api.forecast.io/forecast/$darkskyKey/$lat,$lon?exclude=minutely,hourly,alerts,flags";
            $weather_data1 = json_encode(file_get_contents($darkskyAPI_URL1));

        } else if (isset($_POST["date"])) {
            // User clicked on summary to see more details. Manual form submission
            $lon = $_POST["longitude"];
            $lat = $_POST["latitude"];
            
            $street = $_POST["Street"];
            if (isset($_POST["currentLocation"])) {
                $city = $_POST["currentCity"];
            } else {
                $city = $_POST["City"];
            }
            
            $state = $_POST["State"];
            $date = $_POST["date"];
            $pageNumber = 2;
            $darkskyAPI_URL2 = "https://api.forecast.io/forecast/$darkskyKey/$lat,$lon,$date?exclude=minutely";
            $weather_data2 = json_encode(file_get_contents($darkskyAPI_URL2));

        }
    
    ?>
</head>

<body>
    <div class="weatherSearch">
        <div class="title">
            <h1><i style="padding-top:10px;">Weather Search</i></h1>
        </div>

        <div class="form">
            <form id="myForm" method="post" action="<?= $_SERVER['PHP_SELF']; ?>">
                <b>Street</b> <input id="street" size="17" name="Street" type="text" style="margin-top:6px;"><br>
                <b>City</b> &nbsp; &nbsp;<input id="city" size="17" name="City" type="text" style="margin-top:6px; margin-left:-1px;"><br>
                <b>State</b> <select id="state" name="State" style="margin-top:6px; width:220px;">
                    <option selected=selected value="blank">State</option>
                    <option value="hyphen">------------------------------------------</option>
                    <option value="AL">Alabama</option>
                    <option value="AK">Alaska</option>
                    <option value="AZ">Arizona</option>
                    <option value="AR">Arkansas</option>
                    <option value="CA">California</option>
                    <option value="CO">Colorado</option>
                    <option value="CT">Connecticut</option>
                    <option value="DE">Delaware</option>
                    <option value="DC">District Of Columbia</option>
                    <option value="FL">Florida</option>
                    <option value="GA">Georgia</option>
                    <option value="HI">Hawaii</option>
                    <option value="ID">Idaho</option>
                    <option value="IL">Illinois</option>
                    <option value="IN">Indiana</option>
                    <option value="IA">Iowa</option>
                    <option value="KS">Kansas</option>
                    <option value="KY">Kentucky</option>
                    <option value="LA">Louisiana</option>
                    <option value="ME">Maine</option>
                    <option value="MD">Maryland</option>
                    <option value="MA">Massachusetts</option>
                    <option value="MI">Michigan</option>
                    <option value="MN">Minnesota</option>
                    <option value="MS">Mississippi</option>
                    <option value="MO">Missouri</option>
                    <option value="MT">Montana</option>
                    <option value="NE">Nebraska</option>
                    <option value="NV">Nevada</option>
                    <option value="NH">New Hampshire</option>
                    <option value="NJ">New Jersey</option>
                    <option value="NM">New Mexico</option>
                    <option value="NY">New York</option>
                    <option value="NC">North Carolina</option>
                    <option value="ND">North Dakota</option>
                    <option value="OH">Ohio</option>
                    <option value="OK">Oklahoma</option>
                    <option value="OR">Oregon</option>
                    <option value="PA">Pennsylvania</option>
                    <option value="RI">Rhode Island</option>
                    <option value="SC">South Carolina</option>
                    <option value="SD">South Dakota</option>
                    <option value="TN">Tennessee</option>
                    <option value="TX">Texas</option>
                    <option value="UT">Utah</option>
                    <option value="VT">Vermont</option>
                    <option value="VA">Virginia</option>
                    <option value="WA">Washington</option>
                    <option value="WV">West Virginia</option>
                    <option value="WI">Wisconsin</option>
                    <option value="WY">Wyoming</option>
                </select>

                <div class="checkbox">
                    <input id="currentLocation" type="checkbox" name="currentLocation" onclick="return fillCurrentAddress()"> <span class="currloc">Current Location</span>
                </div>
                <div class="buttons">
                    <input type="hidden" name="longitude" id="lon" value="">
                    <input type="hidden" name="latitude" id="lat" value="">
                    <input type="hidden" name="currentCity" id="currCity" value="">
                    <!--                    <input type="hidden" name="summary" id="summary" value="">-->
                    <input type="hidden" name="date" id="date" value="">
                    <input type="submit" name="Search" value="search" onclick="return validateData()">
                    <input type="reset" name="Reset" value="clear" onClick="return resetValues()">
                </div>
                <div class="vl"></div>
            </form>
        </div>
    </div>

    <div id="alert" hidden=true class="alert">
        <p>Please check the input address</p>
    </div>

    <div id="details1" hidden=true>
        <div id="currently" class="currently">

        </div>

        <div id="dailyTable">

        </div>
    </div>

    <div id="details2" hidden=true>
        <p align="center" style="font-size:36px; margin-top:28px;"><b>Daily Weather Detail</b></p>
        <div id="currently2" class="currently2">

        </div>
        <p align="center" style="font-size:36px; margin-top:28px;"><b>Day's Hourly Weather</b></p>
        <div id="arrow" style="text-align: center" ;>
            <a href="javascript:displayGraph()">
                <img id="arrowImage" src="https://cdn4.iconfinder.com/data/icons/geosm-e-commerce/18/point-down-512.png" height="50" width="50" style="margin-top: -30px;">
            </a>
        </div>
        <div id="graph" class="graph">

        </div>
    </div>

    <!--  HTML code over  -->

    <!--  Javescript code begins  -->
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script>
        // Decide which page to display based on pagenumber set by server
        window.onload = function() {
            var pageNumber = "<?php echo $pageNumber;?>";
            if (pageNumber == '1') {
                createPage1();
            } else if (pageNumber == '2') {
                createPage2();
            } else {
                return;
            }
        }

        // Callback to draw google chart
        function drawDailyGraph() {
            var data = new google.visualization.DataTable();
            data.addColumn('number', 'X');
            data.addColumn('number', 'T');

            var weatherDataGraph = JSON.parse(<?php echo $weather_data2;?>);
            var dailyTempData = weatherDataGraph.hourly.data;

            // Collect temperature data for 24 hours of the selected day
            var tempArray = new Array();
            for (var i = 0; i < dailyTempData.length; i++) {
                tempArray.push([i, dailyTempData[i].temperature]);
            }

            data.addRows(tempArray);

            var options = {
                hAxis: {
                    title: 'Time'
                },
                vAxis: {
                    title: 'Temperature',
                    textPosition: 'none'
                }
            };

            // Need to insert graph in the div with id='graph'
            var chart = new google.visualization.LineChart(document.getElementById('graph'));
            chart.draw(data, options);
        }

        // Event handler for clicking on arrow on 2nd page
        function displayGraph() {
            var imageTag = document.getElementById("arrowImage");

            if (imageTag.src.match("point-down")) {
                // Expand and display chart
                imageTag.src = "https://cdn0.iconfinder.com/data/icons/navigation-set-arrows-part-one/32/ExpandLess-512.png";
                document.getElementById('graph').hidden = false;
                google.charts.load('current', {
                    packages: ['corechart', 'line']
                });
                google.charts.setOnLoadCallback(drawDailyGraph);

            } else {
                // Collapse and hide the chart
                imageTag.src = "https://cdn4.iconfinder.com/data/icons/geosm-e-commerce/18/point-down-512.png";
                document.getElementById('graph').hidden = true;
            }
        }

        // Even handler when user clicks on summary to get more details of that day's weather
        function getSummary(datetime, city, lat, lon) {

            document.getElementById("date").value = datetime;
            document.getElementById("lon").value = lon;
            document.getElementById("lat").value = lat;
            document.getElementById("currCity").value = city;
            document.getElementById("myForm").submit();
        }

        function createPage1() {
            var isChecked = "<?php echo $isChecked;?>";

            var lon = "<?php echo $lon;?>";
            var lat = "<?php echo $lat;?>";
            var city = "<?php echo $city;?>";
            if (isChecked == false) {
                document.getElementById("street").value = "<?php echo $street;?>";
                document.getElementById("city").value = city;
                document.getElementById("state").value = "<?php echo $state;?>";
                document.getElementById("lon").value = lon;
                document.getElementById("lat").value = lat;
            } else {
                document.getElementById('currentLocation').setAttribute('checked', 'checked');
                document.getElementById("lon").value = lon;
                document.getElementById("lat").value = lat;
                document.getElementById("currCity").value = city;
                disable();
            }

            var zeroResults = "<?php echo $zero_results;?>";

            if (zeroResults == true) {
                document.getElementById("alert").hidden = false;
                return;
            }

            document.getElementById("details1").hidden = false;

            var weatherData = JSON.parse(<?php echo $weather_data1;?>);
            var city = "<?php echo $city?>";
            var page1Card = '<h1 style="font-size:32px; padding-top: 20px; margin-left:20px;">' + city + '</h1>';
            page1Card += '<div style="margin-top: -22px; margin-left:20px;">' + weatherData.timezone + '</div>';
            page1Card += '<div style="font-size:90px; margin-left:20px; margin-top:10px;"><b>' + Math.round(weatherData.currently.temperature);
            page1Card += '</b><img src="https://cdn3.iconfinder.com/data/icons/virtual-notebook/16/button_shape_oval-512.png" height="14" width="14" style="margin-bottom: 58px; margin-left:-3px;"><b style="font-size:40px;">  F</b></div>';
            page1Card += '<div style="margin-left:20px; font-size:30px; margin-top:-5px;"><b>' + weatherData.currently.summary + '</b></div>';
            page1Card += '<div style="margin-top:5px;">';
            page1Card += '<div class="icon"><div><img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-16-512.png" title="Humidity" height="30" width=30"><div>' +
                weatherData.currently.humidity + '</div></div></div>';
            page1Card += '<div class="icon"><div> <img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-25-512.png" title="Pressure" height="30" width=30"><div>' +
                weatherData.currently.pressure + '</div></div></div>';
            page1Card += '<div class="icon"><div> <img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-27-512.png" title="WindSpeed" height="30" width=30"><div>' +
                weatherData.currently.windSpeed + '</div></div></div>';
            page1Card += '<div class="icon"><div> <img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-30-512.png" title="Visibility" height="30" width=30"><div>' +
                weatherData.currently.visibility + '</div></div></div>';
            page1Card += '<div class="icon"><div> <img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-28-512.png" title="CloudCover" height="30" width=30"><div>' +
                weatherData.currently.cloudCover + '</div></div></div>';
            page1Card += '<div class="icon"><div> <img src="http://cdn2.iconfinder.com/data/icons/weather-74/24/weather-24-512.png" title="Ozone" height="30" width=30"><div>' +
                weatherData.currently.ozone + '</div></div></div>';
            page1Card += '</div>';

            var daily_weather_table = weatherData.daily.data;
            var table1_html = '<table class="daily_table"><tr>';
            table1_html += '<th>&nbsp;Date&nbsp;</th>';
            table1_html += '<th>&nbsp;Status&nbsp;</th>';
            table1_html += '<th>&nbsp;Summary&nbsp;</th>';
            table1_html += '<th>&nbsp;TemperatureHigh&nbsp;</th>';
            table1_html += '<th>&nbsp;TemperatureLow&nbsp;</th>';
            table1_html += '<th>&nbsp;Wind Speed&nbsp;</th>';
            table1_html += '</tr>';

            for (var i = 0; i < daily_weather_table.length; i++) {
                var daily_data = daily_weather_table[i];
                var time = new Date(daily_data.time * 1000);
                var yyyy = time.getFullYear();
                var mm;
                if ((time.getMonth() + 1) < 10) {
                    mm = "0" + (time.getMonth() + 1);
                } else {
                    mm = (time.getMonth() + 1);
                }
                var dd;
                if (time.getDate() < 10) {
                    dd = "0" + time.getDate();
                } else {
                    dd = time.getDate();
                }

                var dateString = yyyy + "-" + mm + "-" + dd;
                var status_url = "http://cdn2.iconfinder.com/data/icons/weather-74/24/";
                var status = daily_data.icon;
                var icons = '';
                var summary = daily_data.summary;
                var tempHigh = daily_data.temperatureHigh;
                var tempLow = daily_data.temperatureLow;
                var wind = daily_data.windSpeed;

                if (status == "clear-day" || status == "clear-night") {
                    icons = "weather-12-512.png";
                } else if (status == "rain") {
                    icons = "weather-04-512.png";
                } else if (status == "snow") {
                    icons = "weather-19-512.png";
                } else if (status == "sleet") {
                    icons = "weather-07-512.png";
                } else if (status == "wind") {
                    icons = "weather-27-512.png";
                } else if (status == "fog") {
                    icons = "weather-28-512.png";
                } else if (status == "cloudy") {
                    icons = "weather-01-512.png";
                } else if (status == "partly-cloudy-day" || status == "partly-cloudy-night") {
                    icons = "weather-02-512.png";
                }

                table1_html += '<tr><td style="font-size:15px;">&nbsp;' + dateString + '&nbsp;</td>';
                table1_html += '<td><img src=' + status_url + icons + ' height="40" width="40" ></td>';
                table1_html += '<td>&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:getSummary(' + daily_data.time + ',' + "'" + city + "'" + "," + lat + "," + lon + ')" style="text-decoration: none; color: white;">' + summary + '</a>&nbsp;&nbsp;&nbsp;&nbsp;</td>';
                table1_html += '<td>' + tempHigh + '</td>';
                table1_html += '<td>' + tempLow + '</td>';
                table1_html += '<td>' + wind + '</td>';
                table1_html += '</tr>';
            }
            table1_html += '</table>';
            document.getElementById("currently").innerHTML = page1Card;
            document.getElementById("dailyTable").innerHTML = table1_html;

        }

        function createPage2() {

            var isChecked = "<?php echo $isChecked;?>";

            var lon = "<?php echo $lon;?>";
            var lat = "<?php echo $lat;?>";
            if (isChecked == false) {
                document.getElementById("street").value = "<?php echo $street;?>";
                document.getElementById("city").value = "<?php echo $city;?>";
                document.getElementById("state").value = "<?php echo $state;?>";
            } else {
                document.getElementById('currentLocation').setAttribute('checked', 'checked');
                document.getElementById("lon").value = lon;
                document.getElementById("lat").value = lat;
                document.getElementById("currCity").value = "<?php echo $city;?>";
                disable();
            }

            weatherData = JSON.parse(<?php echo $weather_data2;?>);
            document.getElementById("details2").hidden = false;

            var status = weatherData.currently.icon;
            var statusURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/';

            if (status == 'clear-day' || status == 'clear-night') {
                statusURL += 'sun-512.png';
            } else if (status == 'rain') {
                statusURL += 'rain-512.png';
            } else if (status == 'snow') {
                statusURL += 'snow-512.png';
            } else if (status == 'sleet') {
                statusURL += 'lightning-512.png';
            } else if (status == 'wind') {
                statusURL = 'https://cdn4.iconfinder.com/data/icons/the-weather-is-nice-today/64/weather_10-512.png';
            } else if (status == 'fog') {
                statusURL += 'cloudy-512.png';
            } else if (status == 'cloudy') {
                statusURL += 'cloud-512.png';
            } else if (status == 'partly-cloudy-day' || status == 'partly-cloudy-night') {
                statusURL += 'sunny-512.png';
            }

            var precipitation = weatherData.currently.precipIntensity;
            if (precipitation <= 0.001) {
                precipitation = 'None';
            } else if (precipitation <= 0.015) {
                precipitation = 'Very Light';
            } else if (precipitation <= 0.05) {
                precipitation = 'Light';
            } else if (precipitation <= 0.1) {
                precipitation = 'Moderate';
            } else {
                precipitation = 'Heavy';
            }

            var rainProb = Math.round(weatherData.currently.precipProbability * 100);
            var windSpeed = weatherData.currently.windSpeed;
            var humidity = Math.round(parseFloat(weatherData.currently.humidity) * 100);
            var visibility = weatherData.currently.visibility;
            var timezone = weatherData.timezone;
            var sunrise = new Date(weatherData.daily.data[0].sunriseTime * 1000).toLocaleTimeString("en", {
                timeZone: timezone
            }).split(':')[0];
            var sunset = new Date(weatherData.daily.data[0].sunsetTime * 1000).toLocaleTimeString("en", {
                timeZone: timezone
            }).split(':')[0];

            page2Card = '<div style="margin-left: 25px;"><div style="float: left; width: 50%;"><p style="font-size:35px; padding-top: 90px; margin-top: -35px;">' + weatherData.currently.summary + '</p>';
            page2Card += '<div style="font-size:110px; margin-top: -40px;"><b>' + Math.round(parseInt(weatherData.currently.temperature));
            page2Card += '</b><img src="https://cdn3.iconfinder.com/data/icons/virtual-notebook/16/button_shape_oval-512.png" height="14" width="14" style="margin-bottom: 78px;"><b style="font-size:85px;">F</b></div></div>';
            page2Card += '<div style="float: left; text-align: center;  width: 50%;"><img src="' + statusURL + '" height="260" width="260" style="margin-left: -35px;"></div>';
            page2Card += '<div style="padding-right: 105px">';
            page2Card += '<div><span class="keys2" style="margin-left: 180px;">Precipitation:</span>&nbsp;<span class="values2">' + precipitation + '</span></div>';
            page2Card += '<div style="margin-top: -7px;"><span class="keys2" style="margin-left: 158px;">Chance of Rain:</span>&nbsp;<span class="values2">' + rainProb + '</span><span class="keys2">&nbsp;%</span></div>';
            page2Card += '<div style="margin-top: -7px;"><span class="keys2" style="margin-left: 187px;">Wind Speed:</span>&nbsp;<span class="values2">' + windSpeed + '</span><span class="keys2">&nbsp;mph</span></div>';
            page2Card += '<div style="margin-top: -7px;"><span class="keys2" style="margin-left: 208px;">Humidity:</span>&nbsp;<span class="values2">' + humidity + '</span><span class="keys2">&nbsp;%</span></div>';
            page2Card += '<div style="margin-top: -7px;"><span class="keys2" style="margin-left: 213px;">Visibility:</span>&nbsp;<span class="values2">' + visibility + '</span><span class="keys2">&nbsp;mi</span></div>';
            page2Card += '<div style="margin-top: -7px;"><span class="keys2" style="margin-left: 153px;">Sunrise&nbsp;/&nbsp;Sunset:</span>&nbsp;<span class="values2">' + sunrise + '<span class="keys2">&nbsp;AM</span>/&nbsp;' + sunset + '<span class="keys2">&nbsp;PM</span></span></div>';
            page2Card += '</div>';

            document.getElementById("currently2").innerHTML = page2Card;

        }

        function validateData() {
            if (document.getElementById("currentLocation").checked == true) {

                return true;
            }

            var city = document.getElementById("city");
            var street = document.getElementById("street");
            var state = document.getElementById("state");

            if (street.value == "" || city.value == "" || state.value == "blank" || state.value == "hyphen") {
                document.getElementById("alert").hidden = false;
                document.getElementById("details1").hidden = true;
                document.getElementById("details2").hidden = true;

                return false;
            } else {
                return true;
            }
        }

        function resetValues() {
            document.getElementById("myForm").reset();
            document.getElementById('currentLocation').removeAttribute('checked');
            document.getElementById("city").disabled = false;
            document.getElementById("street").disabled = false;
            document.getElementById("state").disabled = false;

            document.getElementById("alert").hidden = true;
            document.getElementById("details1").hidden = true;
            document.getElementById("details2").hidden = true;
            return true;
        }

        function disable() {
            document.getElementById("city").disabled = true;
            document.getElementById("street").disabled = true;
            document.getElementById("state").disabled = true;

            return true;
        }

        function enable() {
            document.getElementById("city").disabled = false;
            document.getElementById("street").disabled = false;
            document.getElementById("state").disabled = false;

            return true;
        }

        function fillCurrentAddress() {
            if (document.getElementById("currentLocation").checked == true) {

                // Disable the form
                document.getElementById("myForm").reset();
                document.getElementById('currentLocation').removeAttribute('checked');
                document.getElementById("city").disabled = false;
                document.getElementById("street").disabled = false;
                document.getElementById("state").disabled = false;

                disable();
                document.getElementById('currentLocation').setAttribute('checked', 'checked');

                var req = new XMLHttpRequest();
                req.open("GET", "http://ip-api.com/json", false);
                req.send();
                if (req.status == 200) {
                    var response = JSON.parse(req.responseText);
                    document.getElementById("lon").value = response.lon;
                    document.getElementById("lat").value = response.lat;
                    document.getElementById("currCity").value = response.city;
                } else {
                    console.log("request for ip-api failed!");
                }
            } else {
                enable();
            }

            return true;
        }

    </script>
</body>

</html>
