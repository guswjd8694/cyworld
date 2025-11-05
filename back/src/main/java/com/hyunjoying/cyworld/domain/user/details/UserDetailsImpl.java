package com.hyunjoying.cyworld.domain.user.details;

import com.hyunjoying.cyworld.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final User user;
    private String role;

    public UserDetailsImpl(User user) {
        this.user = user;
        this.role = "ROLE_USER";
    }

    public static UserDetailsImpl create(Integer userId, String loginId, String role) {
        User authUser = User.createForAuthentication(userId, loginId, role);
        authUser.setId(userId);
        authUser.setLoginId(loginId);

        UserDetailsImpl userDetails = new UserDetailsImpl(authUser);
        userDetails.role = role;

        return userDetails;
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
