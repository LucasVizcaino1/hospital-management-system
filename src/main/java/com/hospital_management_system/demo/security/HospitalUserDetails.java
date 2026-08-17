package com.hospital_management_system.demo.security;

import com.hospital_management_system.demo.model.Rol;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class HospitalUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Long userId;
    private final Long principalId;
    private final Rol rol;

    public HospitalUserDetails(String username, String password,
                               Long userId, Long principalId, Rol rol) {
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.principalId = principalId;
        this.rol = rol;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + rol.name())
        );
    }

    @Override
    public String getPassword() {
        return this.password;
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