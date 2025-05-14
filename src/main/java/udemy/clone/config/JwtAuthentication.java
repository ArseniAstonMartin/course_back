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
    private String name;
    private String email;
    private String id;
    private User.Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) () -> "ROLE_" + role.toString());
    }

    @Override
    public Object getCredentials() {
        return id;
    }

    @Override
    public Object getDetails() {
        return id;
    }

    @Override
    public Object getPrincipal() {
        return name;
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
    public String getName() { return name; }
}
