import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing/landing';
import { HomeComponent } from './pages/home/home.component';
import { RestaurantMenuPage } from './pages/restaurant-menu/restaurant-menu.page';
import { RestaurantDetailPage } from './pages/restaurant-detail/restaurant-detail.page';
import {PaymentPage} from './pages/payment/payment.page';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'home', component: HomeComponent },
  { path: 'restaurant-menu/:name', component: RestaurantMenuPage },
  {
    path: 'payment/:orderId',
    component: PaymentPage
  },
  { path: 'restaurant-detail/:name', component: RestaurantDetailPage },
  { path: '**', redirectTo: '' } // fallback
];
