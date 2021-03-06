import {Payment} from './payment';
import {Shipping} from './shipping';

export class User {
  public id: number;
  public firstName: string;
  public lastName: string;
  public username: string;
  public authorities: string;
  public password: string;
  public email: string
  public phone: string;
  public enabled: boolean;
  public userPaymentList: Payment[];
  public userShippingList: Shipping[];

}
