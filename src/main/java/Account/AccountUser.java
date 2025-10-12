package Account;

public class AccountUser extends Account {
    private double credit;
    public AccountUser(){
        super();
        credit=0;
    }
    public double getCredit(){
        return credit;
    }
    public void setCredit(double newCredit){
        credit+=newCredit;
    }
}
