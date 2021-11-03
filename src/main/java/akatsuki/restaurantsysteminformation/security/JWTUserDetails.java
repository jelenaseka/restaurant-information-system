package akatsuki.restaurantsysteminformation.security;

import akatsuki.restaurantsysteminformation.registereduser.RegisteredUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JWTUserDetails implements UserDetails {

    private final RegisteredUser user;

    public JWTUserDetails(RegisteredUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // user.isEmailVerified();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Sta god hocemo da bude u tokenu definisemo ovako i zatim jos u fajlu JWTTokenUtil
    public long getId() {
        return user.getId();
    }
}
