var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var https = require('https');
var http = require('http');
var cors = require('cors');

// Routes to include to handle different routes
var weatherRouter = require('./routes/weather');
var autocompleteRouter = require('./routes/autocomplete');
var stateSealRouter = require('./routes/stateSeal');
var weatherCurrentLocRouter = require('./routes/weatherCurrentLoc');
var dailyWeatherRouter = require('./routes/dailyWeather');
var cityImagesRouter = require('./routes/cityImages');

var app = express();

// For deployment
__dirname = 'dist/myWeatherApp';
app.use(express.static(__dirname));

// Add CORS
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    //res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static('node_modules'));
//app.use(express.static(path.join(__dirname, 'public')));

// Routes usage
app.use('/api/getWeather', weatherRouter);
app.use('/api/getAutocomplete', autocompleteRouter);
app.use('/api/getStateSeal', stateSealRouter);
app.use('/api/getWeatherCurrentLoc', weatherCurrentLocRouter);
app.use('/api/getDailyWeather', dailyWeatherRouter);
app.use('/api/getCityImages', cityImagesRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

var port = process.env.PORT || 5000;

app.listen(port, function () {
    console.log('Server running at http://localhost:' + port + '/');
});

module.exports = app;
