import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import { AuthService } from '../../services/authService';
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
  isLoggedIn = false;

  constructor(private route: ActivatedRoute, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.restaurantName = this.route.snapshot.paramMap.get('name') || '';
    this.auth.isLoggedIn$.subscribe((state) => (this.isLoggedIn = state));
  }

  onOrder(): void {
    if (!this.isLoggedIn) {
      alert('⚠️ Vous devez être connecté pour commander.');
      return;
    }
    this.router.navigate(['/restaurant-menu', this.restaurantName]);
  }
}
