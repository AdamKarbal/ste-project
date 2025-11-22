import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/authService';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  isLoggedIn = false;

  constructor(private authService: AuthService) {
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;
    });
  }

  toggleLogin(): void {
    if (this.isLoggedIn) {
      this.authService.logout();
      alert('👋 Vous êtes déconnecté.');
    } else {
      this.authService.login();
      alert('✅ Vous êtes connecté.');
    }
  }
}
