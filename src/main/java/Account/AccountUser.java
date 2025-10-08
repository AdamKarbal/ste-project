package Account;

public class AccountUser extends Account {
    private float credit;
    public AccountUser(){
        super();
        credit=0;
    }
    public float getCredit(){
        return credit;
    }
    public void setCredit(float newCredit){
        credit+=newCredit;
    }
}
