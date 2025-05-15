package udemy.clone.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import udemy.clone.model.User;
import udemy.clone.repository.UserRepository;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_ROLE = "userRole";

    private final SecretKey jwtAccessSecret;
    private final int expirationInMinutes;
    private final UserRepository userRepository;

    public JwtProvider(
            @Value("${jwt.secret-key}") String jwtAccessSecret,
            @Value("${jwt.expiration}") int expirationInMinutes,
            UserRepository userRepository) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes());
        this.expirationInMinutes = expirationInMinutes;
        this.userRepository = userRepository;
    }

    public String generateAccessToken(User user) {
        final Instant accessExpirationInstant = LocalDateTime.now()
                .plusMinutes(expirationInMinutes).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getId())
                .setExpiration(accessExpiration)
                .signWith(jwtAccessSecret, SignatureAlgorithm.HS256)
                .claim(USER_ID, user.getId())
                .claim(USER_NAME, user.getName())
                .claim(USER_EMAIL, user.getEmail())
                .claim(USER_ROLE, user.getRole().toString())
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
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        String userId = claims.get(USER_ID, String.class);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found by id: " + userId));
        jwtInfoToken.setPrincipal(user);
        return jwtInfoToken;
    }
}
