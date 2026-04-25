package com.example.springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    // JWT密钥
    @Value("${jwt.secret:your-secret-key}")
    private String secret;
    
    // JWT过期时间（毫秒）
    @Value("${jwt.expiration:86400000}")
    private long expiration;
    
    // JWT前缀
    @Value("${jwt.prefix:Bearer}")
    private String prefix;
    
    // JWT请求头
    @Value("${jwt.header:Authorization}")
    private String header;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}