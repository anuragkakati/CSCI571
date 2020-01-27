import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ResultsComponent} from './results/results.component';
import {FavoritesComponent} from './favorites/favorites.component';


const routes: Routes = [
  {path: '', redirectTo: '', pathMatch: 'full'},
  {path: 'results', component: ResultsComponent},
  {path: 'favorites', component: FavoritesComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const routingComponents = [ResultsComponent]
