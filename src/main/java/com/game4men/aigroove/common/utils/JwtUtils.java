package com.game4men.aigroove.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.Key;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    private Key key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(String username, String type) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("type", type)          // "ADMIN" | "USER"
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)  // 초기화된 키 사용
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /** 토큰 발급 주체 구분. "ADMIN" 또는 "USER", 없으면 null */
    public String getTypeFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("type", String.class);
    }

    // public boolean validateToken(String token) {
    //     Logger logger = LoggerFactory.getLogger(this.getClass());

    //     try {
    //         // 구체적인 예외 처리를 위해 try-catch 블록 분리
    //         Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    //         Jws<Claims> claims = Jwts.parserBuilder()
    //                 .setSigningKey(key)
    //                 .build()
    //                 .parseClaimsJws(token);

    //         // 토큰 만료 여부 확인 (선택사항)
    //         Date expiration = claims.getBody().getExpiration();
    //         boolean isExpired = expiration != null && expiration.before(new Date());

    //         if (isExpired) {
    //             logger.warn("JWT 토큰이 만료되었습니다.");
    //             return false;
    //         }

    //         logger.info("JWT 토큰 검증 성공");
    //         return true;

    //     } catch (MalformedJwtException e) {
    //         logger.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
    //     } catch (ExpiredJwtException e) {
    //         logger.error("만료된 JWT 토큰입니다: {}", e.getMessage());
    //     } catch (UnsupportedJwtException e) {
    //         logger.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
    //     } catch (IllegalArgumentException e) {
    //         logger.error("JWT 토큰이 비어 있습니다: {}", e.getMessage());
    //     } catch (Exception e) {
    //         logger.error("JWT 토큰 처리 중 예외 발생: {}", e.getMessage());
    //     }

    //     return false;
    // }

    public boolean validateToken(String token) {
        try {
            // 같은 키 사용
            Jwts.parserBuilder()
                .setSigningKey(key)  // 초기화된 키 사용
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            System.err.println("JWT validation error: " + e.getMessage());
            return false;
        }
    }
}