package io.corementor.infinitymind.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.corementor.infinitymind.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * The Class JwtService.
 * @author Blaise Mugisha
 * @version 1.0
 */
@Service
public class JwtService {
    /**
     * The secret key.
     */
    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;
    /**
     * The access expiration time.
     */
    @Value("${security.jwt.access-expiration-time}")
    private long ACCESS_EXPIRATION_TIME;
    /**
     * The refresh expiration time.
     */

    @Value("${security.jwt.refresh-expiration-time}")
    private long REFRESH_EXPIRATION_TIME;

    /**
     * Extract the username.
     * @param token the token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract the roles.
     * @param token the token
     * @return the roles
     */
    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", Set.class));
    }

    /**
     * Extract the token type.
     * @param token the token
     * @return the token type
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("token_type", String.class));
    }

    /**
     * Check if the token is valid.
     * @param token the token
     * @param user the user
     * @return true if the token is valid, false otherwise
     */
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Check if the token is a refresh token.
     * @param token the token
     * @return true if the token is a refresh token, false otherwise
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTokenType(token));
    }

    /**
     * Check if the token is an access token.
     * @param token the token
     * @return true if the token is an access token, false otherwise
     */
    public boolean isAccessToken(String token) {
        return "access".equals(extractTokenType(token));
    }

    /**
     * Check if the token is expired.
     * @param token the token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract the expiration date.
     * @param token the token
     * @return the expiration date
     */

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a claim.
     * @param token the token
     * @param resolver the resolver
     * @return the claim
     * @param <T> the type of the claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Extract all claims.
     * @param token the token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generate an access token.
     * @param user the user
     * @return the access token
     */
    public String generateAccessToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .claim("token_type", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Generate a refresh token.
     * @param user the user
     * @return the refresh token
     */
    public String generateRefreshToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("token_type", "refresh")
                .claim("jti", UUID.randomUUID().toString()) // Unique token identifier
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * get SignIn Key
     * @return the sign in key
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Check if the token is valid.
     * @param token the token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            // Check if token is expired
            if (isTokenExpired(token)) {
                return false;
            }

            // Check if it's an access token
            if (!isAccessToken(token)) {
                return false;
            }

            // Try to extract username (this will throw exception if token is malformed)
            String username = extractUsername(token);
            return username != null && !username.trim().isEmpty();

        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return false;
        }
    }
}