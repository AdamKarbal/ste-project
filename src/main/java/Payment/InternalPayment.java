package Payment;

import Account.AccountUser;
import Order.Order;

public class InternalPayment extends Payment{
    public InternalPayment(Order newOrder){
        super(newOrder);

    }
    @Override
    public PaymentState processPayment(PaymentMethod paymentMethod){ // Paiement avec l'accompte dans le compte du client
        setStatus(PaymentState.PROCESSING);
        AccountUser currentClient= getOrder().getClient();
        if(currentClient.getCredit() >= getOrder().getPrice()){
            setStatus(paymentCorrect(currentClient));
            return getStatus();
        }
        else
        {
            setStatus(PaymentState.FAILED);
            throw new NotEnoughMoney("Vous n'avez pas assez de crédits");
        }
    }
    private PaymentState paymentCorrect(AccountUser client){
        client.setCredit(-getOrder().getPrice());
        return PaymentState.PAID;
    }
}
