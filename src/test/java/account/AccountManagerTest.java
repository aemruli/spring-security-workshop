package account;

import account.domain.Account;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Agim Emruli
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("accounts-config.xml")
public class AccountManagerTest {

    @Autowired
    private AccountManager accountManager;

    @Test
    public void getAccountById_initializedAccount_returnedById() throws Exception {
        //Arrange

        //Act
        Account account = accountManager.getById("1");

        //Assert
        Assert.assertNotNull(account);
        Assert.assertEquals("1", account.getAccountId());
        Assert.assertEquals("Agim", account.getFirstName());
        Assert.assertEquals("Emruli", account.getLastName());
    }

    @Test
    public void storeAccount_passedAccount_availableAfterStore() throws Exception {
        //Arrange
        Account account = new Account("2", "Steve", "Jobs");

        //Act
        accountManager.storeAccount(account);

        //Assert
        Account storedAccount = accountManager.getById("2");
        Assert.assertEquals(account, storedAccount);
    }
}