import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderDeliveryPoint, OrderDeliveryPointLabels } from '../../../models/order-delivery-point.enum';

@Component({
  selector: 'se-delivery-point-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './delivery-point-selector.component.html',
  styleUrls: ['./delivery-point-selector.component.css']
})
export class DeliveryPointSelectorComponent {

  @Input() selectedPoint?: OrderDeliveryPoint | null = null;
  @Output() pointSelected = new EventEmitter<OrderDeliveryPoint>();

  points = Object.values(OrderDeliveryPoint);
  labels = OrderDeliveryPointLabels;

  select(point: OrderDeliveryPoint) {
    this.pointSelected.emit(point);
  }
}
