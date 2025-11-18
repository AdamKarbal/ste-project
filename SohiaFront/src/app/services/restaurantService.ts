import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  baseUrl = "http://localhost:8001";

  // 🧠 Obtenir tous les restaurants
  async getAllRestaurants() {
    console.log('📡 [Service] Chargement de tous les restaurants...');
    const response = await fetch(`${this.baseUrl}/restaurants`);
    const data = await response.json();
    console.log('✅ [Service] Restaurants chargés :', data);
    return data;
  }

  // 🔎 Filtrage avancé
  async filterRestaurants(params: URLSearchParams | Record<string, string>) {
    let query = '';
    if (params instanceof URLSearchParams) query = params.toString();
    else query = new URLSearchParams(params).toString();

    const url = `${this.baseUrl}/restaurants/filter?${query}`;
    console.log('📡 [Service] Application du filtre →', url);

    const response = await fetch(url);
    const data = await response.json();

    console.log('✅ [Service] Résultats filtrés :', data);
    return data;
  }

  // 🔎 Recherche par nom
  async getRestaurantByName(name: string) {
    console.log(`📡 [Service] Recherche de restaurant avec le nom "${name}"`);
    const response = await fetch(`${this.baseUrl}/restaurants/filter?name=${encodeURIComponent(name)}`);
    const data = await response.json();
    console.log('✅ [Service] Résultats recherche :', data);
    return Array.isArray(data) ? data : [data];
  }

  // 🍽️ Récupérer tous les types de cuisines (enum)
  async getCuisineTypes() {
    const response = await fetch(`${this.baseUrl}/restaurants/enums/cuisines`);
    const data = await response.json();
    console.log('✅ [Service] Cuisines disponibles :', data);
    return data;
  }

  // 🏠 Récupérer tous les types d’établissements (enum)
  async getEstablishmentTypes() {
    const response = await fetch(`${this.baseUrl}/restaurants/enums/types`);
    const data = await response.json();
    console.log('✅ [Service] Types d’établissement :', data);
    return data;
  }

  // ⚙️ Méthode combinée (utile dans le composant)
  async getAvailableEnums() {
    const [cuisines, establishments] = await Promise.all([
      this.getCuisineTypes(),
      this.getEstablishmentTypes()
    ]);
    console.log('✅ [Service] Filtres combinés chargés');
    return { cuisines, establishments };
  }
}
