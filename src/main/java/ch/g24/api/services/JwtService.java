package ch.g24.api.services;

import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.UserRepository;
import ch.g24.api.utility.Utility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey;
    private final long expirationMs;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expirationMs, UserRepository userRepository) {
        this.secretKey = secretKey;
        this.expirationMs = expirationMs;
        this.userRepository = userRepository;
    }

    // 🔹 Extract username (subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 Extract single claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 🔹 Generate token without extra claims
    public String generateToken(UserDetails userDetails, String origin) {

        UserEntity userEntity = userRepository.findByUserName(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userEntity.getUserId());
        return generateToken(extraClaims, userDetails, origin);
    }

    public String generateResetToken(String email) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + expirationMs;

        Date issuedAt = new Date(nowMillis);
        Date expiration = new Date(expMillis);
        logger.info("Generating reset token for {} | issuedAt={} | expiresAt={}",
                email, issuedAt, expiration);
        return Jwts.builder()
                .setSubject(email)                              // E-Mail als Subject
                .setIssuedAt(new Date(nowMillis))              // Token-Ausstellungszeit
                .setExpiration(new Date(expMillis))            // Token-Ablaufzeit
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Signierung
                .compact();
    }

    // 🔹 Generate token with claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String origin) {
        long nowMillis = System.currentTimeMillis();
        int factor = Optional.ofNullable(origin)
                .filter(o -> o.equals(Utility.ORIGIN))
                .map(o -> 165)
                .orElse(1);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(nowMillis + (expirationMs * factor)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
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

