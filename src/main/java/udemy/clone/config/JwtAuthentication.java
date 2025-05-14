package udemy.clone.config;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import udemy.clone.model.User;

import java.util.Collection;
import java.util.Collections;

@Data
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private User principal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + principal.getRole().toString());
    }

    @Override
    public Object getCredentials() {
        return principal.getId();
    }

    @Override
    public Object getDetails() {
        return principal.getId();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}
