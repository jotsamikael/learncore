package com.dodibo.learncore.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/*
* This class represents the authorization service in the system.
* The service is responsible for checking if a user has a permission.
* The service is responsible for returning true if a user has a permission, false otherwise.
* */
@Service("authz")//The @Service("authz") indicates that this class is a service and can be injected into other classes.
public class AuthorizationService {

    public boolean can(String permission) {
        /*
        * This method is responsible for checking if a user has a permission.
        * The method is responsible for returning true if a user has a permission, false otherwise.
        * */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission));
    }
}
