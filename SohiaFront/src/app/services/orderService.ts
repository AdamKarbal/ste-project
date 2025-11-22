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
  constructor(private restaurantService : RestaurantService,private httpClient : HttpClient) {
    this.restaurantService.orderCreatedId$.subscribe((id)=>{
      console.log("Commande créée sous l'id : ",id);
      this.currentOrderIdSubject.next(id);}
    );
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

}
