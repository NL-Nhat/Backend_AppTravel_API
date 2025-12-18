package com.example.travelappapi.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.example.travelappapi.model.NguoiDung;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;


// lop tao token 
@Component
public class JwtTokenProvider {

    // Đọc từ file application.properties
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION;
    
    //phien ban 0.11 tro len 
    private Key getSigningKey(){
        byte[] keyBytes = JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        NguoiDung nguoiDung = (NguoiDung) authentication.getPrincipal();

        Date dateNow = new Date();
        Date ngayHetHan = new Date(JWT_EXPIRATION + dateNow.getTime());

        Map<String, Object> claims = new HashMap<>();
        claims.put("maNguoiDung", nguoiDung.getMaNguoiDung());
        claims.put("hoTen", nguoiDung.getHoTen());
        claims.put("vaiTro", nguoiDung.getVaiTro());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(nguoiDung.getUsername()) //dua ten dang nhap vao
                .setIssuedAt(dateNow)
                .setExpiration(ngayHetHan)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // Ký tên bằng khóa bí mật
                .compact();
        
    }

    // hàm kiểm tra tính hợp lệ của chuỗi jwt
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("token ko dc ho tro");
        } catch (IllegalArgumentException ex) {
            System.out.println("claims trong");
        }
        return false;
    }

    // lấy tên đăng nhập
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // Lấy lại tenDangNhap đã lưu ở Subject
    }
}
