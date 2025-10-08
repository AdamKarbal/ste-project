package Account;

import Payment.Payment;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected static int compteur=0;
    protected int id;
    protected List<Payment> paymentHistory;
    public Account(){
        compteur++;
        paymentHistory=new ArrayList<>();
        id=compteur;
    }
    public int getId(){
        return id;
    }



}
