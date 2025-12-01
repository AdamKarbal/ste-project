import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DeliveryAddress } from '../../../models/delivery-address.model';

@Component({
  selector: 'se-delivery-address-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './delivery-address-modal.component.html',
  styleUrls: ['./delivery-address-modal.component.css']
})
export class DeliveryAddressModalComponent {

  @Input() visible = false;

  @Input() initialAddress?: DeliveryAddress | null;

  @Output() saved = new EventEmitter<DeliveryAddress>();
  @Output() closed = new EventEmitter<void>();

  model: DeliveryAddress = {
    fullName: '',
    street: '',
    postalCode: '',
    city: '',
    country: 'France',
    additionalInfo: ''
  };

  ngOnChanges(): void {
    if (this.initialAddress) {
      this.model = { ...this.initialAddress };
    } else {
      this.model = {
        fullName: '',
        street: '',
        postalCode: '',
        city: '',
        country: 'France',
        additionalInfo: ''
      };
    }
  }

  onSave(): void {
    if (!this.isValid()) {
      return;
    }
    this.saved.emit({ ...this.model });
  }

  onCancel(): void {
    this.closed.emit();
  }

  private isValid(): boolean {
    return !!(
      this.model.fullName &&
      this.model.street &&
      this.model.postalCode &&
      this.model.city &&
      this.model.country
    );
  }
}
