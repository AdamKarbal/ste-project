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
import {DeliveryAddress} from '../../models/delivery-address.model';
import {DeliveryAddressModalComponent} from '../../components/payment/delivery-address-modal/delivery-address-modal.component';
import {OrderDeliveryPoint} from '../../models/order-delivery-point.enum';
import {
  DeliveryPointSelectorComponent
} from '../../components/payment/delivery-point-selector/delivery-point-selector.component';
import {orderService} from '../../services/orderService';


@Component({
  selector: 'se-payment-page',
  standalone: true,
  imports: [
    CommonModule,
    PaymentSummaryComponent,
    PaymentMethodSelectorComponent,
    PaymentStatusComponent,
    NavbarComponent,
    FooterComponent,
    DeliveryPointSelectorComponent
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
  payment: PaymentDone | null = null;
  isAddressModalOpen = false;
  selectedDeliveryPoint?: OrderDeliveryPoint;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private orderService : orderService
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

    if (!this.selectedDeliveryPoint) {
      console.warn('Adresse de livraison manquante');
      return;
    }

    this.isPaying = true;

    this.paymentService.processPayment(this.orderId, this.selectedMethod)
      .pipe(
        finalize(() => {
          this.isPaying = false;
        })
      )
      .subscribe({
        next: (result) => {
          this.payment = result;
        },
        error: err => {
          console.error('Erreur lors du paiement :', err);
        }
      });
  }

  openAddressModal(): void {
    this.isAddressModalOpen = true;
  }
  onDeliveryPointSelected(point: OrderDeliveryPoint) {
    this.selectedDeliveryPoint = point;
    this.orderService.chooseLocation(point).subscribe({
      next: ()=>console.log("Livraison ajoutée",point),
      error : (error) => console.log(error)
    })
  }


  get canPay(): boolean {
    return !!this.selectedMethod && this.totalAmount > 0 && !!this.selectedDeliveryPoint;
  }


}
