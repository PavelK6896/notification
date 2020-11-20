package ru.geekbrains.ui.service.bean;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.geekbrains.ui.service.exceptions.JwtAuthenticationException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
public class Token {
    String token;
    String result = "there is no suitable one";

    @Value("${jwt.secret}")
    private String secretKey;

    ObjectMapper objectMapper;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public List<SimpleGrantedAuthority> getRole(String token) {
        Object object = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role");
        List<SimpleGrantedAuthority> listRole = new ArrayList<>();
        listRole.add(new SimpleGrantedAuthority(object.toString()));
        return listRole;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    public void setTokenValidateAndAuthentication(String token) {
        if (validateToken(token)) {
            try {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(getUsername(token), "", getRole(token));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtAuthenticationException e) {
                SecurityContextHolder.clearContext();
                throw new JwtAuthenticationException("JWT token is expired or invalid");
            }
            this.token = token;
        }
    }

}
