package com.minisocial.security;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "jdbc/minisocial",
        callerQuery = "SELECT password FROM users WHERE email = ?",
        groupsQuery = "SELECT role FROM users WHERE email = ?"
)
@DeclareRoles({"user", "admin"})
@ApplicationScoped
public class SecurityConfig {
}