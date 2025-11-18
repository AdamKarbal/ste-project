package Payment;

import Account.AccountUser;
import Api.Utils.Requester;
import Order.Order;

public class InternalPayment extends Payment{
    public InternalPayment(Order newOrder){
        super(newOrder,PaymentMethod.CASH);

    }
    @Override
    public PaymentState processPayment(){ // Paiement avec l'accompte dans le compte du client
        setStatus(PaymentState.PROCESSING);
        AccountUser currentClient= getOrder().getClient();
        if(currentClient.getCredit() >= getOrder().getPrice()){
            setStatus(paymentCorrect(currentClient));
            return getStatus();
        }
        setStatus(PaymentState.FAILED);
        return PaymentState.FAILED;
    }
    private PaymentState paymentCorrect(AccountUser client){
        Requester.updateCredit(client.getId(),client.getCredit()-getOrder().getPrice());
        return PaymentState.PAID;
    }
}
