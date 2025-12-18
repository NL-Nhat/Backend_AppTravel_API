package com.example.travelappapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.repository.NguoiDungRepository;

@Service
public class AuthService implements UserDetailsService{

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException{
        // Tìm người dùng, nếu không thấy thì ném ra ngoại lệ ngay lập tức
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra trạng thái tài khoản
        if (!user.isEnabled()) {
            throw new DisabledException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin!");
        }

        return user;
    }
    
}
