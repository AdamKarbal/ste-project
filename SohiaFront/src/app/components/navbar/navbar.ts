import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  searchTerm: string = '';

  constructor(private router: Router) {}

  /**
   * Quand l'utilisateur appuie sur "Entrée" dans la barre de recherche
   */
  onSearch() {
    if (!this.searchTerm.trim()) return;

    // Redirige vers la page home avec le paramètre de recherche
    this.router.navigate(['/home'], { queryParams: { name: this.searchTerm } });
  }

  /**
   * Quand on clique sur "Mon Restaurant"
   */
  goToOwner() {
    this.router.navigate(['/owner']);
  }
}
