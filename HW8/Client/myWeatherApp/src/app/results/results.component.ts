import { Component, OnInit } from '@angular/core';
import {RestService} from '../rest.service';
import * as CanvasJS from '../../assets/canvasjs.min.js';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit {

  public weatherData: any;
  public stateSealData: any;
  public city: string;
  public state: string;

  // For graphs Hourly
  public barChartData = [];
  public barChartOptions;
  public barChartLabels = [];
  public barChartLegend;
  public barChartType;
  chartDataThere = false;

  // For daily modal data
  public dailyWeatherData;
  public dateObj;
  public dateString;
  public modalTemperature;
  public modalSummray;
  public modalIconURL;
  public modalPrecip;
  public modalRain;
  public modalWindSpeed;
  public modalHumidity;
  public modalVisibility;

  // For Progress bar
  public showProgressBar: boolean;

  constructor(private restService: RestService) {
    this.showProgressBar = true;
  }

  ngOnInit() {
    this.restService.weatherJSON.subscribe(wResult => {
      this.weatherData = wResult;
    });

    this.restService.city.subscribe(city => {
      this.city = city;
    });

    this.restService.stateSealJSON.subscribe(sResult => {
      this.stateSealData = sResult;
    });

    this.restService.state.subscribe(state => {
      this.state = state;

      this.showProgressBar = false;
    });
  }

  // Sleep function
  delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
  }

  // Methods for current block
  getTemperature() {
    return Math.round(parseFloat(this.weatherData.currently.temperature));
    // this.initialChartData();
  }

  getTimezone() {
    return this.weatherData.timezone;
  }

  getSummary() {
    return this.weatherData.currently.summary;
  }

  getSealURL() {
    return this.stateSealData.items[0].link;
  }

  getHumidity() {
    return this.weatherData.currently.humidity;
  }

  getPressure() {
    return this.weatherData.currently.pressure;
  }

  getWindSpeed() {
    return this.weatherData.currently.windSpeed;
  }

  getVisibility() {
    return this.weatherData.currently.visibility;
  }

  getCloudCover() {
    return this.weatherData.currently.cloudCover;
  }

  getOzone() {
    return this.weatherData.currently.ozone;
  }

  // Methods for Hourly Graph
  initialChartData() {
    console.log('Initial chart data');
    const option = 'Temperature';

    this.barChartLegend = true;
    this.barChartType = 'bar';
    let dataArray = [];
    let labelStr;

    for (let i = 0; i < 24; i++) {
      this.barChartLabels[i] = i;
    }
    dataArray = this.getTemperatureData();
    labelStr = 'Fahrenheit';

    this.barChartOptions = {
      scaleShowVerticalLines: false,
      responsive: true,
      legend: {
        onClick: (e) => e.stopPropagation()
      },
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: false,
            callback: function(value) {if (value % 1 === 0) { return value; }},
            suggestedMax: Math.max(...dataArray) + 2,
            suggestedMin: Math.min(...dataArray) - 4
          },
          scaleLabel: {
            display: true,
            labelString: labelStr
          }
        }]
      }
    };

    this.barChartData = [{
      data : dataArray,
      label: option,
      backgroundColor: '#89d0f2',
      borderColor: '#89d0f2',
      hoverBackgroundColor: '#89d0f2',
      hoverBorderColor: '#89d0f2'
    }];

    this.chartDataThere = true;
  }

  changeChartData() {
    const option = ( <HTMLInputElement>document.getElementById('inputParam')).value;

    this.barChartLegend = true;
    this.barChartType = 'bar';
    let dataArray = [];
    let labelStr;
    let sugMax;
    let sugMin;
    for (let i = 0; i < 24; i++) {
      this.barChartLabels[i] = i;
    }

    if (option === 'Temperature') {
      dataArray = this.getTemperatureData();
      labelStr = 'Fahrenheit';
      sugMax = Math.max(...dataArray) + 2;
      sugMin = Math.min(...dataArray) - 4;
    } else if (option === 'Pressure') {
      dataArray = this.getPressureData();
      labelStr = 'Millibars';
      sugMax = Math.max(...dataArray) + 2;
      sugMin = Math.min(...dataArray) - 4;
    } else if (option === 'Humidity') {
      dataArray = this.getHumidityData();
      labelStr = '% Humidity';
      sugMax = Math.max(...dataArray) + 5;
      sugMin = Math.min(...dataArray) - 10;
    } else if (option === 'Ozone') {
      dataArray = this.getOzoneData();
      labelStr = 'Dobson Units';
      sugMax = Math.max(...dataArray) + 5;
      sugMin = Math.min(...dataArray) - 10;
    } else if (option === 'Visibility') {
      dataArray = this.getVisibilityData();
      labelStr = 'Miles (Maximum 10)';
      sugMax = Math.max(...dataArray) + 2;
      sugMin = Math.min(...dataArray) - 4;
    } else {
      dataArray = this.getWindSpeedData();
      labelStr = 'Miles per hour';
      sugMax = Math.max(...dataArray) + 2;
      sugMin = Math.min(...dataArray) - 2;
    }

    this.barChartOptions = {
      scaleShowVerticalLines: false,
      responsive: true,
      legend: {
        onClick: (e) => e.stopPropagation()
      },
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: false,
            callback: function(value) {if (value % 1 === 0) {return value;}},
            suggestedMax: sugMax,
            suggestedMin: sugMin
          },
          scaleLabel: {
            display: true,
            labelString: labelStr
          }
        }]
      }
    };

    this.barChartData = [{
      data : dataArray,
      label: option,
      backgroundColor: '#98cfef',
      borderColor: '',
      hoverBackgroundColor: '#98cfef',
      hoverBorderColor: '#98cfef'
    }];
  }

  getTemperatureData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].temperature));
    }

    return dataArray;
  }

  getPressureData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].pressure));
    }

    return dataArray;
  }

  getHumidityData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].humidity) * 100);
    }

    return dataArray;
  }

  getOzoneData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].ozone));
    }

    return dataArray;
  }

  getVisibilityData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].visibility));
    }

    return dataArray;
  }

  getWindSpeedData() {
    const dataArray = [];
    for (let i = 0; i < 24; i++) {
      dataArray[i] = Math.round(parseFloat(this.weatherData.hourly.data[i].windSpeed));
    }

    return dataArray;
  }

  // For Weekly range bar chart
  rangeChart() {
    const date0 = new Date(this.weatherData.daily.data[0].time * 1000);
    const date1 = new Date(this.weatherData.daily.data[1].time * 1000);
    const date2 = new Date(this.weatherData.daily.data[2].time * 1000);
    const date3 = new Date(this.weatherData.daily.data[3].time * 1000);
    const date4 = new Date(this.weatherData.daily.data[4].time * 1000);
    const date5 = new Date(this.weatherData.daily.data[5].time * 1000);
    const date6 = new Date(this.weatherData.daily.data[6].time * 1000);

    const label0 = date0.getUTCDate() + '/' + date0.getUTCMonth() + '/' + date0.getUTCFullYear();
    const label1 = date1.getUTCDate() + '/' + date1.getUTCMonth() + '/' + date1.getUTCFullYear();
    const label2 = date2.getUTCDate() + '/' + date2.getUTCMonth() + '/' + date2.getUTCFullYear();
    const label3 = date3.getUTCDate() + '/' + date3.getUTCMonth() + '/' + date3.getUTCFullYear();
    const label4 = date4.getUTCDate() + '/' + date4.getUTCMonth() + '/' + date4.getUTCFullYear();
    const label5 = date5.getUTCDate() + '/' + date5.getUTCMonth() + '/' + date5.getUTCFullYear();
    const label6 = date6.getUTCDate() + '/' + date6.getUTCMonth() + '/' + date6.getUTCFullYear();

    const chart = new CanvasJS.Chart('chartContainer', {
      animationEnabled: true,

      title: {
        text: 'Weekly Weather',
        fontSize: 35
      },

      dataPointWidth: 15,

      axisX: {
        title: 'Days'
      },

      axisY: {
        includeZero: false,
        gridThickness: 0,
        title: 'Temperature in Fahrenheit',
        interval: 10
      },

      // width: 1000,
      height: 300,
      legend: {
        verticalAlign: 'top'
      },

      data: [{
        type: 'rangeBar',
        color: '#98cfef',
        showInLegend: true,
        indexLabel: '{y[#index]}',
        legendText: 'Day wise temperature range',
        toolTipContent: '<b>{label}</b>: {y[0]} to {y[1]}',
        dataPoints: [
          { x: 10, y: [Math.round(parseFloat(this.weatherData.daily.data[6].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[6].temperatureHigh))], label: label6,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 20, y: [Math.round(parseFloat(this.weatherData.daily.data[5].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[5].temperatureHigh))], label: label5,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 30, y: [Math.round(parseFloat(this.weatherData.daily.data[4].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[4].temperatureHigh))], label: label4,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 40, y: [Math.round(parseFloat(this.weatherData.daily.data[3].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[3].temperatureHigh))], label: label3,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 50, y: [Math.round(parseFloat(this.weatherData.daily.data[2].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[2].temperatureHigh))], label: label2,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 60, y: [Math.round(parseFloat(this.weatherData.daily.data[1].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[1].temperatureHigh))], label: label1,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }},
          { x: 70, y: [Math.round(parseFloat(this.weatherData.daily.data[0].temperatureLow)),
              Math.round(parseFloat(this.weatherData.daily.data[0].temperatureHigh))], label: label0,
              click: (e) => {
                this.showModal(e.dataPointIndex);
              }}
        ]
      }]
    });
    setTimeout(() => {
      chart.render();
    }, 500);
  }

  // For Modal
  showModal(index) {
    const timeStamp = this.weatherData.daily.data[6 - index].time;
    const lon = this.weatherData.longitude;
    const lat = this.weatherData.latitude;

    this.restService.getDailyWeatherData(timeStamp, lat, lon).subscribe(
      dailyData => {
        this.dailyWeatherData = dailyData;
        this.dateObj = new Date(this.dailyWeatherData.daily.data[0].time * 1000);
        this.dateString = this.dateObj.getUTCDate() + '/' + this.dateObj.getUTCMonth() + '/' + this.dateObj.getUTCFullYear();
        this.modalTemperature = Math.round(parseFloat(this.dailyWeatherData.currently.temperature));
        this.modalSummray = this.dailyWeatherData.currently.summary;

        const iconString = this.dailyWeatherData.currently.icon;
        if (iconString === 'clear-day' || iconString === 'clear-night') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/sun-512.png';
        } else if (iconString === 'rain') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/rain-512.png';
        } else if (iconString === 'snow') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/snow-512.png';
        } else if (iconString === 'sleet') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/lightning-512.png';
        } else if (iconString === 'wind') {
          this.modalIconURL = 'https://cdn4.iconfinder.com/data/icons/the-weather-is-nice-today/64/weather_10-512.png';
        } else if (iconString === 'fog') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/cloudy-512.png';
        } else if (iconString === 'cloudy') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/cloud-512.png';
        } else if (iconString === 'partly-cloudy-day' || iconString === 'partly-cloudy-night') {
          this.modalIconURL = 'https://cdn3.iconfinder.com/data/icons/weather-344/142/sunny-512.png';
        }

        this.modalPrecip = parseFloat(this.dailyWeatherData.currently.precipIntensity).toFixed(2);
        this.modalRain = Math.round(this.dailyWeatherData.currently.precipProbability * 100);
        this.modalWindSpeed = parseFloat(this.dailyWeatherData.currently.windSpeed).toFixed(2);
        this.modalHumidity = Math.round(this.dailyWeatherData.currently.humidity * 100);
        this.modalVisibility = this.dailyWeatherData.currently.visibility;

      });
    document.getElementById('modalButton').click();
  }

  // For tweet
  tweet() {
    const text = 'The current temperature at ' + this.city + 'is ' + this.weatherData.currently.temperature +
      '° F. The weather conditions are ' + this.weatherData.currently.summary + '&hashtags=CSCI571WeatherSearch';
    const tweetURL = 'https://twitter.com/intent/tweet?text=' + text;

    console.log('Twitter URL : ' + tweetURL);
    window.open(tweetURL, '_blank');
  }

  // For adding to favorite
  makeFavorite() {
    console.log('Adding city ' + this.city + ' to favorites');
    console.log('Adding state ' + this.state + ' to favorites');
    console.log('Adding URL ' + this.stateSealData.items[0].link + ' to favorites');

    if (document.getElementById('favButtonOff')) {
      document.getElementById('starButtonID').innerHTML = '<i class="material-icons" id="favButtonOn">star</i>';
    }

    if (document.getElementById('favButtonOn')) {
      document.getElementById('starButtonID').innerHTML = '<i class="material-icons" id="favButtonOff">star_border</i>';
    }

    const cityObject = {
      stateSealURL: this.stateSealData.items[0].link,
      city: this.city,
      state: this.state
    };

    localStorage.setItem(this.city, JSON.stringify(cityObject));
  }

}
