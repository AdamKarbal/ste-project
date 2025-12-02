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
