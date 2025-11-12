import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root' // ✅ dit à Angular de fournir ce service globalement
})
export class RestaurantService {
  baseUrl = "http://localhost:8001";

  async getAllRestaurants() {
    const response = await fetch(`${this.baseUrl}/restaurants`);
    return await response.json();
  }

  async filterRestaurants(params: Record<string, string>) {
    const query = new URLSearchParams(params).toString();
    const response = await fetch(`${this.baseUrl}/restaurants/filter?${query}`);
    return await response.json();
  }

  async getRestaurantByName(name: string) {
    const response = await fetch(`${this.baseUrl}/restaurants/filter?name=${name}`);
    const data = await response.json();
    return data[0];
  }
}
