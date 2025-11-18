package Account;



public abstract class Account {
    protected static int compteur=0;
    protected int id;
    protected String name;
    public Account(String name){
        id=++compteur;
        this.name=name;
    }
    public Account(){}
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }



}
