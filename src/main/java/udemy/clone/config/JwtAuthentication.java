package udemy.clone.config;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import udemy.clone.model.User;

import java.util.Collection;
import java.util.Collections;

@Data
public class JwtAuthentication implements Authentication {
    private boolean isAuthenticated;
    private Long accountId;
    private String userName;
    private User.Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + role.toString());
    }

    @Override
    public Object getCredentials() {
        return accountId;
    }

    @Override
    public Object getDetails() {
        return accountId;
    }

    @Override
    public Object getPrincipal() {
        return userName;
    }

    @Override
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return userName;
    }
}
