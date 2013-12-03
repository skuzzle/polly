package polly.dyndns.core;

import java.util.List;

import polly.dyndns.MSG;
import polly.dyndns.entities.Account;
import polly.dyndns.entities.Hoster;
import de.skuzzle.polly.sdk.PersistenceManagerV2;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Param;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Read;
import de.skuzzle.polly.sdk.PersistenceManagerV2.Write;
import de.skuzzle.polly.sdk.exceptions.DatabaseException;


public class HostManager {

    private final PersistenceManagerV2 persistence;
    
    
    
    public HostManager(PersistenceManagerV2 persistence) {
        this.persistence = persistence;
    }
    
    
    
    public Account addAccount(int hosterId, String userName, String domainName, 
            String password) throws DatabaseException {
        try (Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final Hoster hoster = read.find(Hoster.class, hosterId);
            if (hoster == null) {
                throw new DatabaseException(MSG.bind(MSG.unknownHosterId, hosterId));
            }
            final Account account = new Account(hoster, userName, domainName, password);
            write.single(account);
            return account;
        }
    }
    
    
    
    public Account deleteAccount(int accountId) throws DatabaseException {
        try (Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final Account acc = read.find(Account.class, accountId);
            if (acc == null) {
                throw new DatabaseException(MSG.bind(MSG.unknownAccountId, accountId));
            }
            acc.setHoster(null);
            write.remove(acc);
            return acc;
        }
    }
    
    
    
    public Hoster addHoster(String name, String baseUrl) throws DatabaseException {
        try (Write write = this.persistence.write()) {
            final Hoster hoster = new Hoster(name, baseUrl);
            write.single(hoster);
            return hoster;
        }
    }
    
    
    
    public List<Account> getAllAccounts() {
        return this.persistence.atomic().findList(Account.class, Account.QUERY_ALL_ACCOUNTS);
    }
    
    
    
    public List<Hoster> getAllHosters() {
        return this.persistence.atomic().findList(Hoster.class, Hoster.QUERY_ALL_HOSTERS);
    }
    
    
    
    public Hoster deleteHoster(int hosterId) throws DatabaseException {
        try (Write write = this.persistence.write()) {
            final Read read = write.read();
            
            final Hoster hoster = read.find(Hoster.class, hosterId);
            if (hoster == null) {
                throw new DatabaseException(MSG.bind(MSG.unknownHosterId, hosterId));
            }
            final List<Account> accounts = read.findList(Account.class, 
                    Account.QUERY_ACCOUNTS_BY_HOSTER, new Param(hoster.getId()));
            
            for (final Account acc : accounts) {
                acc.setHoster(null);
                write.remove(acc);
            }
            
            write.remove(hoster);
            return hoster;
        }
    }
}
