package com.dodibo.learncore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/*
* This class represents the JWT service in the system.
* The service is responsible for generating and validating JWT tokens.
* The service is responsible for extracting the user ID and tenant ID from the JWT token.
* The service is responsible for extracting the token type from the JWT token.
* The service is responsible for generating the JWT token.
* The service is responsible for validating the JWT token.
* */
@Service
public class JwtService {

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_TENANT_ID = "tenantId";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String CLAIM_FULL_NAME = "fullName";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String TOKEN_TYPE_ACCESS = "ACCESS";
    public static final String TOKEN_TYPE_REFRESH = "REFRESH";

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /*
    * This method is responsible for getting the refresh token expiration in milliseconds.
    * The method is responsible for returning the refresh token expiration in milliseconds.
    * */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration;
    }

    /*
    * This method is responsible for extracting the subject from the JWT token.
    * The method is responsible for returning the subject from the JWT token.
    * */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /*
    * This method is responsible for extracting the user ID from the JWT token.
    * The method is responsible for returning the user ID from the JWT token.
    * */
    public Long extractUserId(String token) {
        return extractLongClaim(token, CLAIM_USER_ID);
    }

    /*
    * This method is responsible for extracting the tenant ID from the JWT token.
    * The method is responsible for returning the tenant ID from the JWT token.
    * */
    public Long extractTenantId(String token) {
        return extractLongClaim(token, CLAIM_TENANT_ID);
    }

    /*
    * This method is responsible for extracting the token type from the JWT token.
    * The method is responsible for returning the token type from the JWT token.
    * */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
    }

    /*
    * This method is responsible for checking if the token is an access token.
    * The method is responsible for returning true if the token is an access token, false otherwise.
    * */
    public boolean isAccessToken(String token) {
        return TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
    }

    /*
    * This method is responsible for checking if the token is a refresh token.
    * The method is responsible for returning true if the token is a refresh token, false otherwise.
    * */
    public boolean isRefreshToken(String token) {
        return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
    }

    /*
    * This method is responsible for extracting a long claim from the JWT token.
    * The method is responsible for returning the long claim from the JWT token.
    * */
    private Long extractLongClaim(String token, String claimName) {
        Object value = extractClaim(token, claims -> claims.get(claimName));
        if (value == null) {
            return null;
        }
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Integer intValue) {
            return intValue.longValue();
        }
        if (value instanceof String stringValue) {
            return Long.parseLong(stringValue);
        }
        throw new IllegalStateException("Unexpected claim type for " + claimName + ": " + value.getClass());
    }

    /*
    * This method is responsible for extracting a claim from the JWT token.
    * The method is responsible for returning the claim from the JWT token.
    * */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /*
    * This method is responsible for generating an access token.
    * The method is responsible for returning the access token.
    * */
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    /*
    * This method is responsible for generating an access token.
    * The method is responsible for returning the access token.
    * */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return buildToken(claims, userDetails, accessTokenExpiration);
    }

    /*
    * This method is responsible for generating a refresh token.
    * The method is responsible for returning the refresh token.
    * */
    public String generateRefreshToken(UserDetails userDetails) {
        if (!(userDetails instanceof com.dodibo.learncore.user.User user)) {
            throw new IllegalArgumentException("Unsupported principal type");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        claims.put(CLAIM_USER_ID, user.getId());
        claims.put(CLAIM_TENANT_ID, user.getTenantId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /*
    * This method is responsible for building a token.
    * The method is responsible for returning the token.
    * */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        if (!(userDetails instanceof com.dodibo.learncore.user.User user)) {
            throw new IllegalArgumentException("Unsupported principal type");
        }

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = new HashMap<>(extraClaims);
        claims.put(CLAIM_USER_ID, user.getId());
        claims.put(CLAIM_TENANT_ID, user.getTenantId());
        claims.put(CLAIM_AUTHORITIES, authorities);
        claims.put(CLAIM_FULL_NAME, user.fullName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /*
    * This method is responsible for checking if the token is valid.
    * The method is responsible for returning true if the token is valid, false otherwise.
    * */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (!(userDetails instanceof com.dodibo.learncore.user.User user)) {
            return false;
        }
        final Long userId = extractUserId(token);
        return userId != null && userId.equals(user.getId()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
