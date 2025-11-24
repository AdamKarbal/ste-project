import { Injectable } from '@angular/core';
import { Observable, of, delay } from 'rxjs';
import { PaymentMethod } from '../models/payment-method.enum';
import { PaymentState } from '../models/payment-state.enum';
import { Payment } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  processPayment(orderId: number, method: PaymentMethod): Observable<Payment> {

    const fakePayment: Payment = {
      id: Math.floor(Math.random() * 10000),
      paymentMethod: method,
      status: PaymentState.PROCESSING
    };

    const finalState =
      method === PaymentMethod.CASH
        ? PaymentState.PAID
        : PaymentState.PAID;

    return of({
      ...fakePayment,
      status: finalState
    }).pipe(delay(1200));
  }
}
