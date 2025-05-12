package udemy.clone.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import udemy.clone.model.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private static final String ACCOUNT_ID = "accountId";
    private static final String USER_NAME = "userName";
    private static final String EMAIL = "email";
    private final SecretKey jwtAccessSecret;
    private final int expirationInMinutes;

    public JwtProvider(@Value("${jwt.secret-key}") String jwtAccessSecret,
                       @Value("${jwt.expiration}") int expirationInMinutes) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes());
        this.expirationInMinutes = expirationInMinutes;
    }

    public String generateAccessToken(User user) {
        final Instant accessExpirationInstant = LocalDateTime.now()
                .plusMinutes(expirationInMinutes).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject("JWT Auth token")
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret, SignatureAlgorithm.HS256)
                .claim(ACCOUNT_ID, user.getId())
                .claim(USER_NAME, user.getName())
                .claim(EMAIL, user.getEmail())
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token is expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported token", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed token", mjEx);
        } catch (SignatureException sigEx) {
            log.error("Invalid token signature", sigEx);
        } catch (Exception e) {
            log.error("Invalid token", e);
        }
        return false;
    }

    public Claims getAccessClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtAccessSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtAuthentication generateJwtAuthentication(Claims claims) {
        final JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setId(claims.get(ACCOUNT_ID, String.class));
        jwtAuthentication.setName(claims.get(USER_NAME, String.class));
        jwtAuthentication.setEmail(claims.get(EMAIL, String.class));
        return jwtAuthentication;
    }
}
