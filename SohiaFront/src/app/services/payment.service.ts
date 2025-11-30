import { Injectable } from '@angular/core';
import {Observable, of, delay, tap, BehaviorSubject} from 'rxjs';
import { PaymentMethod } from '../models/payment-method.enum';
import {HttpClient} from '@angular/common/http';
import {orderStateModel} from '../models/orderStateModel';
import {PaymentDone} from '../models/paymentDone';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private orderUrl = 'http://localhost:8000/api/order/';
  private paymentStateSubject = new BehaviorSubject<PaymentDone | null>(null);
  paymentState$ = this.paymentStateSubject.asObservable();

  constructor(private client: HttpClient) {}

  processPayment(orderId: number, method: PaymentMethod): Observable<PaymentDone> {
    console.log(`${this.orderUrl}${orderId}/pay/${method.toString()}/`);
    return this.client
      .post<PaymentDone>(
        `${this.orderUrl}${orderId}/pay/${method.toString()}/`,
        null
      )
      .pipe(
        tap(state => {
          this.paymentStateSubject.next(state);
        })
      );
  }
}
