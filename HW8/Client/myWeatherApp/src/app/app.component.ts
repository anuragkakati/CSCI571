import {
  NgModule,
  Component,
  OnInit
} from '@angular/core';

import {
  ReactiveFormsModule,
  FormsModule,
  FormGroup,
  FormControl,
  Validators,
  FormBuilder
} from '@angular/forms';

import { RestService } from './rest.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'myWeatherApp';

  public weatherData = null;
  public ipapiJSON = null;
  public stateSealJSON = null;
  public setBySearch = false;

  public lon;
  public lat;
  public city;
  public street;
  public state;

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

  formControlForAuto = new FormControl();
  public autocompleteJSON;
  cityList = [];
  public sealURL = null;
  url: any;

  // noinspection JSAnnotator
  constructor(private restService: RestService, private http: HttpClient) { }

  isDisabled: boolean;
  isChecked(checkEvent) {
    if (checkEvent.target.checked) {
      this.isDisabled = true;
      this.formControlForAuto.disable();
    } else {
      this.isDisabled = false;
      this.formControlForAuto.enable();
    }
  }

  searchDisabled() {
    const city = ( <HTMLInputElement>document.getElementById('inputCity')).value;
    const street = ( <HTMLInputElement>document.getElementById('inputStreet')).value;
    const selectOption = ( <HTMLInputElement>document.getElementById('inputState')).value;

    const checkbox = ( <HTMLInputElement>document.getElementById('currentLocation')).checked;

    if (checkbox) {
      return false;
    } else if (city.length === 0 || street.length === 0) {
      return true;
    }

    return false;
  }

  selectValue(state) {
    console.log('selected state : ' + state);
    this.state = state;
  }

  getWeather(searchEvent) {
    this.setBySearch = true;
    if (this.isDisabled) {
      // Checkbox checked. Call ip-api
      const ipapiURL = 'http://ip-api.com/json';
      this.http.get(ipapiURL)
        .subscribe(data => {
          this.ipapiJSON = data;
          console.log('checkbox checked');
          this.lon = this.ipapiJSON.lon;
          this.lat = this.ipapiJSON.lat;
          this.city = this.ipapiJSON.city;
          this.state = this.ipapiJSON.region;
          console.log('ip-api state : ' + this.state);
          this.restService.changeCity(this.city);
          this.restService.changeState(this.state);
          this.restService.getWeatherCurrentLocJson(this.lon, this.lat, this.city)
            .subscribe(weatherData => {
              this.weatherData = weatherData;
              this.restService.changeWeatherJSON(this.weatherData);
              this.setBySearch = false;
              console.log(this.weatherData);
            });
          this.getStateSeal(this.stateJSON[this.state]);
        });
    } else {
      // User given address. Call backend
      // const city = document.getElementById('inputCity');
      const city = ( <HTMLInputElement>document.getElementById('inputCity'));
      const street = ( <HTMLInputElement>document.getElementById('inputStreet'));
      // const selectOption = ( <HTMLInputElement>document.getElementById('inputState'));

      this.city = city.value;
      this.street = street.value;
      // this.state = selectOption.value;

      console.log('street : ' + this.street);
      console.log('city : ' + this.city);
      console.log('state : ' + this.state);

      this.restService.changeCity(this.city);
      this.restService.changeState(this.state);
      this.restService.getWeatherJson(this.street, this.city, this.state)
        .subscribe(data => {
          this.weatherData = data;
          this.restService.changeWeatherJSON(this.weatherData);
          console.log('checkbox NOT checked');
          this.lon = this.weatherData.longitude;
          this.lat = this.weatherData.latitude;
          this.getStateSeal(this.stateJSON[this.state]);
          console.log(this.weatherData);
          this.setBySearch = false;
        });
    }

  }

  getStateSeal(state: string) {
    console.log('getStateSeal state : ' + state);
    this.restService.getStateSealJson(state)
      .subscribe(stateSealData => {
        this.stateSealJSON = stateSealData;
        // this.restService.changeSealURL(this.stateSealJSON.items.link);
        this.restService.changeStateSealJSON(this.stateSealJSON);
      });
  }

  resetValues(resetEvent) {
    this.isDisabled = false;
    this.formControlForAuto.reset();
    this.formControlForAuto.enable();
  }

  ngOnInit(): void {
    this.restService.sealURL.subscribe(url => {
      this.url = url;
    });


    this.formControlForAuto.valueChanges.subscribe(entry => {
      if (entry !== '') {
        this.restService.getAutoCompleteCities(entry).subscribe(data => {
          this.autocompleteJSON = data ;
          console.log(this.autocompleteJSON);
          for (let i = 0; i < this.autocompleteJSON.predictions.length; ++i) {
            this.cityList[i] = this.autocompleteJSON.predictions[i].structured_formatting.main_text;
          }
        });
      }
    });
  }
}
