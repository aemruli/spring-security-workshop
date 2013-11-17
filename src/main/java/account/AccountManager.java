package account;

import account.domain.Account;

/**
 * @author Agim Emruli
 * @since 1.0
 */
public interface AccountManager {

    Account getById(String accountId);

    void storeAccount(Account account);

}
