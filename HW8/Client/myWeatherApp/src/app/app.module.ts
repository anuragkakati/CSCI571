import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule} from './app-routing.module';
import { AppComponent } from './app.component';
import { RestService } from './rest.service';
import { RouterModule, Routes } from '@angular/router';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTooltipModule} from '@angular/material';
import { ChartsModule} from 'ng2-charts';
import { ResultsComponent } from './results/results.component';
import { FavoritesComponent } from './favorites/favorites.component';
@NgModule({
  declarations: [
    AppComponent,
    ResultsComponent,
    FavoritesComponent
    // routingComponents,
    // CurrentComponent,
    // HourlyComponent,
    // WeeklyComponent,
    // CombinedResultsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatTooltipModule,
    ChartsModule
  ],
  providers: [RestService],
  // providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
