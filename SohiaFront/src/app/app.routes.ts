import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing/landing';
import { HomeComponent } from './pages/home/home.component';
import { RestaurantMenuPage } from './pages/restaurant-menu/restaurant-menu.page';
import {RestaurantManagerPageComponent} from './pages/managing/managing';
import { RestaurantDetailPage } from './pages/restaurant-detail/restaurant-detail.page';
import {PaymentPage} from './pages/payment/payment.page';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'home', component: HomeComponent },

  // Page menu d’un restaurant (nom utilisé comme identifiant)
  { path: 'restaurant-menu/:name', component: RestaurantMenuPage },

  // Route de test optionnelle
  { path: 'test-menu', component: RestaurantMenuPage },

  {path : 'restaurant-manager/:name', component : RestaurantManagerPageComponent}
  {
    path: 'payment/:orderId',
    component: PaymentPage
  },
  { path: 'restaurant-detail/:name', component: RestaurantDetailPage },
  { path: '**', redirectTo: '' } // fallback
];
