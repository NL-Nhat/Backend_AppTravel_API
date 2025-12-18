package com.example.travelappapi.config;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;
import com.example.travelappapi.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


// Lớp kiểm tra mọi yêu cầu (Request) gửi đến server xem có đính kèm token JWT hợp lệ hay không
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{  //OncePerRequestFilter -> đảm bảo bộ lọc chỉ chạy duy nhất một lần cho mỗi yêu cầu

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt = getJwtFromRequest(request);

            if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                String tenDangNhap = jwtTokenProvider.getUsernameFromJWT(jwt);
                //Lấy thông tin người dùng từ DB (để đảm bảo dữ liệu mới nhất)
                UserDetails userDetails = authService.loadUserByUsername(tenDangNhap);

                if(userDetails != null && userDetails.isEnabled()) {
                    //Nếu người dùng hợp lệ, nạp vào hệ thống Security
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch(Exception ex) {

        }
        filterChain.doFilter(request, response);
    }

    //Hàm lấy token nguyen ban từ repuest (loại bỏ chữ bearer từ request)
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header có chứa thông tin Bearer không
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Lấy phần chuỗi từ ký tự thứ 7 trở đi để loại bỏ từ "bearer"
        }
        return null;
    }
}
