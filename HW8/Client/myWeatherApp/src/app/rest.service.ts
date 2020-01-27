import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class RestService {

  private weatherJSONSource = new BehaviorSubject(null);
  private stateSealJSONSource = new BehaviorSubject(null);
  private citySource = new BehaviorSubject(null);
  private sealURLSource = new BehaviorSubject(null);
  private stateSource = new BehaviorSubject(null);

  public weatherJSON = this.weatherJSONSource.asObservable();
  public stateSealJSON = this.stateSealJSONSource.asObservable();
  public sealURL = this.sealURLSource.asObservable();
  public city = this.citySource.asObservable();
  public state = this.stateSource.asObservable();

  public localhostPath = '';

  constructor(private http: HttpClient) { }

  private extractWeatherData(res: Response) {
    return res;
  }

  private extractStateSealData(res: Response) {
    // console.log('stateSealURL : ' + res.items[0].link);
    return res;
  }

  private extractAutocompleteData(res: Response) {
    console.log('Autocomplete returning : ' + res);
    return res;
  }

  getWeatherJson(street: string, city: string, state: string): Observable<object> {
    console.log('getWeatherJson making rest API call');
    const url = this.localhostPath + '/api/getWeather?street=' + street + '&city=' + city + '&state=' + state;
    console.log('getWeatherURL : ' + url);
    return this.http.get(url).pipe(
      map(this.extractWeatherData));
  }

  getWeatherCurrentLocJson(lon: string, lat: string, city: string): Observable<object> {
    console.log('getWeatherCurrentLocJson making rest API call');
    const url = this.localhostPath + '/api/getWeatherCurrentLoc?lat=' + lat + '&lon=' + lon;
    console.log('URL : ' + url);
    return this.http.get(url).pipe(
      map(this.extractWeatherData));
  }

  getStateSealJson(state: any): Observable<object> {
    console.log('getStateSealJson making rest API call');
    const url = this.localhostPath + '/api/getStateSeal?state=' + state;
    console.log('StateSealURL : ' + url);
    return this.http.get(url).pipe(
      map(this.extractStateSealData));
  }

  getAutoCompleteCities(partialCity: string) {
    console.log('getAutoCompleteCities making rest API call');
    const url = this.localhostPath + '/api/getAutocomplete?input=' + partialCity;
    console.log('Autocomplete URL : ' + url);
    return this.http.get(url).pipe(
      map(this.extractAutocompleteData));
  }

  getDailyWeatherData(timestamp, lat, lon) {
    console.log('getDailyWeatherData making rest API call');
    const url = this.localhostPath + '/api/getDailyWeather?timestamp=' + timestamp + '&lat=' + lat + '&lon=' + lon;
    console.log('dailyWeatherURL : ' + url);
    return this.http.get(url).pipe(
      map(this.extractWeatherData));
  }

  changeWeatherJSON(weatherJSON) {
    console.log('Changing weatherJSON to : ' + weatherJSON);
    this.weatherJSONSource.next(weatherJSON);
  }

  changeStateSealJSON(stateSealJSON) {
    console.log('Changing stateSealJSON to : ' + stateSealJSON);
    this.stateSealJSONSource.next(stateSealJSON);
  }

  changeCity(city) {
    console.log('Changing city to : ' + city);
    this.citySource.next(city);
  }

  changeState(state) {
    console.log('Changing state to : ' + state);
    this.stateSource.next(state);
  }
}
