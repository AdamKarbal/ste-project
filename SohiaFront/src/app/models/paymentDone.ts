import {OrderState} from './order-state.enum';

export interface PaymentDone{
  idOrder:number,
  orderState:OrderState
}
