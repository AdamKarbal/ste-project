import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RestaurantMenuPage } from './pages/restaurant-menu/restaurant-menu.page';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },

  // Page d’accueil
  { path: 'home', component: HomeComponent },

  // Page menu d’un restaurant (nom utilisé comme identifiant)
  { path: 'restaurant-menu/:name', component: RestaurantMenuPage },

  // Route de test optionnelle
  { path: 'test-menu', component: RestaurantMenuPage },
];
