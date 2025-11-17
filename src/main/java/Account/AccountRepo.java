package Account;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AccountRepo {
    private Map<Integer,Account> clients;
    public AccountRepo(){
        clients=new HashMap<Integer, Account>();
    }
    public Optional<Account> findById(int id){
        return Optional.ofNullable(clients.get(id));
    }
    public AccountUser save(AccountUser accountUser){
        clients.put(accountUser.getId(),accountUser);
        return accountUser;
    }

    public boolean deleteById(int id){
        if(clients.containsKey(id)){
            clients.remove(id);
            return true;
        }
        return false;
    }
    public boolean updateOrder(int id, AccountUser newAccount) {
        Account oldAccount = findById(id).orElseThrow(
                () -> new AccountNotFound("Compte " + id + " introuvable."));
        if (oldAccount !=null) {
            clients.remove(id);
            clients.put(id,newAccount);
            return true;
        }
        return false;
    }

    public List<Account> getAllOrders(){
        return clients.values().stream().toList();
    }
}
