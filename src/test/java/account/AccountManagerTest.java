package account;

import account.domain.Account;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Before
    public void authenticate() throws Exception {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("agim", "secret"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticate);
        SecurityContextHolder.setContext(context);
    }

    @After
    public void logout() throws Exception {
        SecurityContextHolder.clearContext();
    }

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
        Account account = new Account("2", "Steve", "Jobs", "agim");

        //Act
        accountManager.storeAccount(account);

        //Assert
        Account storedAccount = accountManager.getById("2");
        Assert.assertEquals(account, storedAccount);
    }
}