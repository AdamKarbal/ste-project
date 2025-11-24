import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {RestaurantService} from './restaurantService';
import {orderStateModel} from '../models/orderStateModel'

@Injectable({
  providedIn: 'root'
})
export class orderService{
  private orderUrl = 'http://localhost:8000/api/order/';
  private orderStateSubject = new BehaviorSubject<orderStateModel | null>(null);
  orderState$ = this.orderStateSubject.asObservable();
  private currentOrderIdSubject = new BehaviorSubject<number | null>(null);
  currentOrderId$ = this.currentOrderIdSubject.asObservable();
  constructor(private httpClient : HttpClient) {
  }
  getCurrentOrderId():number|null{
    return this.currentOrderIdSubject.value;
  }

  addItem(dishId:number,body:any):Observable<orderStateModel>{
    return this.httpClient.post<orderStateModel>(`${this.orderUrl}${this.getCurrentOrderId()}/dish/${dishId}/`,body).pipe(
      tap((state) => {
        this.orderStateSubject.next(state);
      })
    );
  }

  removeItem(dishId:number):Observable<orderStateModel>{
    return this.httpClient.delete<orderStateModel>(`${this.orderUrl}${this.getCurrentOrderId()}/dish/${dishId}/`).pipe(
      tap((state) => {
        this.orderStateSubject.next(state);
      })
    );
  }
  createOrder(body: any): Observable<number> {
    return this.httpClient
      .post<number>(`${this.orderUrl}`, body)
      .pipe(
        tap((order) => {
          if (order) {
            console.log(order);
            this.currentOrderIdSubject.next(order);
          } else {
            console.warn('Réponse de création de commande sans id :', order);
          }
        })
      );
  }

}
