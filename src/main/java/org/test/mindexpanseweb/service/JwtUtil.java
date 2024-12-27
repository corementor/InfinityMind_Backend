package org.test.mindexpanseweb.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${private_key_path}")
    private String privateKeyPath;
    private PrivateKey loadPrivateKey(String path) throws Exception {
        // Read private key from PEM
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(path)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public String jwtTokenGenerator(String userEmail) {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);

            return Jwts.builder()
                    .setSubject(userEmail)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + 14400000))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
