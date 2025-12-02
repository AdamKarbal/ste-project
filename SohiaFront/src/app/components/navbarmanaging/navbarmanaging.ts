import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/authService';

@Component({
  selector: 'app-navbarmanaging',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './navbarmanaging.html',
  styleUrl: './navbarmanaging.css'
})
export class NavbarManagingComponent {
  searchTerm: string = '';
  isLoggedIn: boolean = false;

  constructor(private router: Router, private auth: AuthService) {
    this.auth.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }

  goToOwner() {
    this.router.navigate(['/owner']);
  }
  toggleLogin() {
    if (this.isLoggedIn) {
      this.auth.logout();
    } else {
      this.auth.login();
    }
  }
  goHome() {
    this.auth.logout();
    this.router.navigate(['/landing']);
  }

}
