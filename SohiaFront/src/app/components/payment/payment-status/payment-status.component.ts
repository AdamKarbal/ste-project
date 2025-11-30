import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Payment } from '../../../models/payment.model';
import { PaymentState } from '../../../models/payment-state.enum';

@Component({
  selector: 'se-payment-status',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-status.component.html',
  styleUrls: ['./payment-status.component.css']
})
export class PaymentStatusComponent {

  @Input() payment?: Payment;
  @Input() isPaying = false;

  PaymentState = PaymentState;
}
