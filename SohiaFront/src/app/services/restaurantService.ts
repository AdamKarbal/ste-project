import { Injectable } from '@angular/core';
import {Observable, Subject, tap} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {orderCreateResponse} from '../models/orderCreateResponse';
import {ConsoleLogger} from '@angular/compiler-cli';
import {orderService} from './orderService';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {
  baseUrl = 'http://localhost:8001';
  constructor(private OrderService:orderService) {
  }
  /** Obtenir tous les restaurants */
  async getAllRestaurants() {
    console.log('[Service] 📡 Chargement de tous les restaurants...');
    const response = await fetch(`${this.baseUrl}/restaurants`);
    if (!response.ok) {
      console.error(`[Service] ❌ Erreur HTTP ${response.status}`);
      throw new Error(`Erreur HTTP ${response.status}`);
    }
    const data = await response.json();
    console.log('[Service] ✅ Restaurants chargés :', data);
    return data;
  }

  /** Appliquer des filtres complexes (types, cuisine, horaires, prix, etc.) */
  async filterRestaurants(params: URLSearchParams | Record<string, string>) {
    let query = '';

    // Conversion Record → URLSearchParams
    if (params instanceof URLSearchParams) {
      query = params.toString();
    } else {
      query = new URLSearchParams(params).toString();
    }

    // Nettoyage du paramètre openAt (évite le double encodage)
    query = query.replace(/%3A/g, ':');

    const url = `${this.baseUrl}/restaurants/filter?${query}`;
    console.log('[Service] 📡 Application du filtre →', url);

    try {
      const response = await fetch(url, {
        headers: { Accept: 'application/json' }
      });

      if (!response.ok) {
        console.error(`[Service] ❌ Erreur HTTP ${response.status} (${response.statusText})`);
        throw new Error(`Erreur HTTP ${response.status}`);
      }

      const data = await response.json();
      console.log('[Service] ✅ Résultats filtrés :', data);
      return data;
    } catch (error) {
      console.error('[Service] ❌ Erreur réseau ou parsing :', error);
      throw error;
    }
  }

  /** Recherche par nom */
  async getRestaurantByName(name: string) {
    const url = `${this.baseUrl}/restaurants/filter?name=${encodeURIComponent(name)}`;
    console.log(`[Service] 📡 Recherche de restaurant : "${name}" →`, url);

    const response = await fetch(url);
    if (!response.ok) {
      console.error(`[Service] ❌ Erreur HTTP ${response.status}`);
      throw new Error(`Erreur HTTP ${response.status}`);
    }

    const data = await response.json();
    console.log('[Service] ✅ Résultats recherche :', data);

    // Le backend peut renvoyer un seul objet ou une liste
    return Array.isArray(data) ? data : [data];
  }

  /** Récupérer tous les types de cuisines disponibles (Enum du backend) */
  async getCuisineTypes() {
    console.log('[Service] 📡 Chargement des types de cuisine...');
    const response = await fetch(`${this.baseUrl}/restaurants/enums/cuisines`);
    if (!response.ok) {
      console.error(`[Service] ❌ Erreur HTTP ${response.status}`);
      throw new Error(`Erreur HTTP ${response.status}`);
    }

    const data = await response.json();
    console.log('[Service] ✅ Cuisines disponibles :', data);
    return data;
  }

  /** Récupérer tous les types d’établissements disponibles (Enum du backend) */
  async getEstablishmentTypes() {
    console.log('[Service] 📡 Chargement des types d’établissement...');
    const response = await fetch(`${this.baseUrl}/restaurants/enums/types`);
    if (!response.ok) {
      console.error(`[Service] ❌ Erreur HTTP ${response.status}`);
      throw new Error(`Erreur HTTP ${response.status}`);
    }

    const data = await response.json();
    console.log('[Service] ✅ Types d’établissement :', data);
    return data;
  }

  /** Charger les filtres disponibles (cuisines + types d’établissement) */
  async getAvailableEnums() {
    console.log('[Service] 📡 Chargement combiné des filtres disponibles...');
    const [cuisines, establishments] = await Promise.all([
      this.getCuisineTypes(),
      this.getEstablishmentTypes()
    ]);
    console.log('[Service] ✅ Filtres combinés chargés');
    return { cuisines, establishments };
  }

  createOrder(body: any): Observable<number> {
    return this.OrderService.createOrder(body);
  }

  async getRestaurantDetails(name: string) {
    const url = `${this.baseUrl}/restaurants/filter?name=${encodeURIComponent(name)}`;
    console.log(`[Service] 📡 Détails restaurant : "${name}" →`, url);

    const response = await fetch(url);
    if (!response.ok) throw new Error(`Erreur HTTP ${response.status}`);

    const data = await response.json();

    // Le backend renvoie une liste → on prend le premier
    const restaurant = Array.isArray(data) ? data[0] : data;

    console.log('[Service] 🍽️ Restaurant complet reçu :', restaurant);

    return restaurant;
  }
}
