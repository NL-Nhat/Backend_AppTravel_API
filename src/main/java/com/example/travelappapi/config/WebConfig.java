package com.example.travelappapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     // Lấy đường dẫn gốc của dự án (Linh hoạt cho cả Windows và Linux Render)
    //     String rootPath = System.getProperty("user.dir");
        
    //     // Đường dẫn đến thư mục chứa ảnh
    //     String uploadPath = "file:" + rootPath + File.separator + "uploads" + File.separator + "avatar" + File.separator;

    //     // Ánh xạ URL /avatar/** vào thư mục vật lý uploads/avatar/
    //     registry.addResourceHandler("/avatar/**")
    //             .addResourceLocations(uploadPath)
    //             .setCachePeriod(0); // Tắt cache để cập nhật ảnh ngay lập tức khi test
    // }
}