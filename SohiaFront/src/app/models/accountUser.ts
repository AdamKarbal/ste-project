import {Payment} from './payment.model';

export interface AccountUserCreated{
  id:number,
  name:string
}

export interface AccountUser{
  id:number,
  name:string,
  credit:number,
  history : Map<number,Payment>
}
