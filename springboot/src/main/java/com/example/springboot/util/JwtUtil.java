package com.example.springboot.util;

import com.example.springboot.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取密钥
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        String secret = jwtConfig.getSecret();
        // 确保密钥足够长（至少256位）
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            // 生成一个安全的密钥
            return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return JWT令牌
     */
    public String generateToken(String userId, String username, String role) {
        // 设置过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + jwtConfig.getExpiration());
        
        // 设置Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        // 生成JWT令牌
        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).get("userId", String.class);
    }

    /**
     * 从令牌中获取角色
     * @param token JWT令牌
     * @return 角色
     */
    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 验证令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

    /**
     * 验证令牌是否有效
     * @param token JWT令牌
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        return !isTokenExpired(token) && getUsernameFromToken(token).equals(username);
    }
}