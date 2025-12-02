import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/authService';
import { RestaurantService } from '../../services/restaurantService';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FooterComponent } from '../../components/footer/footer';

@Component({
  selector: 'app-restaurant-detail',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FooterComponent],
  templateUrl: './restaurant-detail.page.html',
  styleUrl: './restaurant-detail.page.css',
})
export class RestaurantDetailPage implements OnInit {

  restaurantName = '';
  restaurant: any = null; // ⭐ Restaurant complet
  isLoggedIn = false;
  loading = true;

  tagLabels: Record<string, string> = {
    GLUTEN_FREE: "Sans gluten",
    CONTAINS_FROZEN_PRODUCTS: "Contient des produits surgelés",
    MAY_CONTAIN_TRACES_OF_PEANUTS: "Peut contenir des traces d'arachides",
    VEGETARIAN: "Végétarien",
    VEGAN: "Vegan",
    HALAL: "Halal",
    KOSHER: "Casher",
    LACTOSE_FREE: "Sans lactose",
    ORGANIC: "Bio",
    CONTAINS_MILK: "Contient du lait",
    SPICY: "Épicé"
  };



  constructor(
    private route: ActivatedRoute,
    private auth: AuthService,
    private service: RestaurantService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    this.restaurantName = this.route.snapshot.paramMap.get('name') || '';

    this.auth.isLoggedIn$.subscribe((state) => (this.isLoggedIn = state));

    // ⭐ Chargement du restaurant complet (dishes + toppings + horaires)
    this.restaurant = await this.service.getRestaurantDetails(this.restaurantName);

    this.loading = false;
  }

  onOrder(): void {
    if (!this.isLoggedIn) {
      alert('⚠️ Vous devez être connecté pour commander.');
      return;
    }
    this.router.navigate(['/restaurant-menu', this.restaurantName]);
  }
}
