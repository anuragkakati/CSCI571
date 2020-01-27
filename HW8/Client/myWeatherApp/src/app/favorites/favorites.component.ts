import { Component, OnInit } from '@angular/core';
import {RestService} from '../rest.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {

  public storedCityObjects = [];
  public cityKeys = [];
  constructor(private restService: RestService, private router: Router) {}

  // For rest-api call for state seal
  public stateJSON = {
    AL: 'Alabama',
    AK: 'Alaska',
    AS: 'American Samoa',
    AZ: 'Arizona',
    AR: 'Arkansas',
    CA: 'California',
    CO: 'Colorado',
    CT: 'Connecticut',
    DE: 'Delaware',
    DC: 'District Of Columbia',
    FM: 'Federated States Of Micronesia',
    FL: 'Florida',
    GA: 'Georgia',
    GU: 'Guam',
    HI: 'Hawaii',
    ID: 'Idaho',
    IL: 'Illinois',
    IN: 'Indiana',
    IA: 'Iowa',
    KS: 'Kansas',
    KY: 'Kentucky',
    LA: 'Louisiana',
    ME: 'Maine',
    MH: 'Marshall Islands',
    MD: 'Maryland',
    MA: 'Massachusetts',
    MI: 'Michigan',
    MN: 'Minnesota',
    MS: 'Mississippi',
    MO: 'Missouri',
    MT: 'Montana',
    NE: 'Nebraska',
    NV: 'Nevada',
    NH: 'New Hampshire',
    NJ: 'New Jersey',
    NM: 'New Mexico',
    NY: 'New York',
    NC: 'North Carolina',
    ND: 'North Dakota',
    MP: 'Northern Mariana Islands',
    OH: 'Ohio',
    OK: 'Oklahoma',
    OR: 'Oregon',
    PW: 'Palau',
    PA: 'Pennsylvania',
    PR: 'Puerto Rico',
    RI: 'Rhode Island',
    SC: 'South Carolina',
    SD: 'South Dakota',
    TN: 'Tennessee',
    TX: 'Texas',
    UT: 'Utah',
    VT: 'Vermont',
    VI: 'Virgin Islands',
    VA: 'Virginia',
    WA: 'Washington',
    WV: 'West Virginia',
    WI: 'Wisconsin',
    WY: 'Wyoming'
  };

  ngOnInit() {
    this.cityKeys = Object.keys(localStorage);
    for (let i = 0; i < this.cityKeys.length; i++) {
      this.storedCityObjects[i] = JSON.parse(localStorage.getItem(this.cityKeys[i]));
      console.log('storedCityObjects data : ' + this.storedCityObjects[i].city);
    }
  }

  // For removing from favorites
  public removeFromFavorites(cityToDelete) {
    console.log('Removing city ' + cityToDelete + ' from favorites');
    localStorage.removeItem(cityToDelete);
    this.updateStoredCities();
  }

  public updateStoredCities() {
    this.storedCityObjects = [];
    this.cityKeys = [];
    this.cityKeys = Object.keys(localStorage);

    for (let i = 0; i < this.cityKeys.length; i++) {
      this.storedCityObjects[i] = JSON.parse(localStorage.getItem(this.cityKeys[i]));
      console.log('storedCityObjects data : ' + this.storedCityObjects[i].city);
    }
  }

  public routeToResults(city, state) {
    // Make rest-api call to get weather data again

    this.restService.changeCity(city);
    this.restService.changeState(state);
    this.restService.getWeatherJson('', city, state).
      subscribe(wData => {
        this.restService.changeWeatherJSON(wData);
        this.restService.getStateSealJson(state).
          subscribe(sData => {
            this.restService.changeStateSealJSON(sData);
            this.router.navigate(['results']);
        });
    });
  }
}
