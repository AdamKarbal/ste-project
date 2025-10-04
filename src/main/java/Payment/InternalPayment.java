package Payment;

import Account.AccountUser;
import Order.Order;

public class InternalPayment extends Payment{
    public InternalPayment(Order newOrder){
        super(newOrder);

    }
    @Override
    public void processPayment(PaymentMethod paymentMethod){ // Paiement avec l'accompte dans le compte du client
        setStatus(PaymentState.PROCESSING);
        AccountUser currentClient= getOrder().getClient();
        if(currentClient.getCredit() >= getOrder().getPrice()){
            setStatus(paymentCorrect(currentClient));
        }
        else
        {
            setStatus(PaymentState.FAILED);
            throw new NotEnoughMoney("Vous n'avez pas assez d'argent");
        }
    }
    private PaymentState paymentCorrect(AccountUser client){
        client.setCredit(-getOrder().getPrice());
        return PaymentState.PAID;
    }
}
