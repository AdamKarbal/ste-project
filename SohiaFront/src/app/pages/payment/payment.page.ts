import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PaymentSummaryComponent } from '../../components/payment/payment-summary/payment-summary.component';
import { PaymentMethodSelectorComponent } from '../../components/payment/payment-method-selector/payment-method-selector.component';
import { PaymentStatusComponent } from '../../components/payment/payment-status/payment-status.component';

import { PaymentService } from '../../services/payment.service';
import { PaymentMethod } from '../../models/payment-method.enum';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FooterComponent } from '../../components/footer/footer';
import {finalize, Observable} from 'rxjs';
import {PaymentDone} from '../../models/paymentDone';

@Component({
  selector: 'se-payment-page',
  standalone: true,
  imports: [
    CommonModule,
    PaymentSummaryComponent,
    PaymentMethodSelectorComponent,
    PaymentStatusComponent,
    NavbarComponent,
    FooterComponent
  ],
  templateUrl: './payment.page.html',
  styleUrls: ['./payment.page.css']
})
export class PaymentPage implements OnInit {

  orderId!: number;
  totalAmount = 0;

  selectedMethod?: PaymentMethod;
  isPaying = false;
  paymentState$! : Observable<PaymentDone|null>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('orderId'));
    this.paymentState$ = this.paymentService.paymentState$;
    const nav = this.router.getCurrentNavigation();
    const stateFromNav = nav?.extras.state as { totalAmount?: number } | undefined;
    const stateFromHistory = history.state as { totalAmount?: number };

    this.totalAmount = stateFromNav?.totalAmount
      ?? stateFromHistory?.totalAmount
      ?? 0;

    console.log('Total reçu sur la page paiement :', this.totalAmount);
  }

  onMethodSelected(method: PaymentMethod) {
    this.selectedMethod = method;
  }

  onPay() {
    if (!this.selectedMethod) return;
    this.isPaying = true;
    this.paymentService.processPayment(this.orderId, this.selectedMethod)
      .pipe(
        finalize(() => {
          this.isPaying = false;
        })
      )
      .subscribe({
        error: err => {
          console.error('Erreur lors du paiement :', err);
        }
      });
  }
}
