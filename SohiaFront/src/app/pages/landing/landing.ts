import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './landing.html',
  styleUrls: ['./landing.css']
})
export class LandingPageComponent {
  constructor(private router: Router) {}

  goToClient() {
    this.router.navigate(['/home']);
  }

  goToOwner() {
    this.router.navigate(['/restaurant-manager/Chez%20Mario']);
  }
}
