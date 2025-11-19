import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RestaurantMenuPage } from './pages/restaurant-menu/restaurant-menu.page';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'restaurant/:id/menu', component: RestaurantMenuPage },
  {
    path: 'test-menu',
    component: RestaurantMenuPage
  },
  { path: 'home', component: HomeComponent }
];
