import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ✅ Ajoute ceci
import { RestaurantService } from '../../services/restaurantService';
import { NavbarComponent } from '../../components/navbar/navbar';
import { LoaderComponent } from '../../components/loader/loader';
import { RestaurantCardComponent } from '../../components/restaurant-card/restaurant-card';
import { FooterComponent } from '../../components/footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule, // ✅ Et ici
    NavbarComponent,
    LoaderComponent,
    RestaurantCardComponent,
    FooterComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  restaurants: any[] = [];
  loading = true;
  searchTerm: string = ''; // ✅ déjà ajouté

  constructor(private restaurantService: RestaurantService) {}

  async ngOnInit() {
    try {
      this.restaurants = await this.restaurantService.getAllRestaurants();
      console.log('✅ Restaurants chargés :', this.restaurants);
    } finally {
      this.loading = false;
    }
  }


  onSearch() {
    console.log('🔍 Recherche déclenchée :', this.searchTerm);
  }
}
