import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Restaurant } from '../../models/restaurant.model';
import { CuisineType , CuisineTypeLabel} from '../../models/cuisine-type.model';
import { EstablishmentType , EstablishmentTypeLabel} from '../../models/establishment-type.model';

@Component({
  selector: 'se-restaurant-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './restaurant-settings.component.html',
  styleUrls: ['./restaurant-settings.component.css']
})
export class RestaurantSettingsComponent {
  @Input() restaurant!: Restaurant;
  @Output() establishmentChange = new EventEmitter<EstablishmentType>();
  @Output() addCuisine = new EventEmitter<CuisineType>();
  @Output() removeCuisine = new EventEmitter<CuisineType>();

  cTypes = Object.values(CuisineType);
  eTypes = Object.values(EstablishmentType);
  eLabels = EstablishmentTypeLabel;
  cLabels = CuisineTypeLabel;
  selectedCuisine: CuisineType | null = null;

  onChangeEstablishment(val: EstablishmentType) {
    this.establishmentChange.emit(val);
  }

  onAddCuisine() {
    if (this.selectedCuisine) {
      this.addCuisine.emit(this.selectedCuisine);
      this.selectedCuisine = null;
    }
  }

  onRemoveCuisine(c: CuisineType) {
    this.removeCuisine.emit(c);
  }
}
