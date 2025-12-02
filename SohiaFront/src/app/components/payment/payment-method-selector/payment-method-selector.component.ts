import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentMethod } from '../../../models/payment-method.enum';

@Component({
  selector: 'se-payment-method-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-method-selector.component.html',
  styleUrls: ['./payment-method-selector.component.css']
})
export class PaymentMethodSelectorComponent {

  methods: PaymentMethod[] = Object.values(PaymentMethod) as PaymentMethod[];

  @Output() methodSelected = new EventEmitter<PaymentMethod>();

  select(method: PaymentMethod) {
    this.methodSelected.emit(method);
  }
}
