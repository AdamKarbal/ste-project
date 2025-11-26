import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/authService'; // Assure-toi que ce service existe

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  searchTerm: string = '';
  isLoggedIn: boolean = false;

  constructor(private router: Router, private auth: AuthService) {
    // Surveille l’état de connexion
    this.auth.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }



  /** Navigation vers l’espace restaurateur */
  goToOwner() {
    this.router.navigate(['/owner']);
  }

  /** Connexion / déconnexion */
  toggleLogin() {
    if (this.isLoggedIn) {
      this.auth.logout(); //  Déconnexion + redirection vers /home
    } else {
      this.auth.login(); //  Connexion instantanée
    }
  }

  goHome() {

    this.auth.logout();
    this.router.navigate(['/landing']);
  }

}
