package account.internal;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Agim Emruli
 * @since 1.0
 */
@Component
public class SimpleUserDetailsService implements UserDetailsService {

    private final ConcurrentHashMap<String, UserDetails> users = new ConcurrentHashMap<String, UserDetails>();

    @PostConstruct
    public void initializeUsers() {
        users.put("agim", new User("agim", "dadacb850ece4991a835177831d1fb35",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details = users.get(username);
        if (details == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(details.getUsername(), details.getPassword(), details.getAuthorities());
    }
}