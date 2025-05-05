package udemy.clone.config;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import udemy.clone.model.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey secretKey;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject("Token for" + user.getName())
                .claim("userId", user.getId())
                .claim("username", user.getName())
                .claim("role", "ROLE_" + user.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtAuthentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null, List.of(new SimpleGrantedAuthority(role))
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
