package io.corementor.mindexpanse.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.corementor.mindexpanse.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", Set.class));
    }
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User user) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("email", user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .signWith(getSignInKey())
                .compact();

    }
    private SecretKey getSignInKey() {
        String SECRET_KEY = "c52ce3add79d1f83d7d6491ee228bee2126978012e17253627e94f4607981e9a0a89833014d45361c351443a937c809f1f362c36e26ef118c1c5ec1edf7a803a5c72c5e5506a96f0198c789fcf352e050ee83620a43c4f1f003e048ea207f7542cf9b76ecefb56c7407c69ff0980a250d415b3a654d6425fa0c0c0b528df53b186674fcd4be53192d5e10b88d42e4bc2001ab386155e3734398092c3d11708dfccb7f72a5011516283ae4503acc67bfdc0f28249d69a70b78986fe13738088bc925973cb166274d325a2c54a9455f78e9c80a05af906baf6e0faac7873129a481b493f4a325781cfbedee1c2c826497a7af78d8c69a572b712f90705bebd3338";
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
