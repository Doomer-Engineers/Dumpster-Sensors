package com.dumpster_sensor.webapp;

import com.dumpster_sensor.webapp.models.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetail implements org.springframework.security.core.userdetails.UserDetails {
    private User user;

    public UserDetail(User user){
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    //getter for password
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    //getter for username
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
