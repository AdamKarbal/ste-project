import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
    FormsModule,
    NavbarComponent,
    LoaderComponent,
    RestaurantCardComponent,
    FooterComponent,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class HomeComponent implements OnInit, OnDestroy {
  /** Liste des restaurants affichés */
  restaurants: any[] = [];

  /** Indicateur de chargement */
  loading = true;

  /** Texte de recherche */
  searchTerm = '';

  /** Affichage du panneau de filtres */
  showFilters = false;

  /** Filtres disponibles (issus du backend) */
  availableFilters = {
    cuisines: [] as string[],
    establishments: [] as string[],
  };

  /** Liste des jours de la semaine */
  daysOfWeek = [
    'LUNDI',
    'MARDI',
    'MERCREDI',
    'JEUDI',
    'VENDREDI',
    'SAMEDI',
    'DIMANCHE',
  ];

  /** Valeurs des filtres actifs */
  filters = {
    cuisines: [] as string[],
    establishments: [] as string[],
    openNow: false,
    openAt: '',
    openDay: '',
    priceMin: '',
    priceMax: '',
  };

  /** Gestionnaire pour fermer le panneau de filtres */
  private clickHandler?: (event: MouseEvent) => void;

  constructor(private restaurantService: RestaurantService) {}

  /** Initialisation du composant */
  async ngOnInit(): Promise<void> {
    this.initClickOutsideHandler();
    await Promise.all([
      this.loadAllRestaurants(),
      this.loadAvailableFilters(),
    ]);
  }

  /** Nettoyage du listener global */
  ngOnDestroy(): void {
    if (this.clickHandler) {
      document.removeEventListener('click', this.clickHandler);
    }
  }

  /** Ferme le panneau des filtres si clic à l’extérieur */
  private initClickOutsideHandler(): void {
    this.clickHandler = (event: MouseEvent) => {
      const filterPanel = document.querySelector('.filter-panel');
      const filterButton = document.querySelector('.filter-btn');

      if (
        this.showFilters &&
        filterPanel &&
        !filterPanel.contains(event.target as Node) &&
        !filterButton?.contains(event.target as Node)
      ) {
        this.showFilters = false;
      }
    };
    document.addEventListener('click', this.clickHandler);
  }

  /** Charge tous les restaurants */
  private async loadAllRestaurants(): Promise<void> {
    this.loading = true;
    try {
      this.restaurants = await this.restaurantService.getAllRestaurants();
      console.log('[Home] Restaurants chargés :', this.restaurants);
    } catch (err) {
      console.error('[Home] Erreur chargement restaurants :', err);
    } finally {
      this.loading = false;
    }
  }

  /** Charge les filtres disponibles depuis le backend */
  private async loadAvailableFilters(): Promise<void> {
    try {
      const enums = await this.restaurantService.getAvailableEnums();
      this.availableFilters = enums;
      console.log('[Home] Filtres disponibles :', enums);
    } catch (err) {
      console.error('[Home] Erreur chargement filtres :', err);
    }
  }

  /** Affiche ou masque le panneau de filtres */
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  /** Active ou désactive une cuisine */
  toggleCuisine(cuisine: string): void {
    const i = this.filters.cuisines.indexOf(cuisine);
    if (i === -1) this.filters.cuisines.push(cuisine);
    else this.filters.cuisines.splice(i, 1);

    console.log('[Home] Cuisines sélectionnées :', this.filters.cuisines);
  }

  /** Active ou désactive un type d’établissement */
  toggleEstablishment(type: string): void {
    const i = this.filters.establishments.indexOf(type);
    if (i === -1) this.filters.establishments.push(type);
    else this.filters.establishments.splice(i, 1);

    console.log('[Home] Types sélectionnés :', this.filters.establishments);
  }

  /** Recherche simple par nom */
  async onSearch(): Promise<void> {
    if (!this.searchTerm.trim()) {
      await this.loadAllRestaurants();
      return;
    }

    this.loading = true;
    try {
      this.restaurants = await this.restaurantService.getRestaurantByName(
        this.searchTerm
      );
      console.log('[Home] Résultats recherche :', this.restaurants);
    } catch (err) {
      console.error('[Home] Erreur recherche :', err);
    } finally {
      this.loading = false;
    }
  }

  /** Application des filtres */
  async applyFilters(): Promise<void> {
    this.loading = true;
    try {
      const queryParams = new URLSearchParams();

      // Nom
      if (this.searchTerm.trim()) queryParams.append('name', this.searchTerm.trim());

      // Cuisines
      this.filters.cuisines.forEach((c) => queryParams.append('cuisine', c));

      // Types d’établissement
      this.filters.establishments.forEach((t) => queryParams.append('type', t));

      // Ouverture (jour + heure)
      if (this.filters.openNow) {
        queryParams.append('openNow', 'true');
      } else if (this.filters.openAt && this.filters.openDay) {
        queryParams.append('openAt', `${this.filters.openDay}-${this.filters.openAt}`);
      }

      // Prix
      if (this.filters.priceMin) queryParams.append('priceMin', this.filters.priceMin);
      if (this.filters.priceMax) queryParams.append('priceMax', this.filters.priceMax);

      console.log('[Home] Application des filtres :', queryParams.toString());

      this.restaurants = await this.restaurantService.filterRestaurants(queryParams);
      console.log('[Home] Résultats filtrés :', this.restaurants);
    } catch (err) {
      console.error('[Home] Erreur application filtres :', err);
    } finally {
      this.loading = false;
      this.showFilters = false;
    }
  }

  /** Réinitialise tous les filtres */
  async clearFilters(): Promise<void> {
    this.filters = {
      cuisines: [],
      establishments: [],
      openNow: false,
      openAt: '',
      openDay: '',
      priceMin: '',
      priceMax: '',
    };
    this.searchTerm = '';
    console.log('[Home] Filtres réinitialisés');
    await this.loadAllRestaurants();
  }
}
