import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentMethod } from '../../../models/payment-method.enum';

@Component({
  selector: 'se-payment-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-summary.component.html',
  styleUrls: ['./payment-summary.component.css']
})
export class PaymentSummaryComponent {

  @Input() orderId!: number;
  @Input() selectedMethod?: PaymentMethod;
  @Input() isPaying = false;
  @Input() totalAmount = 0;

  @Output() pay = new EventEmitter<void>();
}
