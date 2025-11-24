import { PaymentMethod } from './payment-method.enum';
import { PaymentState } from './payment-state.enum';

export interface Payment {
  id: number;
  status: PaymentState;
  paymentMethod: PaymentMethod;
}
