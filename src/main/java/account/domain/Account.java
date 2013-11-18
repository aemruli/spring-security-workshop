package account.domain;

/**
 * @author Agim Emruli
 * @since 1.0
 */
public class Account {

    private final String accountId;
    private final String firstName;
    private final String lastName;
    private final String owner;

    public Account(String accountId, String firstName, String lastName, String owner) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.owner = owner;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return accountId.equals(account.accountId) &&
                firstName.equals(account.firstName) &&
                lastName.equals(account.lastName);

    }

    @Override
    public int hashCode() {
        int result = accountId.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
