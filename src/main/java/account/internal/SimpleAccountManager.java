package account.internal;

import account.AccountManager;
import account.domain.Account;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Agim Emruli
 * @since 1.0
 */
@Service
public class SimpleAccountManager implements AccountManager {

    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<String, Account>();

    @PostConstruct
    public void populateAccounts() {
        accounts.put("1", new Account("1", "Agim", "Emruli"));
    }

    @Secured("ROLE_USER")
    @Override
    public Account getById(String accountId) {
        Account source = accounts.get(accountId);
        return new Account(source.getAccountId(), source.getFirstName(), source.getLastName());
    }

    @Secured("ROLE_USER")
    @Override
    public void storeAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }
}