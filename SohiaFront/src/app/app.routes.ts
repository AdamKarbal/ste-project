import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RestaurantMenuPage } from './pages/restaurant-menu/restaurant-menu.page';
import {PaymentPage} from './pages/payment/payment.page';

export const routes: Routes = [
  // Page d’accueil
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'restaurant-menu/:name', component: RestaurantMenuPage },
  {
    path: 'payment/:orderId',
    component: PaymentPage
  }
];
