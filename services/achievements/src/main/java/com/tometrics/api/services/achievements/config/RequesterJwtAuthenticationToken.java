package com.tometrics.api.services.achievements.config;

import com.tometrics.api.services.commonservice.models.Requester;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Collection;
import java.util.Map;

@Transient
public class RequesterJwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String name;

    static Requester requester(Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        return new Requester(userId.intValue());
    }

    /**
     * Constructs a {@code JwtAuthenticationToken} using the provided parameters.
     *
     * @param jwt the JWT
     */
    public RequesterJwtAuthenticationToken(Jwt jwt) {
        super(jwt, requester(jwt), jwt, null);
        this.name = jwt.getSubject();
    }

    /**
     * Constructs a {@code JwtAuthenticationToken} using the provided parameters.
     *
     * @param jwt         the JWT
     * @param authorities the authorities assigned to the JWT
     */
    public RequesterJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, requester(jwt), jwt, authorities);
        this.setAuthenticated(true);
        this.name = jwt.getSubject();
    }

    /**
     * Constructs a {@code JwtAuthenticationToken} using the provided parameters.
     *
     * @param jwt         the JWT
     * @param authorities the authorities assigned to the JWT
     * @param name        the principal name
     */
    public RequesterJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name) {
        super(jwt, requester(jwt), jwt, authorities);
        this.setAuthenticated(true);
        this.name = name;
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    /**
     * The principal name which is, by default, the {@link Jwt}'s subject
     */
    @Override
    public String getName() {
        return this.name;
    }

}
